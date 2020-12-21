package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextInputDialog;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class SyntaxAnalyzer {
    private static Variable IT = new Variable();
    private ObservableList<Token> tokens = FXCollections.observableArrayList();
    private ArrayList<ArrayList> lines;
    private ArrayList<String> syntaxErrors;
    private ArrayList<String> lineErrors;
    private ArrayList<String> outputs;
    private ObservableList<Variable> variables;
    private TextFlow textOutput;

    private ArrayList<String> operations;
    private ArrayList<String> operands;

    public SyntaxAnalyzer() {
        this.syntaxErrors = new ArrayList<String>();
        this.lineErrors = new ArrayList<String>();
        this.outputs = new ArrayList<String>();
        this.variables = FXCollections.observableArrayList();
        SyntaxAnalyzer.IT.setVarName("IT");
        this.variables.add(SyntaxAnalyzer.IT);
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
        else if(stringLine.get(0).getType().equals("Arithmetic Operator")){
            artihmeticOperation(stringLine, lineNumber);
        }
        else if(stringLine.get(0).getType().equals("Identifier")) {
            int ind = findVar(stringLine.get(0).getLexeme());
            if (ind == this.variables.size()) {
                this.lineErrors.add("In line " + Integer.toString(lineNumber) + ": ");
                this.syntaxErrors.add(stringLine.get(0).getLexeme() + " not defined\n");
                return;
            } else {
                assignVariable(stringLine, lineNumber, ind);
            }
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
                if(this.variables.size() > 0 && findVar(stringLine.get(1).getLexeme()) < this.variables.size()) {
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
                        // When the initialozed value is a variable
                        if(stringLine.get(3).getType().equals("Identifier")) {
                            int ind = findVar(stringLine.get(3).getLexeme());
                            if(ind == this.variables.size()) {
                                this.lineErrors.add("In line " + Integer.toString(lineNumber) + ": ");
                                this.syntaxErrors.add(stringLine.get(3).getLexeme() + " not defined\n");
                                return;
                            }
                            else {
                                String datatype = checkVarDatatype(this.variables.get(ind).getValue());
                                v.isInitialized = true;
                                v.setType(datatype);
                                v.setValue(this.variables.get(ind).getValue());
                            }
                        }
                        // When the initialized value is a literal
                        else if(stringLine.get(3).getType().equals("Literal") || stringLine.get(3).getType().equals(
                                "Boolean Literal")) {
                            String datatype = checkVarDatatype(stringLine.get(3).getLexeme());
                            v.isInitialized = true;
                            v.setType(datatype);
                            v.setValue(stringLine.get(3).getLexeme());
                        }
                        // When the lexeme after ITZ is not a literal/variable identifier
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
                    this.syntaxErrors.add("Variable " + t.getLexeme() + " not defined" + "\n");
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
        if(stringLine.size() > 2) {
            this.lineErrors.add("In line " + Integer.toString(lineNumber) + ": ");
            this.syntaxErrors.add("No tokens should be after identifier in GIMMEH keyword\n");
            return;
        }

        if(stringLine.get(1).getType().equals("Identifier")) {
            int ind = findVar(stringLine.get(1).getLexeme());
            if(ind == this.variables.size()) {
                this.lineErrors.add("In line " + Integer.toString(lineNumber) + ": ");
                this.syntaxErrors.add("Variable " + stringLine.get(1).getLexeme() + " not defined" + "\n");
            } else {
                // For getting the input of the user
                TextInputDialog input = new TextInputDialog("Enter value");
                input.setHeaderText("Enter value for " + stringLine.get(1).getLexeme());
                String userInput = input.showAndWait().get().trim();
                String inputDatype = checkVarDatatype(userInput);
                if(inputDatype.equals("YARN"))
                    userInput = "\"" + userInput + "\"";
                this.variables.get(ind).setType(inputDatype);
                this.variables.get(ind).setValue(userInput);
            }
        } else {
            this.lineErrors.add("In line " + Integer.toString(lineNumber) + ": ");
            this.syntaxErrors.add("Expected Identifier but found" + stringLine.get(1).getType() + "\n");
        }
    }

    void assignVariable(ArrayList<Token> stringLine,int lineNumber, int varIndex) {

        // Checks if R keyword exists after variable identifier
        try {
            if (!stringLine.get(1).getLexeme().equals("R")) {
                this.lineErrors.add("In line " + Integer.toString(lineNumber) + ": ");
                this.syntaxErrors.add("Expected Assignment keyword but found " + stringLine.get(1).getType() + "\n");
                return;
            }
        } catch(IndexOutOfBoundsException e) {
            this.lineErrors.add("In line " + Integer.toString(lineNumber) + ": ");
            this.syntaxErrors.add("Needs asignment keyword after variable\n");
            return;
        }

        try {
            if(stringLine.get(2).getType().equals("Literal") || stringLine.get(2).getType().equals("Boolean Literal")) {
                    this.variables.get(varIndex).setType(checkVarDatatype(stringLine.get(2).getLexeme()));
                    this.variables.get(varIndex).setValue(stringLine.get(2).getLexeme());
                    this.variables.get(varIndex).isInitialized = true;
            }
            else if(stringLine.get(2).getType().equals("Identifier")) {
                int index = findVar(stringLine.get(2).getLexeme());
                if(index == this.variables.size()) {
                    this.lineErrors.add("In line " + Integer.toString(lineNumber) + ": ");
                    this.syntaxErrors.add(stringLine.get(2).getLexeme() + " not defined\n");
                    return;
                }
                else {
                    this.variables.get(varIndex).setType(this.variables.get(index).getDatatype());
                    this.variables.get(varIndex).setValue(this.variables.get(index).getValue());
                    this.variables.get(varIndex).isInitialized = true;
                }
            }
            else if(stringLine.get(2).getType().equals("Arithmetic Operator")) {
                stringLine.remove(0);
                stringLine.remove(0);
                artihmeticOperation(stringLine, lineNumber);
                if(this.syntaxErrors.size() == 0) {
                    this.variables.get(varIndex).setType(IT.getDatatype());
                    this.variables.get(varIndex).setValue(IT.getValue());
                }
            }
            else {
                this.lineErrors.add("In line " + Integer.toString(lineNumber) + ": ");
                this.syntaxErrors.add("Expected literal/identifier after R but found" + stringLine.get(2).getType() + "\n");
            }
        } catch (IndexOutOfBoundsException e) {
            this.lineErrors.add("In line " + Integer.toString(lineNumber) + ": ");
            this.syntaxErrors.add("Needs literal/identifier after R keyword\n");
        }

    }

    void artihmeticOperation(ArrayList<Token> stringLine, int lineNumber) {
        ArrayList<String> operands = new ArrayList<String>();

        try {
            if (stringLine.get(1).getType().equals("Literal")) {
                if(stringLine.get(1).getLexeme().matches("^([1-9][0-9]*)$") || stringLine.get(1).getLexeme().matches(
                        "^([1-9][0-9]*\\.[0-9]*)$")) {
                    operands.add(stringLine.get(1).getLexeme());
                }
                else {
                    this.lineErrors.add("In line " + Integer.toString(lineNumber) + ": ");
                    this.syntaxErrors.add("Expected numeric literal/variable/expression but found " + stringLine.get(1).getType());
                    return;
                }
            }
            else if(stringLine.get(1).getType().equals("Identifier")) {
                int varIndex = findVar(stringLine.get(1).getLexeme());
                // Error checking for undefined variable
                if(varIndex == this.variables.size()) {
                    this.lineErrors.add("In line " + Integer.toString(lineNumber) + ": ");
                    this.syntaxErrors.add(stringLine.get(1).getLexeme() + " not defined");
                    return;
                }
                // Error checking for uninitialized variable
                else if(!this.variables.get(varIndex).isInitialized) {
                    this.lineErrors.add("In line " + Integer.toString(lineNumber) + ": ");
                    this.syntaxErrors.add(stringLine.get(1).getLexeme() + " not yet initialized");
                    return;
                }
                else {
                    // Error checking for Non-NUMBR/NUMBAR variables
                    System.out.println("LORENTEEEEE!!!");
                    if(this.variables.get(varIndex).getValue().matches("^([1-9][0-9]*)$") || this.variables.get(varIndex).getValue().matches(
                            "^([1-9][0-9]*\\.[0-9]*)$")) {
                        operands.add(this.variables.get(varIndex).getValue());
                    }
                    else {
                        this.lineErrors.add("In line " + Integer.toString(lineNumber) + ": ");
                        this.syntaxErrors.add("Value of variable " + stringLine.get(1).getLexeme() + " is not " +
                                "of type NUMBR/NUMBAR");
                        return;
                    }
                }
            }
        }catch(Exception e){
            this.lineErrors.add("In line " + Integer.toString(lineNumber) + ": ");
            this.syntaxErrors.add("Expected numeric literal/variable/expression");
            return;
        }

        try {
            if(!stringLine.get(2).getLexeme().equals("AN")) {
                this.lineErrors.add("In line " + Integer.toString(lineNumber) + ": ");
                this.syntaxErrors.add("Expected AN keyword but found " + stringLine.get(2).getType());
                return;
            }
        } catch (Exception e) {
            this.lineErrors.add("In line " + Integer.toString(lineNumber) + ": ");
            this.syntaxErrors.add("Expected AN keyword");
        }

        try {
            if (stringLine.get(3).getType().equals("Literal")) {
                if(stringLine.get(3).getLexeme().matches("^([1-9][0-9]*)$") || stringLine.get(3).getLexeme().matches(
                        "^([1-9][0-9]*\\.[0-9]*)$")) {
                    operands.add(stringLine.get(3).getLexeme());
                }
                else if(stringLine.get(3).getType().equals("Identifier")) {
                    System.out.println("Operand is identifier");
                }
                else {
                    this.lineErrors.add("In line " + Integer.toString(lineNumber) + ": ");
                    this.syntaxErrors.add("Expected numeric literal/variable/expression but found " + stringLine.get(3).getType());
                    return;
                }
            }
            else if(stringLine.get(3).getType().equals("Identifier")) {
                int varIndex = findVar(stringLine.get(3).getLexeme());

                // Error checking for undefined variable
                if(varIndex == this.variables.size()) {
                    this.lineErrors.add("In line " + Integer.toString(lineNumber) + ": ");
                    this.syntaxErrors.add(stringLine.get(3).getLexeme() + " not defined");
                    return;
                }
                // Error checking for uninitialized variable
                else if(!this.variables.get(varIndex).isInitialized) {
                    this.lineErrors.add("In line " + Integer.toString(lineNumber) + ": ");
                    this.syntaxErrors.add(stringLine.get(3).getLexeme() + " not yet initialized");
                    return;
                }

                else {
                    // Error checking for Non-NUMBR/NUMBAR variables
                    if(this.variables.get(varIndex).getValue().matches("^([1-9][0-9]*)$") || this.variables.get(varIndex).getValue().matches(
                            "^([1-9][0-9]*\\.[0-9]*)$")) {
                        operands.add(this.variables.get(varIndex).getValue());
                    }
                    else {
                        this.lineErrors.add("In line " + Integer.toString(lineNumber) + ": ");
                        this.syntaxErrors.add("Value of variable " + stringLine.get(3).getLexeme() + " is not " +
                                "of type NUMBR/NUMBAR");
                        return;
                    }
                }
            } else if(stringLine.get(3).getType().equals("Arithmetic Operator")) {

            }
        } catch(Exception e) {
            this.lineErrors.add("In line " + Integer.toString(lineNumber) + ": ");
            this.syntaxErrors.add("Expected numeric literal/variable/expression");
        }

        boolean isFloat = false;
        float sum = 0;
        for(String number: operands) {
            if(number.matches("^([1-9][0-9]*)$")) {
                sum = sum + Integer.parseInt(number);
            }
            if(number.matches("^([1-9][0-9]*\\.[0-9]*)$")) {
                sum = sum + Float.parseFloat(number);
                isFloat = true;
            }
        }

        if(isFloat) {
            IT.setType("NUMBAR");
            IT.setValue(Float.toString(sum));
        }
        else {
            IT.setType("NUMBR");
            IT.setValue(Integer.toString((int) sum));
        }

        System.out.println("Value of IT" + IT.getValue());
    }


    /*
    This is for finding the variable in the variables arraylist;
    If variable does not exist, index will be equal to the size
    of the variable arraylist
     */
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


    String checkVarDatatype(String literal) {
        String datatype = "YARN";
        if(literal.matches("^([1-9][0-9]*)$"))
            datatype = "NUMBR";
        if(literal.matches("^([1-9][0-9]*\\.[0-9]*)$"))
            datatype = "NUMBAR";
        if(literal.matches("^(WIN|FAIL)$"))
            datatype = "TROOF";

        return datatype;
    }

    /*
    Setters and Getters
     */

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
