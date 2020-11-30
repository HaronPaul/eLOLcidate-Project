package sample;

import javafx.beans.property.SimpleStringProperty;

public class Token {
    private SimpleStringProperty lexeme;
    private SimpleStringProperty type;

    public Token(String lex, String type) {
        this.lexeme = new SimpleStringProperty(lex);
        this.type = new SimpleStringProperty(type);
    }

    String getLexeme() {
        return this.lexeme.get();
    }

    String getType(){
        return this.type.get();
    }
}
