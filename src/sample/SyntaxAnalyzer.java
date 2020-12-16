package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.ArrayList;

public class SyntaxAnalyzer {
    private ObservableList<Token> tokens = FXCollections.observableArrayList();
    private ArrayList<String> syntaxErrors;
    private ArrayList<String> lineErrors;
    private ArrayList<String> outputs;

    public SyntaxAnalyzer() {
        this.syntaxErrors = new ArrayList<String>();
        this.lineErrors = new ArrayList<String>();
        this.outputs = new ArrayList<String>();
    }


    void checkSyntax(ArrayList<Token> stringLine, int lineNumber) {
        if(stringLine.size() == 0)
            return;

        for(Token t: stringLine) {
            System.out.print(t.getLexeme() + " ");
        }

        if(stringLine.get(0).getLexeme().equals("HAI")) {
            if (stringLine.size() > 1) {
                this.syntaxErrors.add("No other statements should be added after HAI delimiter" + "\n");
                this.lineErrors.add("In line " + Integer.toString(lineNumber) + ": ");
            }
        }
        else if(stringLine.get(0).getLexeme().equals("I HAS A")) {
            checkVarDeclaration(stringLine, lineNumber);
        }
        else if(stringLine.get(0).getLexeme().equals("VISIBLE")) {
            addtoOutput(stringLine, lineNumber);
        }

    }

    void checkVarDeclaration(ArrayList<Token> stringLine, int lineNumber) {
        boolean validInit = true;

        try {
            if(!stringLine.get(1).getType().equals("Identifier")) {
                this.lineErrors.add("In line " + Integer.toString(lineNumber) + ": ");
                this.syntaxErrors.add("Expected Identifier but found " +  stringLine.get(1).getType() + "\n");
                validInit = false;
            }

            if(validInit) {

                // When there is an variable identifier/string or number literal after the ITZ keyword
                if(stringLine.size() > 2) {
                    if (!stringLine.get(2).getLexeme().equals("ITZ")) {
                        this.lineErrors.add("In line " + Integer.toString(lineNumber) + ": ");
                        this.syntaxErrors.add("Expected keyword \"ITZ\" but found " + stringLine.get(2).getType() + "\n");
                        return;
                    }
                    try {
                        if(stringLine.get(3).getType().equals("Identifier") || stringLine.get(3).getType().equals(
                            "Literal")) {
                            stringLine.get(2).setValue(stringLine.get(3).getLexeme());
                        }
                        else {
                            this.lineErrors.add("In line " + Integer.toString(lineNumber) + ": ");
                            this.syntaxErrors.add("Expected literal/identifier but found " + stringLine.get(3).getType() + "\n");
                        }
                    } catch(Exception e) {
                        this.lineErrors.add("In line " + Integer.toString(lineNumber) + ": ");
                        this.syntaxErrors.add("No initalized value after ITZ" + "\n");
                    }
                }
            }
        }catch (IndexOutOfBoundsException e) {}
    }

    void addtoOutput(ArrayList<Token> stringLine, int lineNumber) {
        stringLine.remove(0);
        String print = "";
        for(Token t: stringLine) {
            if(t.getType().equals("Literal")) {
                print = print + t.getLexeme() + " ";
            }
        }
        print = print + "\n" ;
        this.outputs.add(print);
    }

    ArrayList<String> getSyntaxErrors(){
        return this.syntaxErrors;
    }
    ArrayList<String> getLineErrors() {return this.lineErrors; }
    ArrayList<String> getOutputs() {return this.outputs;}

    void printErrors() {
       for(int i=0;i<this.syntaxErrors.size();i++) {
           System.out.print("In line " + this.lineErrors.get(i) + ": ");
           System.out.println(this.syntaxErrors.get(i));
       }
    }

}
