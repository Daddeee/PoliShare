package it.polimi.polishare.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Downloader extends Remote {
    byte[] download(String title) throws RemoteException;
    void ping() throws RemoteException;
}
