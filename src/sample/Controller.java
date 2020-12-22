package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
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

    @FXML
    private TableView<Variable> symbolTable;
    @FXML
    private TableColumn<Variable, String> identCol;
    @FXML
    private TableColumn<Variable, String> valCol;

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
            lexer.setTextOutput(textOutput);
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

    @FXML
    void clickExecute() {
        this.symbolTable.getItems().clear();
        System.out.print("Clicked Execute");
        SyntaxAnalyzer syntax = this.lexer.getSyntax();
        syntax.setTextOutput(textOutput);

        int ind = 0;
        for(ArrayList<Token> line : syntax.getLines()) {
            int lineNum = this.lexer.getLineNumbers().get(ind);
            syntax.checkSyntax(line, lineNum);
            if(syntax.getSyntaxErrors().size() > 0) {
                printSyntaxErrors(syntax);
                break;
            }
            ind++;
        }
        if(syntax.getSyntaxErrors().size() == 0)
            addToSymbolTable(syntax);

        syntax.getSyntaxErrors().clear();
        syntax.getLineErrors().clear();
    }

    // This references the stage from the main class
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    void printSyntaxErrors(SyntaxAnalyzer syntax) {
        for(int i=0;i<syntax.getSyntaxErrors().size();i++) {
            String err = syntax.getLineErrors().get(i) + syntax.getSyntaxErrors().get(i);
            Text t = new Text(err);
            t.setFill(Color.RED);
            t.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
            this.textOutput.getChildren().add(t);
        }
    }

    void addToSymbolTable(SyntaxAnalyzer syntax) {
        for(Variable v: syntax.getVariables()) {
            System.out.println(v.getVarName());
        }

        this.identCol.setCellValueFactory(new PropertyValueFactory<Variable, String>("varName"));
        this.valCol.setCellValueFactory(new PropertyValueFactory<Variable, String>("value"));
        this.symbolTable.setItems(syntax.getVariables());
    }
}