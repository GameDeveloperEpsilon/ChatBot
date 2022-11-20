package window;

import chatBot.ChatBotInterface;

public interface WindowInterface {
    void addChatItem(String chatItem);
    void addLogItem(String logItem);
    void changeImage(String type);
    void bindChatBot(ChatBotInterface chatBot);
}
