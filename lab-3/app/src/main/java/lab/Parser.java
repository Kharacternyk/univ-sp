package lab;

import java.util.regex.*;

public class Parser {
    private final String input;
    private int position;
    private int shift;
    private Lexema lexema;

    public Parser(String input) {
        this.input = input;
    }

    public int getPosition() {
        return position;
    }

    public boolean hasNext() {
        return position < input.length();
    }

    public Lexema getNextLexema() {
        shift = 0;
        lexema = null;

        tryWhiteSpace();
        tryComment();
        tryNumber();
        tryString();
        tryKeyword();
        tryIdentifier();
        tryDelimiter();
        tryOperator();

        position += shift;

        return lexema;
    }

    private void tryWhiteSpace() {
        tryPattern("\\s+", null);
    }

    private void tryComment() {
        tryPattern("//[^\\n]*$", null);
        tryPattern("/\\*.*\\*/", null);
    }

    private void tryNumber() {
        tryPattern("0n?", "number");
        tryPattern("[1-9][0-9]*n?", "number");
        tryPattern("0[0-7]+", "number");
        tryPattern("0[oO][0-7]+n?", "number");
        tryPattern("0[xX][0-7a-fA-F]+n?", "number");
        tryPattern("0[bB][01]+n?", "number");
        tryPattern("0?\\.[0-9]+([eE][+-]?[0-9]+)?", "number");
        tryPattern("[1-9][0-9]*\\.[0-9]+([eE][+-]?[0-9]+)?", "number");
        tryPattern("0[eE][+-]?[0-9]+", "number");
        tryPattern("[1-9][0-9]*[eE][+-]?[0-9]+", "number");
    }

    private void tryString() {
        tryPattern("\"([^\\n\"]|\\\")*\"", "string");
        tryPattern("\'([^\\n\']|\\\')*\'", "string");
        tryPattern("`([^\\n`]|\\`)*`", "string");
    }

    private void tryKeyword() {
        tryPattern(
            "break|case|catch|class|const|continue|debugger|default|delete|do|else|export|" +
            "extends|false|finally|for|function|if|import|in|instanceof|new|null|return|" +
            "static|super|switch|this|throw|true|try|typeof|var|void|while|with|yield",
            "reserved word"
        );
    }

    private void tryIdentifier() {
        tryPattern("[A-Za-z_$][\\w$]*", "identifier");
    }
    
    private void tryDelimiter() {
        tryPattern("[\\{\\}\\(\\)\\[\\];,.]", "delimiter");
    }

    private void tryOperator() {
        tryPattern("\\*\\*=|>>>=|&&=|\\|\\|=|==|!=", "operator");
        tryPattern("&&|\\|\\||!|>>>", "operator");
        tryPattern(">|<|-|\\+|\\*|/", "operator");
        tryPattern(
            "=|\\+=|-=|\\*=|/=|%=|<<=|>>=|&=|^=|\\|=||\\?\\?=",
            "operator"
        );
        tryPattern(
            "===|!==|>=|<=" +
            "%|\\+\\+|--|\\*\\*|" +
            "&|\\||^|~|<<|>>|" +
            "\\?|:",
            "operator"
        );
    }

    private void tryPattern(String pattern, String lexemaType) {
        var compiledPattern = Pattern.compile(pattern, Pattern.DOTALL | Pattern.MULTILINE);
        var matcher = compiledPattern.matcher(input.subSequence(position, input.length()));

        if (matcher.lookingAt()) {
            var newShift = matcher.end() - matcher.start();

            if (newShift > shift) {
                shift = newShift;
                lexema = new Lexema(
                    lexemaType, input.substring(position, position + newShift)
                );
            }
        }
    }
}
