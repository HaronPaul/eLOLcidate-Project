package sample;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Variable {
    SimpleStringProperty varName;
    SimpleStringProperty value;
    boolean isInitialized;
    String type;


    public Variable() {
        this.isInitialized = false;
        if(!this.isInitialized) {
            this.type = "NOOB";
            this.type = "NULL";
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
        this.type = type;
    }

    String getVarName() {
        return this.varName.get();
    }

    String getValue() {
        return this.value.get();
    }


}