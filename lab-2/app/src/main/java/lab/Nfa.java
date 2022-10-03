package lab;

import java.util.HashMap;
import java.util.HashSet;

public class Nfa {
    private final HashMap<String, HashMap<Character, String>> transitions;
    private final HashSet<String> finalStates;
    private final String initialState;

    public Nfa(
        HashMap<String, HashMap<Character, String>> transitions,
        HashSet<String> finalStates,
        String initialState
    ) {
        this.transitions = transitions;
        this.finalStates = finalStates;
        this.initialState = initialState;
    }

    public boolean isAccepted(String word) {
        String state = initialState;

        for (char character: word.toCharArray()) {
            state = transitions.get(state).get(character);
        }

        return finalStates.contains(state);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (String state: transitions.keySet()) {
            for (Character character: transitions.get(state).keySet()) {
                String newState = transitions.get(state).get(character);

                builder.append(state);
                builder.append(" ");
                builder.append(character);
                builder.append(" ");
                builder.append(newState);
                builder.append("\n");
            }
        }

        return builder.toString();
    }
}
