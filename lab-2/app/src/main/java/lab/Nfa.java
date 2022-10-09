package lab;

import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Scanner;

public class Nfa {
    private final ArrayList<NfaTransition> transitions;
    private final HashSet<String> finalStates;
    private final String initialState;
    private final int inputAlphabetLength;

    public Nfa(
        ArrayList<NfaTransition> transitions,
        HashSet<String> finalStates,
        String initialState,
        int inputAlphabetLength
    ) {
        this.transitions = transitions;
        this.finalStates = finalStates;
        this.initialState = initialState;
        this.inputAlphabetLength = inputAlphabetLength;
    }

    public static Nfa fromString(String string) {
        Scanner scanner = new Scanner(string);
        int inputAlphabetLength = scanner.nextInt();
        int stateCount = scanner.nextInt();
        String initialState = scanner.next();
        int finalStatesSize = scanner.nextInt();
        HashSet<String> finalStates = new HashSet<String>();

        for (int i = 0; i < finalStatesSize; ++i) {
            finalStates.add(scanner.next());
        }

        ArrayList<NfaTransition> transitions = new ArrayList<NfaTransition>();

        while (scanner.hasNext()) {
            transitions.add(
                new NfaTransition(scanner.next(), scanner.next().charAt(0), scanner.next())
            );
        }

        scanner.close();

        return new Nfa(transitions, finalStates, initialState, inputAlphabetLength);
    }

    public Dfa toDfa() {
        var transitions =
            new HashMap<HashSet<String>, HashMap<Character, HashSet<String>>>();
        var processedStates = new HashSet<HashSet<String>>();

        var initialState = new HashSet<String>();
        initialState.add(this.initialState);
        transitions.put(initialState, new HashMap<Character, HashSet<String>>());

        for (;;) {
            var newTransitions =
                new HashMap<HashSet<String>, HashMap<Character, HashSet<String>>>();

            for (var entry: transitions.entrySet()) {
                var state = entry.getKey();

                if (processedStates.contains(state)) {
                    continue;
                }

                for (NfaTransition transition: this.transitions) {
                    if (state.contains(transition.currentState)) {
                        var currentTransitions = entry.getValue();

                        if (!currentTransitions.containsKey(transition.character)) {
                            currentTransitions.put(
                                transition.character, new HashSet<String>()
                            );
                        }

                        currentTransitions
                            .get(transition.character)
                            .add(transition.nextState);
                    }
                }

                for (var charEntry: entry.getValue().entrySet()) {
                    if (!transitions.containsKey(charEntry.getValue())) {
                        newTransitions.put(
                            charEntry.getValue(), new HashMap<Character, HashSet<String>>()
                        );
                    }
                }

                processedStates.add(state);
            }

            if (newTransitions.isEmpty()) {
                break;
            }

            transitions.putAll(newTransitions);
        }

        HashMap<String, HashMap<Character, String>> stringTransitions =
            new HashMap<String, HashMap<Character, String>>();
        
        for (HashSet<String> state: transitions.keySet()) {
            StringBuilder builder = new StringBuilder();

            for (String subState: state) {
                builder.append(subState);
                builder.append("'");
            }

            stringTransitions.put(
                builder.toString(), new HashMap<Character, String>()
            );

            for (char character: transitions.get(state).keySet()) {
                HashSet<String> nextState = transitions.get(state).get(character);
                StringBuilder nextBuilder = new StringBuilder();

                for (String subState: nextState) {
                    nextBuilder.append(subState);
                    nextBuilder.append("'");
                }

                stringTransitions
                    .get(builder.toString())
                    .put(character, nextBuilder.toString());
            }
        }

        HashSet<String> finalStates = new HashSet<String>();

        for (HashSet<String> state: transitions.keySet()) {
            for (String finalState: this.finalStates) {
                if (state.contains(finalState)) {
                    StringBuilder builder = new StringBuilder();

                    for (String subState: state) {
                        builder.append(subState);
                        builder.append("'");
                    }

                    finalStates.add(builder.toString());
                }
            }
        }

        return new Dfa(
            stringTransitions, finalStates, this.initialState + "'", inputAlphabetLength
        );
    }
}
