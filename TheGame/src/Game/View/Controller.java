package Game.View;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class Controller {
    public Button login;
    public TextField userField;
    public Text notification,notilogin;
    public PasswordField passwordField,passwordField2;
    public String usern;
    public Connection getConnection(){
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
    public void initialize(){

    }
    public void handleLogin(ActionEvent event){
            String username = userField.getText();
            String password = passwordField.getText();
            try {
                Connection conn = getConnection();
                String sql = "SELECT * FROM user WHERE username = ?  AND password = ? ;";
                PreparedStatement stm = conn.prepareStatement(sql);
                stm.setString(1,username);
                stm.setString(2,password);
                ResultSet rs = stm.executeQuery();
              if (rs.next()){
                  notilogin.setText("LOGIN SUCCESS");
                  usern = username;
                  handleMainScene(event,username);
              }
                rs.close();
            } catch (SQLException | IOException throwables) {
                notilogin.setText("Error");
                throwables.printStackTrace();
            }
        notilogin.setText("Wrong password or username");
    }
    public void handleMainScene(ActionEvent event,String name) throws IOException {
        Stage stageTheEventSourceNodeBelongs = (Stage) ((Node)event.getSource()).getScene().getWindow();
        Parent loader = FXMLLoader.load(getClass().getResource("mainview.fxml"));
//        stageTheEventSourceNodeBelongs.hide();
        stageTheEventSourceNodeBelongs.getScene().setRoot(loader);
        stageTheEventSourceNodeBelongs.setTitle(name);
    }
    public void handleRegisterScene(ActionEvent event) throws IOException {
        Stage stageTheEventSourceNodeBelongs = (Stage) ((Node)event.getSource()).getScene().getWindow();
        Parent register = FXMLLoader.load(getClass().getResource("register.fxml"));

//        stageTheEventSourceNodeBelongs.hide();
        stageTheEventSourceNodeBelongs.getScene().setRoot(register);
    }
    public void handleRegister(){
        Connection conn = null;

        try {
            conn = getConnection();
            String username = userField.getText();
            String password = passwordField.getText();
            String password2 = passwordField2.getText();
            if (password != password2) {
                notification.setText("Password confirmation is wrong");
            }
            String sql = "INSERT INTO user (username,password,scores,online) VALUES (?,?,?,?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.setInt(3, 0);
            preparedStatement.setInt(4, 0);
            int result = preparedStatement.executeUpdate();
            if (result==1){
                notification.setText("Register successfully");
            }
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            notification.setText("Error");
        }

    }
    public void handleReturn(ActionEvent event) throws IOException{
        Stage stageTheEventSourceNodeBelongs = (Stage) ((Node)event.getSource()).getScene().getWindow();
        Parent login = FXMLLoader.load(getClass().getResource("login.fxml"));

//        stageTheEventSourceNodeBelongs.hide();
        stageTheEventSourceNodeBelongs.getScene().setRoot(login);
    }
}
