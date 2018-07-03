package it.polimi.polishare.common.unauthenticated;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface UnauthenticatedSessionFactory extends Remote {
    UnauthenticatedSession getSession() throws RemoteException;

}
