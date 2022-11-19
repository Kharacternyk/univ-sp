package lab;

public class Parser {
    private final String input;
    private int position;
    private String state;
    private Lexema lexema;
    private StringBuilder builder;

    public Parser(String input) {
        this.input = input;
        this.builder = new StringBuilder();
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
                state = null;
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

    private void tryWhiteSpace() {
        if (state == null && (Character.isWhitespace(getChar()) || getChar() == '\n')) {
            lexema = new Lexema(null, null);
        }
    }

    private char getChar() {
        return input.charAt(position);
    }
}
