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
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.Model;

public class FXMLController {
	
	private Model model;

	 @FXML
	    private ResourceBundle resources;

	    @FXML
	    private URL location;

	    @FXML
	    private TextField pInput;

	    @FXML
	    private TextField rainInput;

	    @FXML
	    private Button startButton;

	    @FXML
	    private TextField temperatureInput;

	    @FXML
	    private TextField windInput;
	    
    @FXML
    void startSim(ActionEvent event) {
        try {
        	Stage stage = (Stage) startButton.getScene().getWindow();
        	stage.close();
            Stage primaryStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/pag-2.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getRoot().setStyle("-fx-font-family: 'Verdana'");
            primaryStage.setTitle("Simulazione");
            
            ImageView imgView = (ImageView) scene.lookup("#mapImg");
            
            imgView.setImage(model.getMap());
            
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace(); // Print the exception to identify any issues.
        }
    }

    @FXML
    void initialize() {
        assert pInput != null : "fx:id=\"pInput\" was not injected: check your FXML file 'prova-tesi.fxml'.";
        assert rainInput != null : "fx:id=\"rainInput\" was not injected: check your FXML file 'prova-tesi.fxml'.";
        assert startButton != null : "fx:id=\"startButton\" was not injected: check your FXML file 'prova-tesi.fxml'.";
        assert temperatureInput != null : "fx:id=\"temperatureInput\" was not injected: check your FXML file 'prova-tesi.fxml'.";
        assert windInput != null : "fx:id=\"windInput\" was not injected: check your FXML file 'prova-tesi.fxml'.";

    }

    public void setModel(Model m) {
    	this.model = m;
    }

}
