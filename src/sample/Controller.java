package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.TableColumn;

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
    private TextFlow codePane;

    @FXML
    private TableView<Token> lexemeTable;

    @FXML
    private TableColumn<Token, String> lexemeCol;
    @FXML
    private TableColumn<Token, String> typeCol;

    // When load button is clicked, it will open a new windows
    // and lets the user choose a lol code file
    @FXML
    public void openLOLFile() {
        // For choosing the file
        codePane.getChildren().clear();
        lexer.setCodePane(codePane);
        FileChooser fc = new FileChooser();
        fc.setTitle("Open LOLCode file");
        // Opens a new window
        File selectedFile = fc.showOpenDialog(stage);

        // Passing the table properties
        lexer.setLexemeTable(lexemeTable, lexemeCol, typeCol);

        // Passing the LOL code to the lexer
        lexer.setLolFile(selectedFile);
        lexer.readLines();

    }

    // This references the stage from the main class
    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
