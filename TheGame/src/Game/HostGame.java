package Game;

import Game.Server.Server;
import javafx.application.Application;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import javafx.stage.Stage;
public class HostGame extends Application
{
    private Stage PrimaryStage;
    private Label status = new Label("");
    private Parent createContent(){
        BorderPane root = new BorderPane();
        root.setPrefSize(300, 50);
        HBox main = new HBox(20);
        Label portLB = new Label("Port");
        TextField portField = new TextField("");
        Button start = new Button("Start");

        EventHandler<ActionEvent> startEvent = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                 Server s = new Server(Integer.parseInt(portField.getText()));
                 status.setText("Server is ready on port " + portField.getText());
            }
        };
        start.setOnAction(startEvent);

        main.getChildren().addAll(portLB, portField,start);
        root.setCenter(main);
        root.setBottom(status);
        return root;
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        this.PrimaryStage = primaryStage;
        Scene scene = new Scene(createContent());
        PrimaryStage.setTitle("Start a server");
        PrimaryStage.setScene(scene);
        PrimaryStage.setResizable(false);
        PrimaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}