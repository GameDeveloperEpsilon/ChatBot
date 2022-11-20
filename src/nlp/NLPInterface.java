package nlp;

public interface NLPInterface {
    void addPrompt(String prompt);
    String moodStateChange(String input);
    String respond();
    void loadDialogue();
}
