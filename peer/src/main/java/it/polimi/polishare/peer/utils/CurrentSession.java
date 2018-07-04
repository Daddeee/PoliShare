package it.polimi.polishare.peer.utils;

import it.polimi.polishare.common.DHT.DHT;
import it.polimi.polishare.common.DHT.DHTException;
import it.polimi.polishare.common.NoteMetaData;
import it.polimi.polishare.common.server.ReverseSession;
import it.polimi.polishare.common.server.Session;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class CurrentSession {
    private static Session currentSession = null;
    private static String currentUsername = null;
    private static DHT<NoteMetaData> dht = null;
    private static ReverseSession reverseSession = null;

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

    public static ReverseSession getReverseSession() {
        return reverseSession;
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

    public static void setReverseSession(ReverseSession reverseSession) {
        CurrentSession.reverseSession = reverseSession;
    }

    public static void shutDown() throws RemoteException, DHTException {
        if(currentSession != null) currentSession.logout();
        if(dht != null) dht.leave();
        if(reverseSession != null) UnicastRemoteObject.unexportObject(reverseSession, true);

        currentSession = null;
        currentUsername = null;
        dht = null;
        reverseSession = null;
    }
}
