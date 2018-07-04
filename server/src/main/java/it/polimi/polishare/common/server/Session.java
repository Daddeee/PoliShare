package it.polimi.polishare.common.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;

public interface Session extends Remote {
    String getServerDHTName() throws RemoteException;
    void logout() throws RemoteException;
    void setReverseSession(ReverseSession reverseSession) throws RemoteException;
    ReverseSession getReverseSession() throws RemoteException;

    void sendMessage(Message message) throws RemoteException;
}
