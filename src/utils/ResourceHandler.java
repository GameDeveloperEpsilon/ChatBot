package utils;

import chatBot.ChatBot;
import chatBot.ChatBotInterface;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
    public final String chatBot_dataFilePath;
    public final String dialogueFilePath;

    public ResourceHandler() {
        atlas = new TreeMap<>();

        URL chatBot_dataFile = getClass().getResource("/chatBot_data.json");
        assert chatBot_dataFile != null;
        chatBot_dataFilePath = chatBot_dataFile.getPath();

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

    public void saveChatBotVariables(ChatBotInterface chatBot) {

        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();

        Gson gson = builder.create();
        String chatBotJSON = gson.toJson(chatBot);

        // Write to json file
        try (FileWriter writer = new FileWriter(chatBot_dataFilePath)) {

            writer.write(chatBotJSON);
            loggingContext.addLogItem("The file chatBot_data.json was updated.");

        } catch (IOException ex) {
            loggingContext.addLogItem("The file chatBot_data.json could not be updated.");
        }
    }

    public void setChatBotVariables(ChatBotInterface chatBot) {
        getChatBotVariablesFromUser(chatBot);
        saveChatBotVariables(chatBot);
    }

    public void loadAttributes(ChatBotInterface chatBot, String type) {

        String filePath = chatBot_dataFilePath;

        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();

        Gson gson = builder.create();

        try (FileReader fileReader = new FileReader(filePath)) {

            if (type.equals("ChatBot")) {
                ChatBot temp = gson.fromJson(fileReader, ChatBot.class);
                chatBot.setName(temp.getName());
                chatBot.setMood(temp.getMood());
            } else {
                throw new RuntimeException("Load Attributes: ChatBot type does not exist!");
            }

        } catch (FileNotFoundException e) {
            loggingContext.addLogItem("The file chatBot_data.json could not be found.");
            createFile(filePath, "chatBot_data.json");
            ChatBotInterface cBot = ChatBot.FromDefaultFactory(this, loggingContext);
            setChatBotVariables(cBot);
        } catch (IOException e) {
            loggingContext.addLogItem("The file chatBot_data.json could not be read.");
            ChatBotInterface cBot = ChatBot.FromDefaultFactory(this, loggingContext);
            setChatBotVariables(cBot);
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
