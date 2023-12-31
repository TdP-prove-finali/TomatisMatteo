package javacode.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javacode.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class FXMLControllerTwo {

    private Model model;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button fireButton;

    @FXML
    private Button quitbutton;

    @FXML
    private ImageView mapImg;

    @FXML
    private Text text;

    @FXML
    void goBack(ActionEvent event) {
    	 try {
         	Stage stage = (Stage) quitbutton.getScene().getWindow();
         	stage.close();
            Stage primaryStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("..\\..\\resources\\pag-1.fxml"));
            Parent root = loader.load();
            FXMLControllerOne controller = loader.getController();
            controller.setModel(this.model);
            Scene scene = new Scene(root, 700, 500);
            scene.getRoot().setStyle("-fx-font-family: 'Verdana'");
            primaryStage.setTitle("Simulazione");
            primaryStage.setScene(scene);
            primaryStage.show();
         } catch (IOException e) {
             e.printStackTrace(); // Print the exception to identify any issues.
         }
    }

    @FXML
    void startFire(ActionEvent event) {
        this.text.setText("Aggiornamento in corso...");

        WritableImage updatedImage = this.model.spreadFire(this.mapImg);

        if (updatedImage != null) {
            this.mapImg.setImage(updatedImage);
            this.text.setText("Aggiornamento completato");
        } else {
            this.text.setText("Si è verificato un errore nell'aggiornamento");
        }

        this.fireButton.setDisable(true);
    }

    @FXML
    void initialize() {
        assert fireButton != null : "fx:id=\"fireButton\" was not injected: check your FXML file 'pag-2.fxml'.";
        assert mapImg != null : "fx:id=\"mapImg\" was not injected: check your FXML file 'pag-2.fxml'.";
        assert quitbutton != null : "fx:id=\"quitbutton\" was not injected: check your FXML file 'pag-2.fxml'.";
        assert text != null : "fx:id=\"text\" was not injected: check your FXML file 'pag-2.fxml'.";
    }

    public void setModel(Model model) {
        this.model = model;
    }

}

