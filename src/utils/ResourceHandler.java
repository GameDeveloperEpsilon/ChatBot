package utils;

import chatBot.ChatBotInterface;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import window.WindowInterface;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;

public class ResourceHandler {

    //public static final String resourcePath = System.getProperty("user.home") + "/Appdata/Local/MyChatBot";

    private WindowInterface loggingContext;
    private final TreeMap<String, BufferedImage> atlas;
    public final String chatbot_dataFilePath;
    public final String dialogueFilePath;

    public ResourceHandler() {
        atlas = new TreeMap<>();

        URL chatbot_dataFile = getClass().getResource("/chatbot_data.json");
        assert chatbot_dataFile != null;
        chatbot_dataFilePath = chatbot_dataFile.getPath();

        URL dialogueFile = getClass().getResource("/dialogue.csv");
        assert dialogueFile != null;
        dialogueFilePath = dialogueFile.getPath();

        // Initialize Resources
        loadImages();
    }

    private void createFile(String filePath, String fileName) {
        try {
            boolean made = new File(filePath).createNewFile();
            if (made) {
                loggingContext.addLogItem(String.format("The file %s was made.", fileName));
            } else {
                loggingContext.addLogItem(String.format("The file %s already exists", fileName));
            }
        } catch (IOException ex) {
            loggingContext.addLogItem(String.format("The file %s could not be made.", fileName));
        }
    }

    public void getChatBotVariablesFromUser(ChatBotInterface chatBot) {
        // Get values from user
        String name = JOptionPane.showInputDialog("What is my name?");
        String mood = JOptionPane.showInputDialog("How am I feeling?");

        // Set instance variables
        chatBot.setName(name);
        chatBot.setMood(mood);
    }

    @SuppressWarnings("unchecked")
    public void saveChatBotVariables(ChatBotInterface chatBot) {
        // Write to json file
        try (FileWriter writer = new FileWriter(chatbot_dataFilePath)){

            JSONObject chatbotJSON = new JSONObject();

            chatbotJSON.put("name", chatBot.getName());
            chatbotJSON.put("mood", chatBot.getMood());
            writer.write(chatbotJSON.toJSONString());
            loggingContext.addLogItem("The file chatbot_data.json was updated.");
        } catch (IOException ex) {
            loggingContext.addLogItem("The file chatbot_data.json could not be updated.");
        }
    }

    public void setChatBotVariables(ChatBotInterface chatBot) {
        getChatBotVariablesFromUser(chatBot);
        saveChatBotVariables(chatBot);
    }

    public void loadAttributes(ChatBotInterface chatBotInterface) {
        String filePath = chatbot_dataFilePath;
        try (FileReader fileReader = new FileReader(filePath)) {

            JSONParser parser = new JSONParser();
            JSONObject chatBotData = (JSONObject) parser.parse(fileReader);

            String name = (String) chatBotData.get("name");
            String mood = (String) chatBotData.get("mood");

            chatBotInterface.setName(name);
            chatBotInterface.setMood(mood);

        } catch (FileNotFoundException e) {
            loggingContext.addLogItem("The file chatbot_data.json could not be found.");
            createFile(filePath, "chatbot_data.json");
            setChatBotVariables(chatBotInterface);
        } catch (IOException e) {
            loggingContext.addLogItem("The file chatbot_data.json could not be read.");
            setChatBotVariables(chatBotInterface);
        } catch (ParseException e) {
            loggingContext.addLogItem("The file chatbot_data.json could not be parsed.");
            setChatBotVariables(chatBotInterface);
        }
    }

    public TreeMap<String, ArrayList<String>> loadDialogue() {
        String filePath = dialogueFilePath;
        TreeMap<String, ArrayList<String>> responseMap = new TreeMap<>();
        try (FileReader fileReader = new FileReader(filePath);
             Scanner reader = new Scanner(fileReader)) {
            while (reader.hasNextLine()) {
                String line = reader.nextLine().toLowerCase();
                loggingContext.addLogItem(line);
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
            loggingContext.addLogItem("The file dialogue.csv could not be found.");
            createFile(filePath, "dialogue.csv");
        } catch (IOException e) {
            loggingContext.addLogItem("The file dialogue.csv could not be read.");
        }
        return responseMap;
    }

    /**
     * Puts images from resource folder into treeMap atlas.
     */
    private void loadImages() {

        try {
            URL spriteURL = getClass().getResource("/SpriteSheet.png");
            int spriteWidth = 256;
            int spriteHeight = 256;
            assert spriteURL != null;
            BufferedImage spriteSheet = ImageIO.read(spriteURL);
            atlas.put("happy", spriteSheet.getSubimage(0, 0, spriteWidth, spriteHeight));
            atlas.put("sad", spriteSheet.getSubimage(spriteWidth, spriteHeight, spriteWidth, spriteWidth));
            atlas.put("annoyed", spriteSheet.getSubimage(spriteWidth, 0, spriteWidth, spriteHeight));
            atlas.put("grumpy", spriteSheet.getSubimage(0, spriteHeight, spriteWidth, spriteHeight));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Finds image corresponding to the type given.
     * @param type - the type of mood to display
     * @return the image corresponding to the mood.
     */
    public BufferedImage getImage(String type) {
        return atlas.get(type);
    }

    public void setLoggingContext(WindowInterface loggingContext) {
        this.loggingContext = loggingContext;
    }

}
