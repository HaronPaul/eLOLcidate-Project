package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.ArrayList;

public class SyntaxAnalyzer {
    private ObservableList<Token> tokens = FXCollections.observableArrayList();
    private ArrayList<String> syntaxErrors;
    private ArrayList<String> lineErrors;

    public SyntaxAnalyzer(ObservableList<Token> t) {
        this.tokens = t;
        this.syntaxErrors = new ArrayList<String>();
        this.lineErrors = new ArrayList<String>();
    }


    void checkSyntax(ArrayList<Token> stringLine, int lineNumber) {
        for(Token t: stringLine) {
            System.out.print(t.getLexeme() + " ");
        }

        if(stringLine.get(0).getLexeme().equals("HAI")) {
            if (stringLine.size() > 1) {
                this.syntaxErrors.add("No other statements should be be added in HAI delimiter");
                this.lineErrors.add(Integer.toString(lineNumber));
            }
        }
        else if(stringLine.get(0).getLexeme().equals("I HAS A")) {
            checkVarDeclaration(stringLine, lineNumber);
        }

    }

    void checkVarDeclaration(ArrayList<Token> stringLine, int lineNumber) {
        try {
            if(stringLine.get(1).getType() != "Identifier") {
                this.lineErrors.add(Integer.toString(lineNumber));
                this.syntaxErrors.add("Keyword I HAS A needs identifier");
            }

            if(stringLine.size() > 2) {
                if (stringLine.get(2).getLexeme() != "ITZ") {
                    this.lineErrors.add(Integer.toString(lineNumber));
                    this.syntaxErrors.add(stringLine.get(2).getLexeme() + "Invalid initializer keyword. Use ITZ");
                }
            }

        }catch (IndexOutOfBoundsException e) {}
    }

    ArrayList<String> getSyntaxError(){
        return this.syntaxErrors;
    }

    void printErrors() {
       for(int i=0;i<this.syntaxErrors.size();i++) {
           System.out.print("In line number " + this.lineErrors.get(i) + ": ");
           System.out.println(this.syntaxErrors.get(i));
       }
    }

}
