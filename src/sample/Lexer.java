package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextArea;

import java.awt.desktop.SystemSleepEvent;
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
    String subline;


    // This is for checking if the lexeme is BTW or OBTW
    boolean isComment = false;

    // Constructor for Lexer class
    public Lexer() {
        this.stringLine = new ArrayList<Token>();
    }

    public void readLines() {
        int lineNum = 1;
        BufferedReader reader;

        // Reading line by line
        try {
            reader = new BufferedReader(new FileReader(lolFile));

            // This stores the line read
            String line = reader.readLine();
            while(line != null) {

                // Creates a string
                String t = line + "\n";

                // Puts the line to the textflow to be displayed in the UI
                codePane.appendText(t);

                // When line is blank (No statements), it will just continue to next line
                if(line.length() == 0) {
                    lineNum++;
                    line = reader.readLine();
                    continue;
                }
                else {
                    matchStrings(line, lineNum);
                    //this.syntax.checkSyntax(this.stringLine, lineNum);
                    stringLine.clear();
                }
                line = reader.readLine();
                lineNum++;
            }

        } catch (IOException e){
            e.printStackTrace();
        }

        //this.syntax.printErrors();
    }

    // Regex part. This is where the keywords, strings,
    // and identifiers are matched
    void matchStrings(String line, int lineNum) {
        this.subline = line.trim();
        boolean flag = true;

        // Regex for the keywords
        Pattern keyword = Pattern.compile("(^B|\\bB)T(W$|W\\b)|(^I|\\bI)\\sHAS\\s(A$|A\\b)|(^H|\\bH)A(I$|I\\b)|(^K|\\bK)" +
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
                "IMME(H$|H\\b)|(^S|\\bS)MOOS(H$|H\\b)|\\sR|(^N|\\bN)UMB(R$|R\\b)|(^N|\\bN)UMBA" +
                "(R$|R\\b)|(^T|\\bT)ROO(F$|F\\b)|(^Y|\\bY)AR(N$|N\\b)");


        Matcher m = keyword.matcher(this.subline);
        // This returns all the matched keywords from the line

        while(m.find()) {
            String matched = m.group();
            // This removes the matched keyword in the line so that it will not be
            // identified as a variable identifier later on.

            // When the matched keyword is BTW, it will ignore the
            // rest of the words in the line and proceed to next line
            if(matched.equals("BTW")) {
                int start = this.subline.indexOf(matched);

                // This will convert the the subline string to an arracy of characters for
                // character checking
                char[] newString = this.subline.toCharArray();

                try {

                    // When it encounters BTW, it replaces all the strings with a space
                    while(start != this.subline.length()) {
                        newString[start] = ' ';
                        start++;
                    }

                    // converts the array of chars to a single stirng
                    this.subline = String.valueOf(newString);

                } catch (Exception e) {
                    System.out.print("");
                }

            }

            // If keyword is OBTW, it ignores the succeeding lines until it encounters
            // a TLDR keyword. isComment flag is also set to true.
            else if(matched.equals("OBTW")) {
                this.isComment = true;
                break;
            }

            // When TLDR is encountered, isComment flag is set to false and continues on matching other
            // lexemes
            else if(matched.equals("TLDR")) {
                this.isComment = false;
                return;
            }

            // If lexeme is not within comment block, it will add the lexeme
            // to the tokens list
            if(!this.isComment) {

                // If the keyword is BTW, it proceeds to the next matched keyword.
                // It will not proceed to the succeeding lines
                if(matched.equals("BTW")) {
                    continue;
                }


                // startInd is the starting index of the matched keyword in the string 'subline'
                // endInd is the end Index of the index of the matched keyword in the string 'subline'
                int startInd = this.subline.indexOf(matched.trim());
                int endInd = startInd + (matched.length() - 1);

                // String to char array
                char[] newString = this.subline.toCharArray();

                try {

                    // If the end index == length of subline (means that it is the last word of the statement)
                    // or if the next character of a string is space, then it will replace the words with a space
                    if(endInd == this.subline.length() - 1 || newString[endInd + 1] == ' ') {
                        for(int i=startInd;i<endInd + 1;i++) {
                            newString[i] = ' ';
                        }
                    }

                    // char array to single string
                    this.subline = String.valueOf(newString);

                    // for adding the lexeme to the tokens list
                    addLexeme(matched, "keyword", lineNum, startInd);

                } catch(Exception e) {
                    System.out.println(matched + "Out of bounds");
                    addLexeme(matched, "keyword", lineNum, startInd);
                }


            }

        }

        // If line is in comment part, it will not continue
        // to the succeeding lines
        if(this.isComment)
            return;

        // Regex for string
        Pattern string = Pattern.compile("\\B\"[^\"]*\"\\B");
        Matcher s = string.matcher(subline);

        while (s.find()) {
            // This is the matched string
            String matchedString = s.group().trim();

            // startInd is the starting index of the matched keyword in the string 'subline'
            // endInd is the end Index of the index of the matched keyword in the string 'subline'
            int startInd = this.subline.indexOf(matchedString);
            int endInd = startInd + (matchedString.length() - 1);

            // string to char array
            char[] newString = this.subline.toCharArray();
            for(int i=startInd;i<(endInd + 1);i++) {
                newString[i] = ' ';
            }

            this.subline = String.valueOf(newString);
            addLexeme(matchedString, "string", lineNum, startInd);
        }

        // Regex for variables identifier
        Pattern varident = Pattern.compile("(^[a-zA-Z]| [a-zA-Z_])[a-zA-Z0-9]*[a-zA-Z0-9]*\\b");
        Matcher v = varident.matcher(subline);
        while(v.find()) {
            // Trimming the leading and trailing whitespaces of the line
            boolean hasSymbol = false;
            String matchedId = v.group().trim();

            int startInd = this.subline.indexOf(matchedId);
            int endInd = startInd + (matchedId.length() - 1);

            char[] newString = this.subline.toCharArray();

            try {
                if (newString[endInd + 1] == ' ' || newString[endInd + 1] == '\t') {
                    for (int i = startInd; i < (endInd + 1); i++) {
                        newString[i] = ' ';
                    }
                    addLexeme(matchedId, "id", lineNum, startInd);
                }

            } catch(Exception e) {
                System.out.print("");
                for (int i = startInd; i < (endInd + 1); i++) {
                    newString[i] = ' ';
                }
                addLexeme(matchedId, "id", lineNum, startInd);
            }

            this.subline = String.valueOf(newString);
        }

        // Regex for numbers
        Pattern number = Pattern.compile("(^-?[0-9]| -?[0-9])[0-9]*\\.?[0-9]*\\b");
        Matcher n = number.matcher(subline);
        while(n.find()) {

            // Trimming the leading and trailing whitespaces of the line
            String matchedNum = n.group().trim();

            int startInd = this.subline.indexOf(matchedNum);
            int endInd = startInd + (matchedNum.length() - 1);

            char[] newString = this.subline.toCharArray();
            for(int i=startInd;i<(endInd + 1);i++) {
                newString[i] = ' ';
            }

            this.subline = String.valueOf(newString);
            addLexeme(matchedNum, "number", lineNum, startInd);
        }

        System.out.println(this.subline);
    }

    // This functions adds the token to the list
    void addLexeme(String matched, String type, int lineNumber, int lineColumn) {

        // Cerates a new token
        Token token = new Token(lineNumber, lineColumn);
        token.setLexeme(matched.trim());

        // When lexeme is a keyword, it will then classify what type of keyword
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
            else if (matched.trim().equals("R"))
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


        // If lexeme is a string, it will get the double quotes as delimiters and stores the string as a literal
        else if(type.equals("string")) {
            // This creates a new token for opening double quotes
            Token openQuote = new Token(lineNumber, lineColumn);
            openQuote.setLexeme(String.valueOf(matched.charAt(0)));
            openQuote.setLexeme("String delimiter");

            // This creates a new token for closing double quotes
            Token closeQuote = new Token(lineNumber, lineColumn);
            closeQuote.setLexeme(String.valueOf(matched.charAt(matched.length() - 1)));
            closeQuote.setLexeme("String delimiter");

            // This removes the double quotes of the string, storing only the string inside the
            // double quotes
            token.setLexeme(matched.replaceAll("\"", ""));

            token.setType("Literal");

            // When lexeme is of type identifier
        } else if(type.equals("id")) {
            token.setType("Identifier");

            // When lexeme is of type number
        } else if(type.equals("number")) {
            token.setType("Literal");
        }

        // Adding the token to the list
        this.tokens.add(token);
        sortLine(token);
    }


    // This is for debugging only
    void printTokens() {
        for(Token t: this.tokens) {
            System.out.println(t.lexemeProperty().getValue() + " " + t.typeProperty().getValue());
        }
    }

    void sortLine(Token token) {
        this.stringLine.add(token);
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

}
