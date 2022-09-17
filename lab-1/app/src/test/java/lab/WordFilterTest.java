package lab;

import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WordFilterTest {
    @Test void testWordFilter() {
        StringSinkMock sink = new StringSinkMock();
        WordFilter filter = new WordFilter(sink);
        String input = "wasp + bee = bumblebee? 'SURELY COURSE NOT!', he yelled";

        for (final char character: input.toCharArray()) {
            filter.feedCharacter(character);
        }

        filter.feedCurrentWord();

        assertEquals(sink.strings.size(), 4);
        assertEquals(sink.strings.get(0), "wasp");
        assertEquals(sink.strings.get(1), "bumblebee");
        assertEquals(sink.strings.get(2), "COURSE");
        assertEquals(sink.strings.get(3), "yelled");
    }
}

class StringSinkMock implements StringSink {
    final public ArrayList<String> strings;

    public StringSinkMock() {
        strings = new ArrayList<String>();
    }

    public void feedString(String string) {
        strings.add(string);
    }
}
