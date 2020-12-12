package sample;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Token {
    SimpleStringProperty lexeme;
    SimpleStringProperty type;
    int lineNum;

    public Token(int lineNumber) {
        this.lineNum = lineNumber;
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
}