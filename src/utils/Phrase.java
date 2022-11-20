package utils;

public class Phrase implements Comparable<Phrase> {
    private final String phrase;
    private final int value;

    public Phrase(String phrase) {
        this.phrase = phrase;
        value = switch (phrase.substring(phrase.length()-1)) {
            case "!" -> 1;
            case "?" -> 2;
            case "." -> 3;
            default -> 4;
        };
    }

    public String getPhrase() {
        return phrase;
    }

    @Override
    public int compareTo(Phrase other) {
        return Integer.compare(value, other.value);
    }
}
