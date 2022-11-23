package nlp;

import chatBot.ChatBotInterface;
import utils.Phrase;
import utils.ResourceHandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.PriorityQueue;
import java.util.TreeMap;

public class NaturalLanguageProcessor implements NLPInterface {

    private final ResourceHandler resourceHandler;
    private final ChatBotInterface context;
    private TreeMap<String, ArrayList<String>> responseMap;
    private final PriorityQueue<Phrase> prompts;

    public NaturalLanguageProcessor(ResourceHandler resourceHandler, ChatBotInterface context) {

        this.resourceHandler = resourceHandler;
        this.context = context;

        responseMap = new TreeMap<>();
        prompts = new PriorityQueue<>();

        loadDialogue();

    }

    @Override
    public void loadDialogue() {
        responseMap = resourceHandler.loadDialogue();
    }

    @Override
    public void addPrompt(String prompt) {
        if (prompt == null) {
            prompt = "";
        }
        prompts.add(new Phrase(prompt));
    }

    @Override
    public String moodStateChange(String input) {
        switch (input) {
            case "you are happy" -> {
                context.moodStateChange("you are happy");
                return "I am happy.";
            }
            case "you are grumpy" -> {
                context.moodStateChange("you are grumpy");
                return "I am grumpy.";
            }
            case "you are sad" -> {
                context.moodStateChange("you are sad");
                return "I am sad.";
            }
            case "you are annoyed" -> {
                context.moodStateChange("you annoyed");
                return "I am annoyed.";
            }
            default -> {
                return null;
            }
        }
    }

    @Override
    public String respond() {
        // Handle being not talked to
        if (prompts.size() == 0) {
            return "Are you there?";
        }

        // Get first prompt
        String prompt = prompts.poll().getPhrase().toLowerCase();

        // Handle mood changing phrase
        String answer = moodStateChange(prompt);
        if (answer != null)
            return answer;

        // Get response candidates
        ArrayList<String> candidateResponses = responseMap.get(prompt);

        // Handle incomprehensible phrase
        if (candidateResponses == null) {
            return "I do not understand you.";
        }

        // Handle comprehensible phrase
        //int responseNumber = new Random(System.currentTimeMillis()).nextInt(0, candidateResponses.size());
        int moodNum = switch (context.getMood()) {
            case "annoyed" -> 1;
            case "sad" -> 2;
            case "grumpy" -> 3;
            default -> 0;
        };
        String response = candidateResponses.get(moodNum);
        if (response.contains("%s=name")) {
            response = response.replace("%s=name", context.getName());
        }
        if (response.contains("%s=mood")) {
            response = response.replace("%s=mood", context.getMood());
        }
        if (response.contains("%s=time")) {
            response = response.replace("%s=time", new Date().toString());
        }
        return response;
    }

}
