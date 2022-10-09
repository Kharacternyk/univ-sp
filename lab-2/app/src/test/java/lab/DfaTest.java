/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package lab;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.HashSet;

class DfaTest {
    @Test void DfaIsSerializable() {
        var transitions = new HashMap<String, HashMap<Character, String>>();

        transitions.put("a", new HashMap<Character, String>());
        transitions.put("b", new HashMap<Character, String>());
        transitions.get("a").put('x', "a");
        transitions.get("a").put('y', "b");
        transitions.get("b").put('x', "a");
        transitions.get("b").put('y', "a");

        var finalStates = new HashSet<String>();

        finalStates.add("b");

        Dfa dfa = new Dfa(transitions, finalStates, "a", 2);

        assertEquals(dfa.toString(), "2\n2\na\n1 b\na x a\na y b\nb x a\nb y a\n");
    }
}