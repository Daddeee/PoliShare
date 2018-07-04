package it.polimi.polishare.peer.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import it.polimi.polishare.common.server.Message;
import it.polimi.polishare.peer.utils.CurrentSession;
import it.polimi.polishare.peer.utils.GroupChat;
import it.polimi.polishare.peer.utils.Notifications;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;

import java.rmi.RemoteException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;

public class ChatController {
    @FXML
    private VBox root;
    @FXML
    private VBox messageBox;
    @FXML
    private HBox sendBox;
    @FXML
    private TextField messageField;

    private static Collection<Message> messages;

    public void initData(double height, double width) {
        GroupChat.setChatController(this);

        root.setPrefHeight(height);
        root.setMaxHeight(height);
        root.setMinHeight(height);

        root.setPrefWidth(width);
        root.setMaxWidth(width);
        root.setMinWidth(width);

        sendBox.setPrefWidth(width - 10.0);
        sendBox.setMaxWidth(width - 10.0);
        sendBox.setMinWidth(width - 10.0);

        messageBox.setPrefWidth(width - 15.0);
        messageBox.setMaxWidth(width - 15.0);
        messageBox.setMinWidth(width - 15.0);
        VBox.setMargin(messageBox, new Insets(5.0));

        initMessages();
    }

    @FXML
    public void send() {
        if(messageField.getText().isEmpty()) return;

        try {
            CurrentSession.getSession().sendMessage(new Message(CurrentSession.getCurrentUsername(), messageField.getText()));
            messageField.clear();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void initMessages(){
        messages = GroupChat.getInstance().getMessages();

        messageBox.getChildren().clear();
        for(Message m : messages) {
            addMessage(m);
        }
    }

    public void addMessage(Message m) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM-dd HH:mm:ss");
        VBox messageVBox = new VBox();

        VBox.setMargin(messageVBox, new Insets(5.0));

        Label authorLabel = new Label(m.getAuthor() + " " + dtf.format(m.getTimeStamp()));
        authorLabel.setStyle("-fx-font-size: 15.0; -fx-text-fill: GREY;");

        Separator separator = new Separator();
        separator.setOrientation(Orientation.HORIZONTAL);

        Label bodyLabel = new Label(m.getBody());
        bodyLabel.setWrapText(true);
        bodyLabel.setStyle("-fx-font-size: 20.0;");

        messageVBox.getChildren().addAll(authorLabel, separator, bodyLabel);
        messageVBox.setSpacing(5.0);
        messageBox.getChildren().add(messageVBox);
    }
}
