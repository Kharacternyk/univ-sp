package lab;

public class NfaTransition {
    public final String currentState;
    public final char character;
    public final String nextState;

    public NfaTransition(String currentState, char character, String nextState) {
        this.currentState = currentState;
        this.character = character;
        this.nextState = nextState;
    }
}
