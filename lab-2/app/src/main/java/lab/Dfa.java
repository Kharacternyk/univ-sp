package lab;

import java.util.HashMap;
import java.util.HashSet;

public class Dfa {
    private final HashMap<String, HashMap<Character, String>> transitions;
    private final HashSet<String> finalStates;
    private final String initialState;
    private final int inputAlphabetLength;

    public Dfa(
        HashMap<String, HashMap<Character, String>> transitions,
        HashSet<String> finalStates,
        String initialState,
        int inputAlphabetLength
    ) {
        this.transitions = transitions;
        this.finalStates = finalStates;
        this.initialState = initialState;
        this.inputAlphabetLength = inputAlphabetLength;
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

        builder.append(inputAlphabetLength);
        builder.append("\n");
        //FIXME
        builder.append(transitions.size());
        builder.append("\n");
        builder.append(initialState);
        builder.append("\n");
        builder.append(finalStates.size());

        for (String finalState: finalStates) {
            builder.append(" ");
            builder.append(finalState);
        }

        builder.append("\n");

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
