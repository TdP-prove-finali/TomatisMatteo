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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class FXMLControllerOne {
    private Model model = new Model();

	@FXML
    private ResourceBundle resources;

    @FXML
    private ImageView rainImg;

    @FXML
    private URL location;

    @FXML
    private TextField rainInput;

    @FXML
    private Button startButton;

    @FXML
    private ImageView tempImg;

    @FXML
    private TextField temperatureInput;
    
    @FXML
    private ImageView windImg;

    @FXML
    private TextField windInput;
	    
    @FXML
    void startSim(ActionEvent event) {
                
        try{

            double temp = Double.parseDouble(this.temperatureInput.getText());
            double rain = Double.parseDouble(this.rainInput.getText());
            double wind = Double.parseDouble(this.windInput.getText());

            double[] input = {temp, rain, wind};  

            model.setBurnedArea(input);

            /* 
            double vegDensity = Double.parseDouble(this.pInput.getText());

            if(vegDensity <= 1 && vegDensity > 0) {
                this.model.setVegDensity(vegDensity);
            } else {
                mostraAlertErrore("Si è verificato un errore", "Controllare di aver rispettato i valori massimi e mininmi della densita' di vegetazione");
                return;
            }
            */

            try {
                Stage stage = (Stage) startButton.getScene().getWindow();
                stage.close();
                Stage primaryStage = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("..\\..\\resources\\pag-2.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root, 700, 500);
                scene.getRoot().setStyle("-fx-font-family: 'Verdana'");
                primaryStage.setTitle("Simulazione");
                
                FXMLControllerTwo controller = loader.getController();
                controller.setModel(this.model);
                ImageView imgView = (ImageView) scene.lookup("#mapImg");
                imgView.setImage(model.getMap());
                
                primaryStage.setScene(scene);
                primaryStage.show();
            } catch (IOException e) {
                e.printStackTrace(); // Print the exception to identify any issues.
            }

        } catch(NumberFormatException n) {
            mostraAlertErrore("Si è verificato un errore", "Controllare di aver inserito correttamente i dati in input");
        }
    }

    // Metodo per mostrare un Alert di errore
    @FXML
    private void mostraAlertErrore(String titolo, String messaggio) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(titolo);
        alert.setContentText(messaggio);

        // Mostriamo e attendiamo che l'utente prema il pulsante "OK" per chiudere l'Alert
        alert.showAndWait();
    }


    @FXML
    void initialize() {
        assert rainImg != null : "fx:id=\"rainImg\" was not injected: check your FXML file 'pag-1.fxml'.";
        assert rainInput != null : "fx:id=\"rainInput\" was not injected: check your FXML file 'pag-1.fxml'.";
        assert startButton != null : "fx:id=\"startButton\" was not injected: check your FXML file 'pag-1.fxml'.";
        assert tempImg != null : "fx:id=\"tempImg\" was not injected: check your FXML file 'pag-1.fxml'.";
        assert temperatureInput != null : "fx:id=\"temperatureInput\" was not injected: check your FXML file 'pag-1.fxml'.";
        assert windImg != null : "fx:id=\"windImg\" was not injected: check your FXML file 'pag-1.fxml'.";
        assert windInput != null : "fx:id=\"windInput\" was not injected: check your FXML file 'pag-1.fxml'.";


    }

    public void setModel(Model m) {
    	this.model = m;
    }
}
