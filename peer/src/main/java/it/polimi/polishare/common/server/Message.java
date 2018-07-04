package it.polimi.polishare.common.server;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Message implements Serializable {
    private String author;
    private String body;
    private LocalDateTime timeStamp;

    public Message(String author, String body) {
        this.author = author;
        this.body = body;
        this.timeStamp = LocalDateTime.now();
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }
}