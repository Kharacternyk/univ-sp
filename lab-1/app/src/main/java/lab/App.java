package lab;

import java.io.*;

public class App {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please provide a filename");
            return;
        }

        File file = new File(args[0]);
        FileInputStream stream;

        try {
            stream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            System.out.println("The file does not exist");
            return;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        HashSetStringSink sink = new HashSetStringSink();
        WordFilter filter = new WordFilter(sink);
        int character;

        try {
            while ((character = reader.read()) != -1) {
                filter.feedCharacter((char) character);
            }
        } catch (IOException e) {
            /* do nothing */
        } finally {
            filter.feedCurrentWord();

            for (String word: sink.strings) {
                System.out.println(word);
            }
        }
    }
}
