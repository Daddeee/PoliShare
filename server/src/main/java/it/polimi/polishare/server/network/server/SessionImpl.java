package it.polimi.polishare.server.network.server;

import it.polimi.polishare.common.server.Session;
import it.polimi.polishare.server.App;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class SessionImpl extends UnicastRemoteObject implements Session {
    private String username;

    public SessionImpl(String username) throws RemoteException {
        this.username = username;
    }

    @Override
    public String getServerDHTName() throws RemoteException {
        return App.DHT_NAME;
    }

    @Override
    public void logout() throws RemoteException {
        SessionFactoryImpl.activeSessions.remove(username);
        UnicastRemoteObject.unexportObject(this, true);
    }
}
