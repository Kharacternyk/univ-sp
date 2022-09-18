package lab;

import java.util.HashSet;

public class HashSetStringSink implements StringSink {
    public final HashSet<String> strings;

    public HashSetStringSink() {
        strings = new HashSet<String>();
    }

    public void feedString(String string) {
        strings.add(string);
    }
}
