package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Controller {
    Stage stage;
    @FXML
    VBox vbox;
    @FXML
    private Button loader;

    // When load button is clicked, it will open a new windows
    // and lets the user choose a lol code file
    @FXML
    public void openLOLFile() {
        // For choosing the file
        FileChooser fc = new FileChooser();
        fc.setTitle("Open LOLCode file");
        fc.showOpenDialog(stage);
        
        System.out.println("Opening file....");
    }

    // This references the stage from the main class
    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
