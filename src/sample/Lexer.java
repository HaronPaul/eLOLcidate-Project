package sample;

import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    File lolFile;
    ArrayList<String> keywords; // This is where the keywords are stored
    ArrayList<String> identifiers;
    TextFlow codePane;

    // Constructor for Lexer class
    public Lexer() {
        this.keywords = new ArrayList<String>();
        this.identifiers = new ArrayList<String>();
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
                    matchStrings(line);
                }
                line = reader.readLine();
            }

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    // Regex part. This is where the keywords, strings,
    // and identifiers are matched
    void matchStrings(String line) {
        Pattern keyword = Pattern.compile("(^I|[\\t\\s]I)\\sHAS\\s(A$|A\\b)|(^K|[\\t\\s]K)THXBY(E$|E\\b)");
        Matcher m = keyword.matcher(line);
        while(m.find()) {
            String matched = m.group();
            System.out.println(matched);
        }
    }


    // Setter for the LOL code passed from the Controller class
    public void setLolFile(File file) {
        this.lolFile = file;
    }

    void setCodePane(TextFlow tf) {
        this.codePane = tf;
    }


}
