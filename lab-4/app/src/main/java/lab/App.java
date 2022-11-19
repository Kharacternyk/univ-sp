package lab;

import java.nio.file.*;

public class App {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Please provide the filenames");
            return;
        }

        String input;

        try {
            input = Files.readString(Path.of(args[0]));
        } catch (Exception e) {
            System.out.println("The input file does not exist");
            return;
        }

        try {
            Files.writeString(Path.of(args[1]), "");
        } catch (Exception e) {
            System.out.println("Cannot write to the output file");
            return;
        }

        var parser = new Parser(input);

        while (parser.hasNext()) {
            var lexema = parser.getNextLexema();

            if (lexema == null) {
                System.out.println(
                        "Error in state '" + parser.getState() + "' at character #"
                                + parser.getPosition() +
                                ": '" + input.charAt(parser.getPosition()) + "'");
                break;
            }
            if (lexema.type == null) {
                continue;
            }

            try {
                Files.writeString(
                        Path.of(args[1]),
                        lexema.value + " - " + lexema.type + "\n",
                        StandardOpenOption.APPEND);
            } catch (Exception e) {
                System.out.println("Cannot write to the output file");
                return;
            }
        }
    }
}
