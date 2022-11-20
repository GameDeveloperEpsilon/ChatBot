package chatBot;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import utils.Phrase;
import window.WindowInterface;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.util.*;

public class ChatBot extends Thread implements ChatBotInterface {
    private final WindowInterface context;
    private final TreeMap<String, ArrayList<String>> responseMap;
    private final PriorityQueue<Phrase> prompts;
    private String name, mood;

    public ChatBot(WindowInterface context) {

        this.context = context;

        responseMap = new TreeMap<>();
        prompts = new PriorityQueue<>();

        loadAttributes();
        loadDialogue();

    }

    private void createFile(String filePath, String fileName) {
        try {
            boolean made = new File(filePath).createNewFile();
            if (made) {
                context.addLogItem(String.format("The file %s was made.", fileName));
            } else {
                context.addLogItem(String.format("The file %s already exists", fileName));
            }
        } catch (IOException ex) {
            context.addLogItem(String.format("The file %s could not be made.", fileName));
        }
    }

    @SuppressWarnings("unchecked")
    private void initializeVariables(String filePath) {
        try (FileWriter writer = new FileWriter(filePath)){
            JSONObject chatbot = new JSONObject();
            name = JOptionPane.showInputDialog("What is my name?");
            mood = JOptionPane.showInputDialog("How am I feeling?");
            chatbot.put("name", name);
            chatbot.put("mood", mood);
            writer.write(chatbot.toJSONString());
            context.addLogItem("The file chatbot_data.json was fixed.");
        } catch (IOException ex) {
            context.addLogItem("The file chatbot_data.json could not be fixed");
        }
    }

    private void loadAttributes() {
        URL file = getClass().getResource("/chatbot_data.json");
        assert file != null;
        String filePath = file.getPath();
        try (FileReader fileReader = new FileReader(filePath)) {

            JSONParser parser = new JSONParser();
            JSONObject chatBotData = (JSONObject) parser.parse(fileReader);

            name = (String) chatBotData.get("name");
            mood = (String) chatBotData.get("mood");

        } catch (FileNotFoundException e) {
            context.addLogItem("The file chatbot_data.json could not be found.");
            createFile(filePath, "chatbot_data.json");
            initializeVariables(filePath);
        } catch (IOException e) {
            context.addLogItem("The file chatbot_data.json could not be read.");
            initializeVariables(filePath);
        } catch (ParseException e) {
            context.addLogItem("The file chatbot_data.json could not be parsed.");
            initializeVariables(filePath);
        }
    }

    private void loadDialogue() {
        URL file = getClass().getResource("/dialogue.csv");
        assert file != null;
        String filePath = file.getPath();
        try (FileReader fileReader = new FileReader(filePath);
             Scanner reader = new Scanner(fileReader)) {
            while (reader.hasNextLine()) {
                String line = reader.nextLine().toLowerCase();
                this.context.addLogItem(line);
                String[] keyValues = line.split(",");
                if (keyValues.length == 0) {
                    continue;
                }
                String key = keyValues[0];
                ArrayList<String> values = new ArrayList<>(List.of(keyValues));
                values.remove(0);
                responseMap.put(key, values);
            }
        } catch (FileNotFoundException e) {
            context.addLogItem("The file dialogue.csv could not be found.");
            createFile(filePath, "dialogue.csv");
        } catch (IOException e) {
            context.addLogItem("The file dialogue.csv could not be read.");
        }
    }

    @Override
    public void addPrompt(String prompt) {
        if (prompt == null) {
            prompt = "";
        }
        prompts.add(new Phrase(prompt));
    }

    private String moodStateChange(String input) {
        switch (input) {
            case "you are happy" -> {
                mood = "happy";
                context.changeImage("happy");
                return "I am happy.";
            }
            case "you are grumpy" -> {
                mood = "grumpy";
                context.changeImage("grumpy");
                return "I am grumpy.";
            }
            case "you are sad" -> {
                mood = "sad";
                context.changeImage("sad");
                return "I am sad.";
            }
            case "you are annoyed" -> {
                mood = "annoyed";
                context.changeImage("annoyed");
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
        int moodNum = switch (mood) {
            case "annoyed" -> 1;
            case "sad" -> 2;
            case "grumpy" -> 3;
            default -> 0;
        };
        String rawResponse = candidateResponses.get(moodNum);
        String response = rawResponse;
        if (rawResponse.contains("%s=name")) {
            response = rawResponse.replace("%s=name", name);
        }
        if (rawResponse.contains("%s=mood")) {
            response = rawResponse.replace("%s=mood", mood);
        }
        return response;
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
