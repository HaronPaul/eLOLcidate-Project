package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.ArrayList;

public class SyntaxAnalyzer {
    private ObservableList<Token> tokens = FXCollections.observableArrayList();
    private boolean checkHAI = false;
    private boolean error;
    private ArrayList<String> syntaxErrors;

    public SyntaxAnalyzer(ObservableList<Token> t) {
        this.tokens = t;
        this.syntaxErrors = new ArrayList<String>();
    }

    void printTokens() {
        for(Token t: this.tokens) {
            System.out.println(Integer.toString(t.getLineNum()));
        }
    }

    void checkSyntax(ArrayList<Token> stringLine) {
        if(checkHAI == false) {
            if(stringLine.size() == 1 && stringLine.get(0).equals("HAI")) {
                checkHAI = true;
                return;
            }
            else {
                System.out.println("SYNTAX ERROR");
                this.syntaxErrors.add("Syntax Error: HAI keyword should have no succeeding statements");
                this.error = true;
            }
        }
        else {
            System.out.println("wew");


        }
    }

    Boolean getHAI() {
        return this.checkHAI;
    }

    Boolean getError() {
        return this.error;
    }

    ArrayList<String> getSyntaxError(){
        return this.syntaxErrors;
    }


}
