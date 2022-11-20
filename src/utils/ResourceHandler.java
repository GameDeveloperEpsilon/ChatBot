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

    private WindowInterface loggingContext;

    private final TreeMap<String, BufferedImage> atlas;

    public ResourceHandler() {
        atlas = new TreeMap<>();
        // TODO Initialize Resources
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

    @SuppressWarnings("unchecked")
    private void initializeVariables(ChatBotInterface chatBotInterface, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)){
            JSONObject chatbot = new JSONObject();
            String name = JOptionPane.showInputDialog("What is my name?");
            String mood = JOptionPane.showInputDialog("How am I feeling?");

            chatBotInterface.setName(name);
            chatBotInterface.setMood(mood);

            chatbot.put("name", name);
            chatbot.put("mood", mood);
            writer.write(chatbot.toJSONString());
            loggingContext.addLogItem("The file chatbot_data.json was fixed.");
        } catch (IOException ex) {
            loggingContext.addLogItem("The file chatbot_data.json could not be fixed");
        }
    }

    public void loadAttributes(ChatBotInterface chatBotInterface) {
        URL file = getClass().getResource("/chatbot_data.json");
        assert file != null;
        String filePath = file.getPath();
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
            initializeVariables(chatBotInterface, filePath);
        } catch (IOException e) {
            loggingContext.addLogItem("The file chatbot_data.json could not be read.");
            initializeVariables(chatBotInterface, filePath);
        } catch (ParseException e) {
            loggingContext.addLogItem("The file chatbot_data.json could not be parsed.");
            initializeVariables(chatBotInterface, filePath);
        }
    }

    public TreeMap<String, ArrayList<String>> loadDialogue() {
        URL file = getClass().getResource("/dialogue.csv");
        assert file != null;
        String filePath = file.getPath();
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
