package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class Controller {
    Stage stage;
    Lexer lexer = new Lexer();

    // ---- HPPL -----
    // Kung gagamit kayo ng ubject galing sa UI, iname niyo muna yung ID niya.
    // After that, reference niyo siya gamit @FXML para maaaccess siya sa code.
    // Example: Yung TextFlow sa code na Pane. Nireference ko muna siya gamit @FXML
    // at nilagay ginamit yung ID niya as variable name (in this case, "loader")
    @FXML
    VBox vbox;

    @FXML
    private Button loader;

    @FXML
    private TextArea codePane;

    // When load button is clicked, it will open a new windows
    // and lets the user choose a lol code file
    @FXML
    public void openLOLFile() {
        // For choosing the file
        lexer.setCodePane(codePane);
        FileChooser fc = new FileChooser();
        fc.setTitle("Open LOLCode file");

        // For the extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("LOL files (*.lol)", "*.lol");
        fc.getExtensionFilters().add(extFilter);

        // Opens a new window
        File selectedFile = fc.showOpenDialog(stage);

        // Passing the LOL code to the lexer
        if(selectedFile != null) {
            codePane.clear();
            lexer.setLolFile(selectedFile);
            lexer.readLines();
        }
    }

    //for execute button(?)
    @FXML
    private Button execute;

    // This references the stage from the main class
    public void setStage(Stage stage) {
        this.stage = stage;
    }
}