package sample;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Token {
    SimpleStringProperty lexeme;
    SimpleStringProperty type;
    int lineNum;
    int lineCol;
    String literalType;
    String value;


    public Token(int lineNumber, int lineColumn) {
        this.lineNum = lineNumber;
        this.lineCol = lineColumn;
    }

    void setLexeme(String l) {
        this.lexeme = new SimpleStringProperty(l);
    }

    void setType(String t) {
        this.type = new SimpleStringProperty(t);
    }

    public StringProperty lexemeProperty() {
        return lexeme;
    }

    public StringProperty typeProperty() {
        return type;
    }

    int getLineNum() {
        return this.lineNum;
    }   

    public String getLexeme() {
        return lexeme.get();
    }

    public String getType() {
        return type.get();
    }

    public int getLineCol() {
        return this.lineCol;
    }

    public void setLiteralType(String litType) {
        this.literalType = litType;
    }

    public void setValue(String v) {
        this.value = v;
    }

}