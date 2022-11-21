package launcher;

import chatBot.ChatBot;
import chatBot.ChatBotInterface;
import utils.ResourceHandler;
import window.MainWindow;

public class App {

    /**
     * Entry point
     * @param args command line arguments - not used
     */
    public static void main(String[] args) {
        ResourceHandler resourceHandler = new ResourceHandler();
        MainWindow mainWindow = new MainWindow(resourceHandler);
        resourceHandler.setLoggingContext(mainWindow);

        ChatBotInterface chatBot = new ChatBot(resourceHandler, mainWindow);
        chatBot.addPrompt("Hello");
        mainWindow.bindChatBot(chatBot);
        Thread chatBotThread = new Thread(chatBot);
        chatBotThread.start();

    }
}
