package Game;


import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class Main extends Application {
    Stage PrimaryStage;
    @Override
    public void start(Stage primaryStage) throws Exception {
        this.PrimaryStage = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("View/login.fxml"));
        Scene scene =  new Scene(root);
        PrimaryStage.setScene(scene);
        PrimaryStage.setTitle("Battleship");
        PrimaryStage.setResizable(false);
        PrimaryStage.show();
        PrimaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                System.exit(0);
            }
        });
    }
    public static void main(String[] args) {
        launch(args);
    }
}
