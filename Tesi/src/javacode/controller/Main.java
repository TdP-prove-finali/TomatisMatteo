package javacode.controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("..\\..\\resources\\pag-1.fxml"));
        primaryStage.setTitle("Your Application Title");
        primaryStage.setScene(new Scene(root, 700, 500)); // Imposta la dimensione della finestra come desiderato
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
