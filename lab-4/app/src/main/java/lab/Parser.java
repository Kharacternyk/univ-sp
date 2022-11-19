package lab;

import java.util.HashSet;
import java.util.Arrays;

public class Parser {
    private final String input;
    private final HashSet<String> keywords;
    private final HashSet<Character> delimiters;
    private final HashSet<Character> operators;
    private final StringBuilder builder;
    private int position;
    private String state;
    private Lexema lexema;

    public Parser(String input) {
        this.input = input;
        builder = new StringBuilder();
        keywords = new HashSet<String>(Arrays.asList(
                "break", "case", "catch", "class", "const", "continue", "debugger",
                "default", "delete", "do", "else", "export",
                "extends", "false", "finally", "for", "function", "if",
                "import", "in", "instanceof", "new", "null", "return", "static",
                "super", "switch", "this", "throw",
                "true", "try", "typeof", "var", "void", "while", "with", "yield"));
        delimiters = new HashSet<Character>(Arrays.asList(
                '[', ']', '{', '}', '(', ')', ';', ',', '.', ':'));
        operators = new HashSet<Character>(Arrays.asList(
                '*', '/', '+', '-', '&', '|', '<', '>', '?', '^', '%', '~'));
    }

    public int getPosition() {
        return position;
    }

    public String getState() {
        return state;
    }

    public boolean hasNext() {
        return position < input.length();
    }

    public Lexema getNextLexema() {
        state = null;
        lexema = null;
        builder.setLength(0);

        while (lexema == null && hasNext()) {
            tryString();
            tryNumber();
            tryComment();
            tryIdentifier();
            tryOperator();
            tryDelimiter();
            tryWhiteSpace();

            if (state == "invalid") {
                return null;
            }

            ++position;
        }

        return lexema;
    }

    private void tryString() {
        if (state == null) {
            if (getChar() == '"') {
                builder.append(getChar());
                state = "double-quote";
            } else if (getChar() == '\'') {
                builder.append(getChar());
                state = "single-quote";
            }
        } else if (state == "double-quote") {
            if (getChar() == '"') {
                builder.append(getChar());
                lexema = new Lexema("string", builder.toString());
            } else if (getChar() == '\\') {
                builder.append(getChar());
                state = "double-quote-escape";
            } else if (getChar() == '\n') {
                state = "invalid";
            } else {
                builder.append(getChar());
            }
        } else if (state == "single-quote") {
            if (getChar() == '\'') {
                builder.append(getChar());
                lexema = new Lexema("string", builder.toString());
            } else if (getChar() == '\\') {
                builder.append(getChar());
                state = "single-quote-escape";
            } else if (getChar() == '\n') {
                state = "invalid";
            } else {
                builder.append(getChar());
            }
        } else if (state == "double-quote-escape") {
            builder.append(getChar());
            state = "double-quote";
        } else if (state == "single-quote-escape") {
            builder.append(getChar());
            state = "single-quote";
        }
    }

    private void tryNumber() {
        if (state == null) {
            if (getChar() == '0') {
                builder.append(getChar());
                state = "zero";
            } else if (getChar() >= '1' && getChar() <= '9') {
                builder.append(getChar());
                state = "decimal";
            }
        } else if (state == "decimal") {
            if (Character.isDigit(getChar())) {
                builder.append(getChar());
            } else if (getChar() == '.') {
                builder.append(getChar());
                state = "float";
            } else {
                --position;
                lexema = new Lexema("number", builder.toString());
            }
        } else if (state == "float") {
            if (Character.isDigit(getChar())) {
                builder.append(getChar());
            } else {
                --position;
                lexema = new Lexema("number", builder.toString());
            }
        } else if (state == "zero") {
            if (getChar() == 'x' || getChar() == 'X') {
                builder.append(getChar());
                state = "hex";
            } else if (getChar() == 'b' || getChar() == 'B') {
                builder.append(getChar());
                state = "binary";
            } else if (getChar() == 'o' || getChar() == 'O') {
                builder.append(getChar());
                state = "octal";
            } else {
                --position;
                lexema = new Lexema("number", builder.toString());
            }
        } else if (state == "hex") {
            if (Character.isDigit(getChar()) ||
                    getChar() >= 'a' && getChar() <= 'f' ||
                    getChar() >= 'A' && getChar() <= 'F') {
                builder.append(getChar());
            } else {
                --position;
                lexema = new Lexema("number", builder.toString());
            }
        } else if (state == "octal") {
            if (getChar() >= '0' && getChar() <= '7') {
                builder.append(getChar());
            } else {
                --position;
                lexema = new Lexema("number", builder.toString());
            }
        } else if (state == "binary") {
            if (getChar() == '0' || getChar() == '1') {
                builder.append(getChar());
            } else {
                --position;
                lexema = new Lexema("number", builder.toString());
            }
        }
    }

