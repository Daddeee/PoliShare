package it.polimi.polishare.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Downloader extends Remote {
    byte[] getChunk(String title, int from, int to) throws RemoteException;
    String getMD5(String title) throws RemoteException;
    int getSize(String title) throws RemoteException;
    void ping() throws RemoteException;
}
