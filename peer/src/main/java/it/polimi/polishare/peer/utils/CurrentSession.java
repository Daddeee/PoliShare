package it.polimi.polishare.peer.utils;

import it.polimi.polishare.common.DHT.DHT;
import it.polimi.polishare.common.NoteMetaData;
import it.polimi.polishare.common.server.Session;

public class CurrentSession {
    private static Session currentSession = null;
    private static String currentUsername = null;
    private static DHT<NoteMetaData> dht = null;

    private CurrentSession() {}

    public static Session getSession() {
        return currentSession;
    }

    public static String getCurrentUsername() {
        return currentUsername;
    }

    public static DHT<NoteMetaData> getDHT() {
        return dht;
    }

    public static void setSession(Session currentSession) {
        CurrentSession.currentSession = currentSession;
    }

    public static void setUsername(String username) {
        CurrentSession.currentUsername = username;
    }

    public static void setDHT(DHT<NoteMetaData> dht) {
        CurrentSession.dht = dht;
    }
}
