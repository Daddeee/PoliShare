package it.polimi.polishare.common.unauthenticated;

import it.polimi.polishare.common.NoteMetaData;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.function.Predicate;

public interface UnauthenticatedSession extends Remote {
    String login(String username, String password) throws RemoteException, LoginFailedException;
    void register(String email, String username) throws RemoteException, RegistrationFailedException;
    List<NoteMetaData> query(Predicate<NoteMetaData> predicate) throws RemoteException;
}
