package chatBot;

import nlp.NLPInterface;
import nlp.NaturalLanguageProcessor;
import utils.ResourceHandler;
import window.WindowInterface;

import java.util.ArrayList;

public class ChatBot implements ChatBotInterface {

    private final ResourceHandler resourceHandler;
    private final WindowInterface context;
    private final NLPInterface naturalLanguageProcessor;
    private final ArrayList<String> moods;
    private String name, mood;

    public ChatBot(ResourceHandler resourceHandler, WindowInterface context) {

        this.resourceHandler = resourceHandler;
        this.context = context;
        this.naturalLanguageProcessor = new NaturalLanguageProcessor(resourceHandler, this);

        moods = new ArrayList<>();
        moods.add("happy");
        moods.add("sad");
        moods.add("grumpy");
        moods.add("annoyed");

        loadAttributes();

    }

    /**
     * Loads ChatBot attributes.
     */
    private void loadAttributes() {
        resourceHandler.loadAttributes(this);
    }

    /**
     * Adds a prompt to the prompt queue.
     * @param prompt - the prompt to add
     */
    @Override
    public void addPrompt(String prompt) {
        naturalLanguageProcessor.addPrompt(prompt);
    }

    /**
     * Changes mood state if conditions are valid.
     * @param input - a condition to test
     */
    @Override
    public void moodStateChange(String input) {
        String prefix = "you are ";
        if (input.startsWith(prefix)) {
            String test = input.substring(prefix.length());
            setMood(test);
            context.changeImage(getMood());
        }
    }

    /**
     * Uses a natural language processor to generate a response.
     * @return - the response
     */
    @Override
    public String respond() {
        return naturalLanguageProcessor.respond();
    }

    /**
     * Gets mood.
     * @return - mood
     */
    @Override
    public String getMood() {
        return mood;
    }

    /**
     * Sets mood if the mood is valid.
     * @param mood - the mood to check and set
     */
    @Override
    public void setMood(String mood) {
        if (mood == null)
            return;
        if (moods.contains(mood))
            this.mood = mood;
    }

    /**
     * Gets name.
     * @return - name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Sets name if it is not null.
     * @param name - a string
     */
    @Override
    public void setName(String name) {
        if (name != null)
            this.name = name;
    }

    /**
     * Runs the ChatBot by looping responding and waiting states.
     */
    @Override
    public void run() {
        int timeout = 10000;
        while (true) {

            context.addChatItem("ChatBot: " + respond());

            try {
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
                context.addLogItem("ChatBot stopped");
                break;
            }
        }

    }
}
