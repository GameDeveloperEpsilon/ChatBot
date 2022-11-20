package chatBot;

public interface ChatBotInterface extends Runnable {
    void addPrompt(String prompt);
    void moodStateChange(String input);
    String respond();
    String getMood();
    void setMood(String mood);
    String getName();
    void setName(String name);
}
