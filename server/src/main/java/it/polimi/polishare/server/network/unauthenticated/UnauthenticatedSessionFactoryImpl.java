package it.polimi.polishare.server.network.unauthenticated;

import it.polimi.polishare.common.unauthenticated.UnauthenticatedSession;
import it.polimi.polishare.common.unauthenticated.UnauthenticatedSessionFactory;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class UnauthenticatedSessionFactoryImpl extends UnicastRemoteObject implements UnauthenticatedSessionFactory {
    public UnauthenticatedSessionFactoryImpl() throws RemoteException {}

    @Override
    public UnauthenticatedSession getSession() throws RemoteException{
        return new UnauthenticatedSessionImpl();
    }
}
