package it.polimi.polishare.server.network.server;

import it.polimi.polishare.common.server.Message;
import it.polimi.polishare.common.server.ReverseSession;
import it.polimi.polishare.common.server.Session;
import it.polimi.polishare.server.App;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class SessionImpl extends UnicastRemoteObject implements Session {
    private String username;
    private ReverseSession reverseSession;

    public SessionImpl(String username) throws RemoteException {
        this.username = username;
    }

    @Override
    public String getServerDHTName() {
        return App.DHT_NAME;
    }

    @Override
    public void logout() throws RemoteException {
        SessionFactoryImpl.activeSessions.remove(username);
        this.reverseSession = null;
        UnicastRemoteObject.unexportObject(this, true);
    }

    @Override
    public ReverseSession getReverseSession() {
        return reverseSession;
    }

    @Override
    public void setReverseSession(ReverseSession reverseSession) {
        this.reverseSession = reverseSession;
    }

    @Override
    public void sendMessage(Message message) throws RemoteException {
        for(Session s : SessionFactoryImpl.activeSessions.values()) {
                s.getReverseSession().chatNotify(message);
        }
    }
}
