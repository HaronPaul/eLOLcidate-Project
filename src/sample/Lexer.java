package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.control.TableView;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.control.TableColumn;

public class Lexer {
    File lolFile;
    ArrayList<String> keywords; // This is where the keywords are stored
    ArrayList<String> identifiers;
    ObservableList<Token> tokens;

    TextFlow codePane;
    TableView lexemeTable;

    // Setting the TableColumns
    TableColumn<Token, String> lexemeCol;
    TableColumn<Token, String> typeCol;

    // Constructor for Lexer class
    public Lexer() {
        this.keywords = new ArrayList<String>();
        this.identifiers = new ArrayList<String>();
        this.tokens = FXCollections.observableArrayList();
    }

    public void readLines() {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(lolFile));
            String line = reader.readLine();
            while(line != null) {

                // Creates a Text object. The text object is then added to the
                // Textflow object
                Text t = new Text(line + "\n");
                codePane.getChildren().add(t);

                // When line is blank (No statements), it will just continue to next line
                if(line.length() == 0) {
                    line = reader.readLine();
                    continue;
                }
                else {
                    matchLexemes(line);
                }
                line = reader.readLine();
            }

        } catch (IOException e){
            e.printStackTrace();
        }
        addToTable();
    }

    // Regex part. This is where the keywords, strings,
    // and identifiers are matched
    void matchLexemes(String line) {
        Pattern keyword = Pattern.compile("(^I|[\\t\\s]I)\\sHAS\\s(A$|A\\b)|(^K|[\\t\\s]K)THXBY(E$|E\\b)|(^V|[\\t\\s]V)ISIBL(E$|E\\b)|(^G|[\\t\\s]G)IMME(H$|H\\b)|(^B|[\\t\\s]B)T(W$|W\\b)|(^H|[\\t\\s]H)A(I$|I\\b)|(^Q|[\\t\\s]Q)UOSHUNT\\sO(F$|F\\b)|(^P|[\\t\\s]P)RODUKT\\sO(F$|F\\b)|(^D|[\\t\\s]D)IFF\\sO(F$|F\\b)|(^S|[\\t\\s]S)UM\\sO(F$|F\\b)|(^B|[\\t\\s]B)IGGR\\sO(F$|F\\b)|(^S|[\\t\\s]S)MALLR\\sO(F$|F\\b)|(^M|[\\t\\s]M)OD\\sO(F$|F\\b)|(^A|[\\t\\s]A)(N$|N\\b)");
        Matcher m = keyword.matcher(line);
        while(m.find()) {
            String matched = m.group().trim();
//            System.out.println(matched);
            addToken(matched);
        }
    }

    void addToken(String matched) {
        if(matched.equals("HAI") || matched.equals("KTHXBYE")) {
            tokens.add(new Token(matched, "Delimiter"));
        }
        else if(matched.equals("SUM OF")|| matched.equals("DIFF OF")||matched.equals("PRODUKT OF")||matched.equals(
                "QUOSHUNT OF")|| matched.equals("MOD OF")) {
            tokens.add(new Token(matched, "Arithmetic operator"));
        }
        else if(matched.equals("BIGGR OF")||matched.equals("SMALLR OF")) {
            tokens.add(new Token(matched, "Comparison operator"));
        }
    }

    void addToTable() {
        for(Token token: tokens) {
            System.out.println(token.getLexeme() + " " + token.getType());
        }
        lexemeCol.setCellValueFactory(new PropertyValueFactory<>("Lexeme"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("Classification"));
        lexemeTable.setItems(this.tokens);
    }

    // ---------------------------------------------------------------------------------------------
    // Setters for the variables passed from the Controller class
    void setLolFile(File file) {
        this.lolFile = file;
    }

    void setCodePane(TextFlow tf) {
        this.codePane = tf;
    }
    void setLexemeTable(TableView<Token> lexemeTable, TableColumn col1, TableColumn col2) {
        this.lexemeTable = lexemeTable;
        col1 = new TableColumn("Lexemes");
        this.lexemeCol = col1;

        col2 = new TableColumn("Classification");
        this.typeCol = col2;
    }

}
