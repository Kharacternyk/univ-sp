package lab;

import java.util.ArrayList;

class WordFilter {
    private final StringSink sink;
    private StringBuilder currentWord;
    private boolean isPreviousLetterConsonant;
    private boolean doesWordPassFilter;

    public WordFilter(StringSink sink) {
        this.sink = sink;
        currentWord = new StringBuilder();
    }
    
    public void feedCharacter(char character) {
        if (isLetter(character)) {
            currentWord.append(character);

            boolean isVowel = isVowel(character);

            if (!isVowel && isPreviousLetterConsonant) {
                doesWordPassFilter = true;
            }

            isPreviousLetterConsonant = !isVowel;
        } else {
            feedCurrentWord();
        }
    }

    public void feedCurrentWord() {
        if (doesWordPassFilter) {
            sink.feedString(currentWord.toString());
        }

        isPreviousLetterConsonant = false;
        doesWordPassFilter = false;
        currentWord.setLength(0);
    }

    private boolean isLetter(char character) {
        return character >= 'A' && character <= 'Z' || character >= 'a' && character <= 'z';
    }

    private boolean isVowel(char character) {
        return "aouyeiAOUYEI".contains(String.valueOf(character));
    }
}
