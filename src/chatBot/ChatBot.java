package chatBot;

import nlp.NLPInterface;
import nlp.NaturalLanguageProcessor;
import utils.ResourceHandler;
import window.WindowInterface;

public class ChatBot implements ChatBotInterface {

    private final ResourceHandler resourceHandler;
    private final WindowInterface context;
    private final NLPInterface naturalLanguageProcessor;
    private String name, mood;

    public ChatBot(ResourceHandler resourceHandler, WindowInterface context) {

        this.resourceHandler = resourceHandler;
        this.context = context;
        this.naturalLanguageProcessor = new NaturalLanguageProcessor(resourceHandler, this);

        loadAttributes();

    }

    private void loadAttributes() {
        resourceHandler.loadAttributes(this);
    }

    @Override
    public void addPrompt(String prompt) {
        naturalLanguageProcessor.addPrompt(prompt);
    }

    @Override
    public void moodStateChange(String input) {
        switch (input) {
            case "you are happy" -> {
                mood = "happy";
                context.changeImage("happy");
            }
            case "you are grumpy" -> {
                mood = "grumpy";
                context.changeImage("grumpy");
            }
            case "you are sad" -> {
                mood = "sad";
                context.changeImage("sad");
            }
            case "you are annoyed" -> {
                mood = "annoyed";
                context.changeImage("annoyed");
            }
        }
        //return naturalLanguageProcessor.moodStateChange(input);
    }

    @Override
    public String respond() {
        return naturalLanguageProcessor.respond();
    }

    @Override
    public String getMood() {
        return mood;
    }

    @Override
    public void setMood(String mood) {
        this.mood = mood;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

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
