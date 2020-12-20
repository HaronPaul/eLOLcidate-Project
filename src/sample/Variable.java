package sample;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Variable {
    SimpleStringProperty varName;
    SimpleStringProperty value;
    boolean isInitialized;
    String datatype;
    boolean isYARN;
    boolean isNUMBR;
    boolean isNUMBAR;
    boolean isTROOF;


    public Variable() {
        this.isInitialized = false;
        if(!this.isInitialized) {
            this.datatype = "NOOB";
        }
    }

    public StringProperty varNameProperty() { return varName;}
    public StringProperty valueProperty() { return value;}

    void setVarName(String varName) {
        this.varName = new SimpleStringProperty(varName);
    }

    void setValue(String value) {
        this.value = new SimpleStringProperty(value);
    }

    void setType(String type) {
        this.datatype = type;
        if(type.equals("NUMBR"))
            this.isNUMBR = true;
        else if(type.equals("NUMBAR"))
            this.isNUMBAR = true;
        else if(type.equals("TROOF"))
            this.isTROOF = true;
        else
            this.isYARN = true;
    }

    String getVarName() {
        return this.varName.get();
    }

    String getValue() {
        return this.value.get();
    }
    String getDatatype() {return this.datatype; }


}