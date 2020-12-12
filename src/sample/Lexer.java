package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextArea;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    File lolFile;
    ObservableList<Token> tokens = FXCollections.observableArrayList();
    ArrayList<Token> stringLine;
    TextArea codePane;
    SyntaxAnalyzer syntax;


    // This is for checking if the lexeme is BTW or OBTW
    boolean isComment = false;

    // Constructor for Lexer class
    public Lexer() {
        this.syntax = new SyntaxAnalyzer(this.tokens);
        this.stringLine = new ArrayList<Token>();
    }

    public void readLines() {
        int lineNum = 1;
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(lolFile));
            String line = reader.readLine();
            while(line != null) {

                // Creates a string
                String t = line + "\n";
                codePane.appendText(t);

                // When line is blank (No statements), it will just continue to next line
                if(line.length() == 0) {
                    line = reader.readLine();
                    continue;
                }
                else {
                    matchStrings(line, lineNum);
                    this.syntax.checkSyntax(this.stringLine);
                    stringLine.clear();
                }
                line = reader.readLine();
                lineNum++;
            }

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    // Regex part. This is where the keywords, strings,
    // and identifiers are matched
    void matchStrings(String line, int lineNum) {
        String subline = line;

        // Regex for the keywords
        Pattern keyword = Pattern.compile("(^I|\\bI)\\sHAS\\s(A$|A\\b)|(^H|\\bH)A(I$|I\\b)|(^K|\\bK)" +
                "THXBY(E$|E\\b)|(^V|\\bV)ISIBL(E$|E\\b)|(^D|\\bD)IFF O(F$|F\\b)|(^S|\\bS)UM O" +
                "(F$|F\\b)|" +
                "(^P|\\bP)RODUKT O(F$|F\\b)|(^Q|\\bQ)UOSHUNT O(F$|F\\b)|(^M|\\bM)OD\\sO(F$|F\\b)|" +
                "(^O|\\bO)BT(W$|W\\b)|(^T|\\bT)LD(R$|R\\b)|(^S|\\bS)MALLR\\sO(F$|F\\b)|(^B|\\bB)" +
                "IGGR\\sO(F$|F\\b)|(^W|\\bW)I(N$|N\\b)|(^F|\\bF)AI(L$|L\\b)|(^B|\\bB)OTH O(F$|F\\b)|" +
                "(^E|\\bE)ITHER O(F$|F\\b)|(^W|\\bW)ON O(F$|F\\b)|(^N|\\bN)O(T$|T\\b)|(^A|\\bA)" +
                "LL O(F$|F\\b)|(^A|\\bA)NY O(F$|F\\b)|(^W|\\bW)T(F$|F\\b)|(^O|\\bO)M(G$|G\\b)|" +
                "(^O|\\bO)MG WT(F$|F\\b)|(^A|\\bA)(N$|N\\b)|(^O|\\bO)\\sRLY(\\?$|\\?\\b)|(^Y|\\bY)" +
                "A RL(Y$|Y\\b)|(^M|\\bM)EBB(E$|E\\b)|(^N|\\bN)O WA(I$|I\\b)|(^O|\\bO)I(C$|C\\b)|" +
                "(^I|\\bI)T(Z$|Z\\b)|(^B|\\bB)OTH SAE(M$|M\\b)|(^D|\\bD)IFFRIN(T$|T\\b)|(^G|\\bG)" +
                "IMME(H$|H\\b)|(^S|\\bS)MOOS(H$|H\\b)|\\sR\\s|(^B|\\bB)T(W$|W\\b)|(^N|\\bN)UMB(R$|R\\b)|(^N|\\bN)UMBA" +
                "(R$|R\\b)|(^T|\\bT)ROO(F$|F\\b)|(^Y|\\bY)AR(N$|N\\b)");


        Matcher m = keyword.matcher(subline);
        // This returns all the matched keywords from the line
        while(m.find()) {
            String matched = m.group().trim();

            subline = subline.replace(matched, "").trim();

            if(matched.equals("BTW")) {
                return;
            }

            else if(matched.equals("OBTW")) {
                this.isComment = true;
                break;
            }

            else if(matched.equals("TLDR")) {
                this.isComment = false;
                break;
            }

            if(!this.isComment)
                addLexeme(matched, "keyword", lineNum);
        }

        // If line is in comment part, it will not continue
        // to the succeeding lines
        if(this.isComment)
            return;

        Pattern string = Pattern.compile("\\B\"[^\"]*\"\\B");
        Matcher s = string.matcher(subline);
        while (s.find()) {
            String matchedString = s.group().trim();
            addLexeme(matchedString, "string", lineNum);
            subline = subline.replace(matchedString, "").trim();
        }

        // Regex for variables identifier
        Pattern varident = Pattern.compile("(^[a-zA-Z]| [a-zA-Z_])[a-zA-Z0-9]*[a-zA-Z0-9]*\\b");
        Matcher v = varident.matcher(subline);
        while(v.find()) {
            String matchedId = v.group().trim();
            addLexeme(matchedId, "id", lineNum);
            matchedId = "\\b(" + matchedId + ")\\b";
            subline = subline.replaceAll(matchedId, "").trim();
        }

        Pattern number = Pattern.compile("(^-?[0-9]| -?[0-9])[0-9]*\\.?[0-9]*\\b");
        Matcher n = number.matcher(subline);
        while(n.find()) {
            String matchedNum = n.group().trim();
            addLexeme(matchedNum, "number", lineNum);
            matchedNum = "\\b" + matchedNum + "\\b";
            subline = subline.replaceAll(matchedNum, "").trim();
        }
    }

    void addLexeme(String matched, String type, int lineNumber) {
        Token token = new Token(lineNumber);
        token.setLexeme(matched);
        if(type.equals("keyword")) {
            if (matched.equals("HAI") || matched.equals("KTHXBYE"))
                token.setType("Code Delimiter");
            else if (matched.equals("I HAS A"))
                token.setType("Variable Declaration");
            else if (matched.equals("VISIBLE"))
                token.setType("Output Keyword");
            else if (matched.equals("SUM OF") || matched.equals("DIFF OF") || matched.equals("PRODUKT OF") || matched.equals("QUOSHUNT OF")
                    || matched.equals("MOD OF") || matched.equals("BIGGR OF") || matched.equals("SMALLR OF"))
                token.setType("Arithmetic Operator");
            else if (matched.equals("BOTH OF") || matched.equals("ANY OF") || matched.equals("EITHER OF") || matched.equals("WON OF") || matched.equals("NOT") || matched.equals("ALL OF"))
                token.setType("Boolean Operator");
            else if (matched.equals("BOTH SAEM") || matched.equals("DIFFRINT"))
                token.setType("Relational Operator");
            else if (matched.equals("WIN") || matched.equals("FAIL"))
                token.setType("Boolean Literal");
            else if (matched.equals("ITZ"))
                token.setType("Variable Initialization");
            else if (matched.equals("AN"))
                token.setType("Separator");
            else if (matched.equals("GIMMEH"))
                token.setType("Input Keyword");
            else if (matched.equals("SMOOSH"))
                token.setType("String Concatenation");
            else if (matched.equals("OBTW") || matched.equals("TLDR") || matched.equals("BTW"))
                token.setType("Comment Delimiter");
            else if (matched.equals("R"))
                token.setType("Assignment Operation");
            else if (matched.equals("NUMBR") || matched.equals("NUMBAR") || matched.equals("TROOF") || matched.equals(
                    "YARN"))
                token.setType("Datatype Keyword");
            else if(matched.equals("YA RLY") || matched.equals("NO WAI") || matched.equals("O RLY?") || matched.equals("OIC"))
                token.setType("If-Else delimiter");
            else if(matched.equals("OMG") || matched.equals("OMGWTF") || matched.equals("GTFO")) {
                token.setType("Switch-Case Delimiter");
            }
        }
        else if(type.equals("string")) {
            Token openQuote = new Token(lineNumber);
            openQuote.setLexeme(String.valueOf(matched.charAt(0)));
            openQuote.setLexeme("String delimiter");

            Token closeQuote = new Token(lineNumber);
            closeQuote.setLexeme(String.valueOf(matched.charAt(matched.length() - 1)));
            closeQuote.setLexeme("String delimiter");

            token.setLexeme(matched.replaceAll("\"", ""));

            token.setType("Literal");
        } else if(type.equals("id")) {
            token.setType("Identifier");
        } else if(type.equals("number")) {
            token.setType("Literal");
        }

        this.tokens.add(token);
        this.stringLine.add(token);
    }

    // This is for debugging only
    void printTokens() {
        for(Token t: this.tokens) {
            System.out.println(t.lexemeProperty().getValue() + " " + t.typeProperty().getValue());
        }
    }

    // Setter for the LOL code passed from the Controller class
    public void setLolFile(File file) {
        this.lolFile = file;
    }

    void setCodePane(TextArea tf) {
        this.codePane = tf;
    }

    ObservableList<Token> getTokens() {
        return this.tokens;
    }

    SyntaxAnalyzer getSa() {
        return this.syntax;
    }


}
