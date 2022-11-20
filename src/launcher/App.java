package launcher;

import chatBot.ChatBot;
import utils.ResourceHandler;
import window.MainWindow;

public class App {

    //public static final String resourcePath = System.getProperty("user.home") + "\\Appdata\\Local\\MyChatBot\\";

    /**
     * Entry point
     * @param args command line arguments - not used
     */
    public static void main(String[] args) {
        ResourceHandler resourceHandler = new ResourceHandler();
        MainWindow mainWindow = new MainWindow(resourceHandler);

        ChatBot chatBot = new ChatBot(mainWindow);
        chatBot.addPrompt("Hello");
        mainWindow.bindChatBot(chatBot);
        chatBot.start();

    }
}
