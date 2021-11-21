package Game.View;

import Game.Server.Server;
import Game.ShipWreck;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.*;
import java.util.Collections;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;

public class MainController {
    public TextField host, port;
    public TextArea textAr;

    public Connection getConnection() {
        Connection conn = null;
        try {
            String JDBC_DRIVER = "com.mysql.jdbc.Driver";
            String DB_URL = "jdbc:mysql://26.174.20.128:3306/gamedb";
            String USER = "root";
            String PASS = "password";
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Connecting to a selected database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected database successfully...");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return conn;
    }

    public void handleCreateRoom(ActionEvent event) throws Exception {
        Stage stageTheEventSourceNodeBelongs = (Stage) ((Node) event.getSource()).getScene().getWindow();
        new Thread(new Runnable() {   // new thread for parallel execution
            public void run() {
                Server s = new Server(Integer.parseInt(port.getText()));
                s.acceptConnections();
            }
        }).start();
        ShipWreck game = new ShipWreck();
        Stage newstage = new Stage();
        game.start(newstage);
        game.set("localhost", port.getText(), stageTheEventSourceNodeBelongs.getTitle());
    }

    public void handleJoinRoom(ActionEvent event) throws Exception {
        Stage stageTheEventSourceNodeBelongs = (Stage) ((Node) event.getSource()).getScene().getWindow();
        ShipWreck game = new ShipWreck();
        Stage newstage = new Stage();
        game.start(newstage);
        game.set(host.getText(), port.getText(), stageTheEventSourceNodeBelongs.getTitle());
    }

    public void getNetworkIPs() {
        textAr.setText("");


        String hostName = null;
        try {
            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface netint : Collections.list(nets)) {

                Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
                for (InetAddress inetAddress : Collections.list(inetAddresses)) {
                    textAr.appendText(inetAddress + "\n");
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void handleMatchHistory() {
        textAr.setText("");
        try {
            Connection conn = getConnection();
            String sql = "SELECT * FROM gamedb.match";
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery(sql);
            while (rs.next()) {
                long millis = Long.parseLong(rs.getString(3));
                long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
                textAr.appendText("Match Id: " + rs.getInt(1) + " , Winner: " + rs.getString(4) + " ,Time to beat: " + seconds + "\n");
            }
            rs.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