    private void tryComment() {
        if (state == null && getChar() == '/') {
            state = "/";
        } else if (state == "/") {
            if (getChar() == '/') {
                state = "//";
            } else if (getChar() == '*') {
                state = "/*";
            } else {
                lexema = new Lexema("operator", "/");
            }
        } else if (state == "//") {
            if (getChar() == '\n') {
                lexema = new Lexema(null, null);
            }
        } else if (state == "/*") {
            if (getChar() == '*') {
                state = "/**";
            }
        } else if (state == "/**") {
            if (getChar() == '/') {
                lexema = new Lexema(null, null);
            } else {
                state = "/*";
            }
        }
    }

    private void tryIdentifier() {
        if (state == null &&
                (Character.isLetter(getChar()) || getChar() == '_' || getChar() == '$')) {
            builder.append(getChar());
            state = "identifier";
        } else if (state == "identifier") {
            if ((Character.isLetterOrDigit(getChar())
                    || getChar() == '_' || getChar() == '$')) {
                builder.append(getChar());
            } else {
                String value = builder.toString();

                if (keywords.contains(value)) {
                    lexema = new Lexema("reserved word", value);
                } else {
                    lexema = new Lexema("identifier", value);
                }

                --position;
            }
        }
    }

    private void tryOperator() {
        if (state == null) {
            if (operators.contains(getChar())) {
                builder.append(getChar());
                state = "binary-operator";
            } else if (getChar() == '=') {
                builder.append(getChar());
                state = "equals";
            } else if (getChar() == '!') {
                builder.append(getChar());
                state = "bang";
            }
        } else if (state == "binary-operator") {
            if (getChar() == '=') {
                builder.append(getChar());
                lexema = new Lexema("operator", builder.toString());
            } else if (operators.contains(getChar())) {
                if (getChar() == builder.charAt(builder.length() - 1) &&
                        getChar() != '%' && getChar() != '^' && getChar() != '~') {
                    builder.append(getChar());
                    state = "double-binary-operator";
                } else {
                    --position;
                    lexema = new Lexema("operator", builder.toString());
                }
            } else {
                --position;
                lexema = new Lexema("operator", builder.toString());
            }
        } else if (state == "double-binary-operator") {
            if (getChar() == '=') {
                builder.append(getChar());
            } else {
                --position;
            }
            lexema = new Lexema("operator", builder.toString());
        } else if (state == "equals") {
            if (getChar() == '=') {
                builder.append(getChar());
                state = "double-equals";
            } else {
                --position;
                lexema = new Lexema("operator", builder.toString());
            }
        } else if (state == "double-equals") {
            if (getChar() == '=') {
                builder.append(getChar());
            } else {
                --position;
            }
            lexema = new Lexema("operator", builder.toString());
        } else if (state == "bang") {
            if (getChar() == '=') {
                builder.append(getChar());
                state = "double-bang";
            } else {
                --position;
                lexema = new Lexema("operator", builder.toString());
            }
        } else if (state == "double-bang") {
            if (getChar() == '=') {
                builder.append(getChar());
            } else {
                --position;
            }
            lexema = new Lexema("operator", builder.toString());
        }
    }

    private void tryDelimiter() {
        if (state == null && delimiters.contains(getChar())) {
            builder.append(getChar());
            lexema = new Lexema("delimiter", builder.toString());
        }
    }

    private void tryWhiteSpace() {
        if (state == null && (Character.isWhitespace(getChar()) || getChar() == '\n')) {
            lexema = new Lexema(null, null);
        }
    }

    private char getChar() {
        return input.charAt(position);
    }
}
