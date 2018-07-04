package it.polimi.polishare.common.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Session extends Remote {
    String getServerDHTName() throws RemoteException;
    void logout() throws RemoteException;
}
