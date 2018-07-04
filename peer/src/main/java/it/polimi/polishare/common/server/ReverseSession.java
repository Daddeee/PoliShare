package it.polimi.polishare.common.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ReverseSession extends Remote {
    void ping() throws RemoteException;
    void chatNotify(Message message) throws RemoteException;
}