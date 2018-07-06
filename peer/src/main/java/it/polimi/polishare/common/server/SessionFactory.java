package it.polimi.polishare.common.server;

import it.polimi.polishare.common.DHT.model.NoteMetaData;
import it.polimi.polishare.common.server.exceptions.LoginFailedException;
import it.polimi.polishare.common.server.exceptions.RegistrationFailedException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.function.Predicate;

public interface SessionFactory extends Remote {
    Session login(String username, String password) throws RemoteException, LoginFailedException;
    void register(String email, String username) throws RemoteException, RegistrationFailedException;
    List<NoteMetaData> query(Predicate<NoteMetaData> predicate) throws RemoteException;
}
