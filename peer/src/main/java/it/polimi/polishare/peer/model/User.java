package it.polimi.polishare.peer.model;

public class User {
    private static User ourInstance = new User();

    public static User getInstance() {
        return ourInstance;
    }

    private String username;

    private User() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
