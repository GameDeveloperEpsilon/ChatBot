package window;

import chatBot.ChatBotInterface;
import utils.ResourceHandler;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MainWindow extends JFrame implements WindowInterface {

    private JPanel mainPanel;
    private JTextArea chatHistory;
    private JTextArea logHistory;
    private JTextField textEntry;
    private JButton submit;
    private JMenuItem saveChatHistory;
    private JLabel imageRegion;
    private JMenuItem saveLogHistory;
    private JMenuItem about;

    private final ResourceHandler resourceHandler;

    public MainWindow(ResourceHandler resourceHandler) {
        this.resourceHandler = resourceHandler;
        setTitle("ChatBot Prototype");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setContentPane(this.mainPanel);
        setResizable(false);
        setVisible(true);

        chatHistory.append("\n");
        logHistory.append("\n");

        saveChatHistory.addActionListener(e -> {

            try (FileWriter writer = new FileWriter("res/chatHistory.txt")) {
                writer.write(chatHistory.getText());
            } catch (IOException ex) {
                addLogItem("Failed to save chat history to file");
                File directory = new File("/res");
                if (!directory.exists()) {
                    if (directory.mkdirs()) {
                        addLogItem("Issue may have been resolved. Try again.");
                    }
                }
            }
        });
        saveLogHistory.addActionListener(e -> {

            try (FileWriter writer = new FileWriter("res/logHistory.txt")) {
                writer.write(logHistory.getText());
            } catch (IOException ex) {
                addLogItem("Failed to save log history to file");
                File directory = new File("/res");
                if (!directory.exists()) {
                    if (directory.mkdirs()) {
                        addLogItem("Issue may have been resolved. Try again.");
                    }
                }
            }
        });
        about.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "This is a prototype for a future ChatBot application."));

    }

    /**
     * Appends a string to the chat history area.
     * @param chatItem the string to be appended
     */
    @Override
    public void addChatItem(String chatItem) {
        chatHistory.append(chatItem + '\n');
    }

    /**
     * Appends a string to the log history area.
     * @param logItem the string to be appended
     */
    @Override
    public void addLogItem(String logItem) {
        logHistory.append(logItem + '\n');
    }

    /**
     * Changes the image based on the type provided
     * @param type the type of image to be displayed
     */
    @Override
    public void changeImage(String type) {
        ImageIcon currentFace = (ImageIcon) imageRegion.getIcon();
        currentFace.setImage(resourceHandler.getImage(type));
        revalidate();
        repaint();
    }

    /**
     * Modifies the action listener for the submit button to affect a chatbot
     * @param chatBot the chatBot to be bound
     */
    @Override
    public void bindChatBot(ChatBotInterface chatBot) {
        submit.addActionListener(e -> {
            addLogItem("Submitted Entry");
            String prompt = textEntry.getText();
            addChatItem("User: " + prompt);
            chatBot.addPrompt(prompt);
        });
    }

    /**
     * Initializes imageRegion with default image
     */
    private void createUIComponents() {
        imageRegion = new JLabel(new ImageIcon(resourceHandler.getImage("happy")));
    }
}