package lab;

import java.util.HashSet;
import java.util.Arrays;

public class Parser {
    private final String input;
    private final HashSet<String> keywords;
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
            tryComment();
            tryString();
            tryIdentifier();
            tryWhiteSpace();

            if (state == null && lexema == null) {
                return null;
            }

            ++position;
        }

        return lexema;
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
                state = null;
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
                state = null;
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

    private void tryWhiteSpace() {
        if (state == null && (Character.isWhitespace(getChar()) || getChar() == '\n')) {
            lexema = new Lexema(null, null);
        }
    }

    private char getChar() {
        return input.charAt(position);
    }
}
