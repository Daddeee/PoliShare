package it.polimi.polishare.peer.utils;

import it.polimi.polishare.common.server.Message;
import it.polimi.polishare.peer.controller.ChatController;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.Collection;

public class GroupChat {
    private static GroupChat ourInstance = new GroupChat();
    private static ChatController chatController = null;

    public static GroupChat getInstance() {
        return ourInstance;
    }

    public static void setChatController(ChatController chatController) {
        GroupChat.chatController = chatController;
    }

    private FixedSizeQueue<Message> messageQueue;

    private GroupChat() {
        this.messageQueue = new FixedSizeQueue<>(100);
    }

    public void addMessage(Message msg) {
        messageQueue.add(msg);
        if(chatController != null) Platform.runLater(() -> chatController.addMessage(msg));
    }

    public Collection<Message> getMessages() {
        return new ArrayList<>(messageQueue.getAll());
    }

    public void reset() {
        ourInstance = new GroupChat();
    }
}
