package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextArea;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller {
    static Stage stage;
    Lexer lexer = new Lexer();

    // Kung gagamit kayo ng ubject galing sa UI, iname niyo muna yung ID niya.
    // After that, reference niyo siya gamit @FXML para maaaccess siya sa code.
    // Example: Yung TextFlow sa code na Pane. Nireference ko muna siya gamit @FXML
    // at nilagay ginamit yung ID niya as variable name (in this case, "loader")
    @FXML
    private TextArea codePane;
    @FXML
    private TableView<Token> lexTable;
    @FXML
    private TableColumn<Token, String> lexCol;
    @FXML
    private TableColumn<Token, String> classCol;

    @FXML
    private TextFlow textOutput;

    // When load button is clicked, it will open a new windows
    // and lets the user choose a lol code file
    @FXML
    public void openLOLFile() {
        this.lexTable.getItems().clear();
        if(this.lexer.getTokens() != null) {
            this.lexer.getTokens().clear();
        }
        // For choosing the file
        if(this.stage == null) {
            System.out.println("Stage is null");
        }
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
            //lexer.printTokens();
            setLexemeTable();

        }
    }

    // This adds the tokens from the "tokens" ObservableList in the lexer to the lexeme table
    // found in the UI
    void setLexemeTable() {
        this.lexCol.setCellValueFactory(new PropertyValueFactory<Token, String>("lexeme"));
        this.classCol.setCellValueFactory(new PropertyValueFactory<Token, String>("type"));
        this.lexTable.setItems(this.lexer.getTokens());
    }

    // This references the stage from the main class
    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
