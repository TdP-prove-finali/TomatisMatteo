package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import model.Model;

public class FXMLControllerbis {
	
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button quitbutton;

    @FXML
    void goBack(ActionEvent event) {
    	 try {
         	Stage stage = (Stage) quitbutton.getScene().getWindow();
         	stage.close();
             Stage primaryStage = new Stage();
             FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/pag-1.fxml"));
             Parent root = loader.load();
             Scene scene = new Scene(root);
             scene.getRoot().setStyle("-fx-font-family: 'Verdana'");
             primaryStage.setTitle("Simulazione");
             primaryStage.setScene(scene);
             primaryStage.show();
         } catch (IOException e) {
             e.printStackTrace(); // Print the exception to identify any issues.
         }
    }

    @FXML
    void initialize() {
        assert quitbutton != null : "fx:id=\"quitbutton\" was not injected: check your FXML file 'sec-page.fxml'.";

    }

}