package Game.Server;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Server {
    private ServerSocket ss;
    private int numPlayers;
    private int maxPlayers;

    private Socket p1Socket;
    private Socket p2Socket;
    private ReadFromClient p1RFC;
    private ReadFromClient p2RFC;
    private WriteToClient p1WTC;
    private WriteToClient p2WTC;

    private Vector<Integer> xPos1 = new Vector<>();
    private Vector<Integer> yPos1 = new Vector<>();
    private Vector<Integer> size1 = new Vector<>();
    private Vector<Boolean> vertical1 = new Vector<>();
    private Vector<Integer> xPos2 = new Vector<>();
    private Vector<Integer> yPos2 = new Vector<>();
    private Vector<Integer> size2 = new Vector<>();
    private Vector<Boolean> vertical2 = new Vector<>();

    private int enemy1X, enemy1Y, enemy2X, enemy2Y = -1;

    public Server(int port){

        this.numPlayers = 0;
        this.maxPlayers = 2;
        try {

            ss = new ServerSocket(port);
            System.out.println("Server is ready on port " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void acceptConnections() {
        try {
            while (true) {
                Socket s = ss.accept();
                numPlayers++;
                DataInputStream in = new DataInputStream(s.getInputStream());
                DataOutputStream out = new DataOutputStream(s.getOutputStream());
                System.out.println("Player " + numPlayers + " has connected");
                out.writeInt(numPlayers);
                ReadFromClient rfc = new ReadFromClient(numPlayers, in);
                WriteToClient wtc = new WriteToClient(numPlayers, out);

                if (numPlayers == 1) {
                    p1Socket = s;
                    p1RFC = rfc;
                    p1WTC = wtc;
                    p1RFC.readFirstBoard();

                } else {
                    p2Socket = s;
                    p2RFC = rfc;
                    p2WTC = wtc;
                    p2RFC.readEnemyBoard();
                    p1WTC.sendPlayer2Board();
                    p2WTC.sendPlayer1Board();
                    Thread readThread1 = new Thread(p1RFC);
                    Thread readThread2 = new Thread(p2RFC);
                    readThread1.start();
                    readThread2.start();


                    Thread writeThread1 = new Thread(p1WTC);
                    Thread writeThread2 = new Thread(p2WTC);
                    writeThread1.start();
                    writeThread2.start();
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private class ReadFromClient implements Runnable {
        private int playerID;
        private DataInputStream dataIn;

        public ReadFromClient(int pid, DataInputStream in) {
            playerID = pid;
            dataIn = in;
            System.out.println("RFC" + playerID);
        }

        public void run() {
            while (true) {
                try {
                    if (playerID == 1) {
                        enemy2X = dataIn.readInt();
                        enemy2Y = dataIn.readInt();
                        System.out.println("Player " + playerID + " shoot " + enemy2X + ",  " + enemy2Y);
                        p2WTC.sendShoot();
                    } else {
                        enemy1X = dataIn.readInt();
                        enemy1Y = dataIn.readInt();
                        System.out.println("Player " + playerID + " shoot " + enemy1X + ",  " + enemy1Y);
                        p1WTC.sendShoot();

                    }
                } catch (IOException e) {
                    System.out.println("IOException FROM RFC");
                }
            }
        }

        public void readFirstBoard() throws IOException {
            //x location
            System.out.println("Read first board");
            for (int i = 0; i < 7; i++) {
                int x = dataIn.readInt();
                xPos1.add(x);
            }
            //y location
            for (int i = 0; i < 7; i++) {
                int y = dataIn.readInt();
                yPos1.add(y);
            }
            //size
            for (int i = 0; i < 7; i++) {
                int s = dataIn.readInt();
                size1.add(s);
            }
            for (int i = 0; i < 7; i++) {
                boolean b = dataIn.readBoolean();
                vertical1.add(b);
            }
            System.out.println("Received location from player 1");

        }

        public void readEnemyBoard() throws IOException {
            //x location
            System.out.println("Read second board");
            for (int i = 0; i < 7; i++) {
                xPos2.add(dataIn.readInt());
            }
            //y location
            for (int i = 0; i < 7; i++) {
                yPos2.add(dataIn.readInt());
            }
            //size
            for (int i = 0; i < 7; i++) {
                int s = dataIn.readInt();
                size2.add(s);
            }
            for (int i = 0; i < 7; i++) {
                boolean b = dataIn.readBoolean();
                vertical2.add(b);
            }
            System.out.println("Received location from player 2");

        }
    }

    private class WriteToClient implements Runnable {
        private int playerID;
        private DataOutputStream dataOut;

        public WriteToClient(int pid, DataOutputStream out) {
            playerID = pid;
            dataOut = out;
            System.out.println("WTC" + playerID);
        }

        public void run() {

        }

        public void sendShoot() {
            try {
                if (playerID == 1) {
                    System.out.println("Player " + playerID + " got hit");
                    dataOut.writeInt(enemy1X);
                    dataOut.writeInt(enemy1Y);
                } else {
                    System.out.println("Player " + playerID + " got hit");

                    dataOut.writeInt(enemy2X);
                    dataOut.writeInt(enemy2Y);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void sendPlayer1Board() throws IOException {
            for (int i = 0; i < xPos1.size(); i++) {
                dataOut.writeInt(xPos1.get(i));
            }
            for (int i = 0; i < yPos1.size(); i++) {
                dataOut.writeInt(yPos1.get(i));
            }
            for (int i = 0; i < size1.size(); i++) {
                dataOut.writeInt(size1.get(i));
            }
            for (int i = 0; i < vertical1.size(); i++) {
                dataOut.writeBoolean(vertical1.get(i));
            }
            System.out.println("Sending player 1 board");
        }

        public void sendPlayer2Board() throws IOException {
            for (int i = 0; i < xPos2.size(); i++) {
                dataOut.writeInt(xPos2.get(i));
            }
            for (int i = 0; i < yPos2.size(); i++) {
                dataOut.writeInt(yPos2.get(i));
            }
            for (int i = 0; i < size2.size(); i++) {
                dataOut.writeInt(size2.get(i));
            }
            for (int i = 0; i < vertical2.size(); i++) {
                dataOut.writeBoolean(vertical2.get(i));
            }
            System.out.println("Sending player 2 board");

        }
    }
//
//    public static void main(String[] args) {
//        RunServer s = new RunServer();
//        s.acceptConnections();
//    }
}
