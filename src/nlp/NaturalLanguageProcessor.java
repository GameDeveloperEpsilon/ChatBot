package nlp;

import chatBot.ChatBotInterface;
import utils.Phrase;
import utils.ResourceHandler;

import java.util.ArrayList;
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
        if (prompts.size() == 0) {
            return "Are you there?";
        }
        String prompt = prompts.poll().getPhrase().toLowerCase();

        String answer = moodStateChange(prompt);
        if (answer != null)
            return answer;

        ArrayList<String> candidateResponses = responseMap.get(prompt);
        if (candidateResponses == null) {
            return "I do not understand you.";
        }
        //int responseNumber = new Random(System.currentTimeMillis()).nextInt(0, candidateResponses.size());
        int moodNum = switch (context.getMood()) {
            case "annoyed" -> 1;
            case "sad" -> 2;
            case "grumpy" -> 3;
            default -> 0;
        };
        String rawResponse = candidateResponses.get(moodNum);
        String response = rawResponse;
        if (rawResponse.contains("%s=name")) {
            response = rawResponse.replace("%s=name", context.getName());
        }
        if (rawResponse.contains("%s=mood")) {
            response = rawResponse.replace("%s=mood", context.getMood());
        }
        return response;
    }

}
