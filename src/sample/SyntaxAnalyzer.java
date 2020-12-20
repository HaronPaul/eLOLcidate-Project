package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextInputDialog;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.Optional;

public class SyntaxAnalyzer {
    private ObservableList<Token> tokens = FXCollections.observableArrayList();
    private ArrayList<ArrayList> lines;
    private ArrayList<String> syntaxErrors;
    private ArrayList<String> lineErrors;
    private ArrayList<String> outputs;
    private ObservableList<Variable> variables;
    private TextFlow textOutput;

    public SyntaxAnalyzer() {
        this.syntaxErrors = new ArrayList<String>();
        this.lineErrors = new ArrayList<String>();
        this.outputs = new ArrayList<String>();
        this.variables = FXCollections.observableArrayList();
    }

    void checkSyntax(ArrayList<Token> stringLine, int lineNumber) {
        if(stringLine.size() == 0)
            return;

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
        else if(stringLine.get(0).getLexeme().equals("GIMMEH")) {
            getUserInput(stringLine, lineNumber);
        }
    }

    void checkVarDeclaration(ArrayList<Token> stringLine, int lineNumber) {
        boolean validInit = true;
        Variable v;
        try {
            if(!stringLine.get(1).getType().equals("Identifier")) {
                this.lineErrors.add("In line " + Integer.toString(lineNumber) + ": ");
                this.syntaxErrors.add("Expected Identifier but found " +  stringLine.get(1).getType() + "\n");
                validInit = false;
                return;
            }

            else {
                if(this.variables.size() > 0 && findVar(stringLine.get(1).getLexeme()) == this.variables.size() - 1) {
                    this.lineErrors.add("In line " + Integer.toString(lineNumber) + ": ");
                    this.syntaxErrors.add("Variable " + stringLine.get(1).getLexeme() + " is already defined\n");
                    return;
                } else {
                    v = new Variable();
                    v.setVarName(stringLine.get(1).getLexeme());
                    v.setValue("NULL");
                    this.variables.add(v);
                }
            }

            if(validInit) {

                // When there is a variable identifier/string or number literal after the ITZ keyword
                if(stringLine.size() > 2) {

                    if (!stringLine.get(2).getLexeme().equals("ITZ")) {
                          this.lineErrors.add("In line " + Integer.toString(lineNumber) + ": ");
                        this.syntaxErrors.add("Expected keyword \"ITZ\" but found " + stringLine.get(2).getType() + "\n");
                        return;
                    }
                    try {
                        if(stringLine.get(3).getType().equals("Identifier")) {
                            int ind = findVar(stringLine.get(3).getLexeme());
                            if(ind == this.variables.size()) {
                                this.lineErrors.add("In line " + Integer.toString(lineNumber) + ": ");
                                this.syntaxErrors.add(stringLine.get(3).getLexeme() + " not yet declared\n");
                                return;
                            }
                            else {
                                v.setValue(this.variables.get(ind).getValue());
                            }

                        }
                        else if(stringLine.get(3).getType().equals("Literal")) {
                            v.setValue(stringLine.get(3).getLexeme());
                        }
                        else {
                            this.lineErrors.add("In line " + Integer.toString(lineNumber) + ": ");
                            this.syntaxErrors.add("Expected literal/identifier but found " + stringLine.get(3).getType() + "\n");
                        }
                    } catch(Exception e) {
                        this.lineErrors.add("In line " + Integer.toString(lineNumber) + ": ");
                        this.syntaxErrors.add("No initalized value after ITZ keyword" + "\n");
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
            else if(t.getType().equals("Identifier")) {
                int index = findVar(t.getLexeme());
                if(index == this.variables.size()) {
                    this.lineErrors.add("In line " + Integer.toString(lineNumber) + ": ");
                    this.syntaxErrors.add("Variable " + t.getLexeme() + " not yet initialized" + "\n");
                    return;
                }
                else if(this.variables.get(index).getValue().equals("NULL")) {
                    this.lineErrors.add("In line " + Integer.toString(lineNumber) + ": ");
                    this.syntaxErrors.add("Variable " + t.getLexeme() + " not yet initialized" + "\n");
                    return;
                }
                else {
                    print = print + this.variables.get(index).getValue();
                }
            }
        }

        print = print + "\n" ;
        Text t = new Text(print);
        this.textOutput.getChildren().add(t);
    }

    void getUserInput(ArrayList<Token> stringLine, int lineNumber) {
        if(stringLine.get(1).getType().equals("Identifier")) {
            int ind = findVar(stringLine.get(1).getLexeme());
            if(ind == this.variables.size()) {
                this.lineErrors.add("In line " + Integer.toString(lineNumber) + ": ");
                this.syntaxErrors.add("Variable " + stringLine.get(1).getLexeme() + " not defined" + "\n");
            } else {
                TextInputDialog input = new TextInputDialog("Enter value");
                input.setHeaderText("Enter value for " + stringLine.get(1).getLexeme());
                Optional<String> result = input.showAndWait();
                String userInput = result.get();
                System.out.println(userInput);
            }
        } else {
            this.lineErrors.add("In line " + Integer.toString(lineNumber) + ": ");
            this.syntaxErrors.add("Expected Identifier but found" + stringLine.get(1).getType() + "\n");
        }
    }

    int findVar(String varName) {
        int index = 0;
        for(Variable v : this.variables) {
            if(v.getVarName().equals(varName)) {
                break;
            }
            index++;
        }

        return index;
    }

    ArrayList<String> getSyntaxErrors(){
        return this.syntaxErrors;
    }
    ArrayList<String> getLineErrors() {return this.lineErrors; }
    ArrayList<String> getOutputs() {return this.outputs;}
    ArrayList<ArrayList> getLines() {return this.lines;}
    ObservableList<Variable> getVariables() {return this.variables;}

    void setLines(ArrayList<ArrayList> lines) {
        this.lines = lines;
    }

    void printErrors() {
       for(int i=0;i<this.syntaxErrors.size();i++) {
           System.out.print("In line " + this.lineErrors.get(i) + ": ");
           System.out.println(this.syntaxErrors.get(i));
       }
    }

    void setTextOutput(TextFlow output) {
        this.textOutput = output;
    }

}
