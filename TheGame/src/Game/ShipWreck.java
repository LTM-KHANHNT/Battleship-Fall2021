package Game;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

import Model.Board;
import Model.Cell;
import Model.Ship;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ShipWreck extends Application {
    //PrimaryStage
    private Stage PrimaryStage;


    //Socket
    private Socket socket;
    private int playerId;
    private Text connect = new Text();
    private Button play = new Button();
    private ReadFromServer rfs;
    private WriteToServer wts;
    private Vector<Integer> xPos1 = new Vector<>();
    private Vector<Integer> yPos1 = new Vector<>();
    private Vector<Integer> size1 = new Vector<>();
    private Vector<Boolean> vertical1 = new Vector<>();
    private Vector<Integer> xPos2 = new Vector<>();
    private Vector<Integer> yPos2 = new Vector<>();
    private Vector<Integer> size2 = new Vector<>();
    private Vector<Boolean> vertical2 = new Vector<>();

    private boolean running = false;
    private Board enemyBoard, playerBoard;
    private int enemyX, enemyY = -1;
    private int shipsToPlace = 7;
    private int shipCell = 3*2 + 2*3 + 2;
    private int yourBoardX, yourBoardY = -1;
    private boolean enemyTurn, ourTurn = false;

    private Random random = new Random();

    private Parent createContent() {
        BorderPane root = new BorderPane();
        root.setPrefSize(1000, 500);
        VBox vBox = new VBox(20);
        VBox grAndTr = new VBox(20);
        Text greeting = new Text("Welcome to battleship, you have in total 7 ships, 2 ships 3 blocks" +
                " 3 ships 2 blocks  and 2 ship 1 block");
        Text tutorial = new Text("Let'start by placing your ship vertical with left click, horizontal with right click and then START THE GAME!");
        grAndTr.getChildren().addAll(greeting,tutorial);
        root.setTop(grAndTr);
        play = new Button("Waiting to place your ship");
        EventHandler<ActionEvent> playEvent = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                connectToServer();
            }
        };

        play.setOnAction(playEvent);


        vBox.getChildren().addAll(play, connect);


        enemyBoard = new Board(true, event -> {
            if (!running)
                return;
            if (!ourTurn) return;
            Cell cell = (Cell) event.getSource();

            if (cell.wasShot)
                return;
            enemyX = cell.x;
            enemyY = cell.y;
            wts.sendShoot();
            ourTurn = cell.shoot();

            if (enemyBoard.shipNumber == 0) {
                ImageView victory = new ImageView(new Image("Assets/gameOver/Victory.png"));
                root.setCenter(victory);
            }

        });


        playerBoard = new Board(false, event -> {

            Cell cell = (Cell) event.getSource();

            if (running) {

                return;
            }


            if (shipsToPlace > 5) {
                Ship s = new Ship(3, event.getButton() == MouseButton.PRIMARY);
                if (playerBoard.placeShip(s, cell.x, cell.y)) {
                    xPos1.add(cell.x);
                    yPos1.add(cell.y);
                    size1.add(3);
                    vertical1.add(s.isVertical());
                    shipsToPlace--;
                }
            } else if (shipsToPlace <= 5 && shipsToPlace > 2) {
                Ship s = new Ship(2, event.getButton() == MouseButton.PRIMARY);
                if (playerBoard.placeShip(s, cell.x, cell.y)) {
                    xPos1.add(cell.x);
                    yPos1.add(cell.y);
                    size1.add(2);
                    vertical1.add(s.isVertical());
                    shipsToPlace--;
                }

            } else {
                Ship s = new Ship(1, event.getButton() == MouseButton.PRIMARY);
                if (playerBoard.placeShip(s, cell.x, cell.y)) {
                    xPos1.add(cell.x);
                    yPos1.add(cell.y);
                    size1.add(1);
                    vertical1.add(s.isVertical());
                    if (--shipsToPlace == 0) {

                        startGame();
                        play.setText("Play NOW");
                        play.setDisable(false);
                    }
                }
            }


        });
        VBox yourBoardwText = new VBox(20, playerBoard, new Text("Your board"));
        yourBoardwText.setAlignment(Pos.CENTER);
        VBox enemyBoardwText = new VBox(20, enemyBoard, new Text("Enemy board"));
        enemyBoardwText.setAlignment(Pos.CENTER);

        vBox.setAlignment(Pos.CENTER_LEFT);
        HBox hBox = new HBox(50, yourBoardwText, enemyBoardwText, vBox);
        hBox.setAlignment(Pos.CENTER);
        root.setCenter(hBox);
        return root;
    }


    private void startGame() {
        running = true;
    }

    //Server setup
    private void connectToServer() {
        try {
            play.setDisable(true);
            socket = new Socket("localhost", 45371);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            playerId = in.readInt();

            System.out.println("You are player " + playerId);
            if (playerId == 1) {
                connect.setText("Waiting for player 2 to connect");
                ourTurn = true;
            }
            wts = new WriteToServer(out);
            rfs = new ReadFromServer(in);

            wts.sendStartBoard();

        } catch (IOException ex) {
            System.out.println(ex.getStackTrace());
            connect.setText("Error connecting to server");
        }
    }

    private class ReadFromServer implements Runnable {
        private DataInputStream in;

        public ReadFromServer(DataInputStream in) {
            this.in = in;
            System.out.println("Read from server created");
        }

        public void run() {
            try {
                waitForReceivingEnemyBoard();

                while (true) {
                    yourBoardX = in.readInt();
                    yourBoardY = in.readInt();
                    System.out.println("Got hit " + yourBoardX + ", " + yourBoardY);


                    if (yourBoardX > -1 && yourBoardY > -1) {
                        Cell cell = playerBoard.getCell(yourBoardX, yourBoardY);
                        ourTurn = !cell.shoot();
                        if (!ourTurn) shipCell--;
                        System.out.println(shipCell);
                        if (shipCell == 0) {
                            BorderPane root = new BorderPane();
                            root.setPrefSize(1000, 500);
                                ImageView gameOver = new ImageView(new Image("Assets/gameOver/gameOver.png",800,100,false,false));

                                root.setCenter(gameOver);
                            PrimaryStage.getScene().setRoot(root);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        public void waitForReceivingEnemyBoard() throws IOException {
            System.out.println("Initialzing enemy board");

            for (int i = 0; i < xPos1.size(); i++) {
                xPos2.add(in.readInt());
            }
            for (int i = 0; i < yPos1.size(); i++) {
                yPos2.add(in.readInt());
            }
            for (int i = 0; i < size1.size(); i++) {
                size2.add(in.readInt());
            }
            for (int i = 0; i < vertical1.size(); i++) {
                vertical2.add(in.readBoolean());
            }

            int type = 6;
            System.out.println("Set board " + size2.size() + " " + vertical2.size());
            //testing only
            while (type >= 0) {

                if (enemyBoard.placeShip(new Ship(size2.get(type), vertical2.get(type)), xPos2.get(type), yPos2.get(type))) {
                    type--;
                }
            }
        }
    }

    private class WriteToServer implements Runnable {
        private DataOutputStream out;

        public WriteToServer(DataOutputStream out) {
            this.out = out;
            System.out.println("Write to server created");
        }

        public void run() {
//            while (true) {
//                try {
//                    out.writeInt(enemyX);
//                    out.writeInt(enemyY);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
        }

        public void sendShoot() {
            try {
                System.out.println("Shoot " + enemyX + ", " + enemyY);
                out.writeInt(enemyX);
                out.writeInt(enemyY);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void sendStartBoard() throws IOException {
            System.out.println("Sending current ship location to Server ");
            for (int i = 0; i < xPos1.size(); i++) {
                out.writeInt(xPos1.get(i));
            }
            for (int i = 0; i < yPos1.size(); i++) {
                out.writeInt(yPos1.get(i));
            }
            for (int i = 0; i < size1.size(); i++) {
                out.writeInt(size1.get(i));
            }
            for (int i = 0; i < vertical1.size(); i++) {
                out.writeBoolean(vertical1.get(i));
            }
            Thread readThread = new Thread(rfs);
            Thread writeThread = new Thread(wts);
            readThread.start();
            writeThread.start();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.PrimaryStage = primaryStage;
        Scene scene = new Scene(createContent());
        PrimaryStage.setTitle("Battleship");
        PrimaryStage.setScene(scene);
        PrimaryStage.setResizable(false);
        PrimaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}