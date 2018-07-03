package it.polimi.polishare.server.network.unauthenticated;

import it.polimi.polishare.common.AddFailedException;
import it.polimi.polishare.common.DHT.DHTException;
import it.polimi.polishare.common.NoteMetaData;
import it.polimi.polishare.common.unauthenticated.LoginFailedException;
import it.polimi.polishare.common.unauthenticated.RegistrationFailedException;
import it.polimi.polishare.common.unauthenticated.UnauthenticatedSession;
import it.polimi.polishare.server.App;
import it.polimi.polishare.server.Utils.Mailer;
import it.polimi.polishare.server.Utils.RandomString;
import it.polimi.polishare.server.model.UserDAO;

import javax.mail.MessagingException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.function.Predicate;

public class UnauthenticatedSessionImpl extends UnicastRemoteObject implements UnauthenticatedSession {
    private UserDAO dao;
    private RandomString randomString;

    public UnauthenticatedSessionImpl() throws RemoteException {
        this.dao = new UserDAO();
        this.randomString = new RandomString(10);
    }

    @Override
    public String login(String username, String password) throws LoginFailedException {
        if(dao.checkLogin(username, password))
            return App.DHT_NAME;
        else
            throw new LoginFailedException("Combinazione username/password non valida.");
    }

    @Override
    public void register(String email, String username) throws RegistrationFailedException {
        String pwd = randomString.nextString();
        try {
            dao.create(username, pwd, email);
            Mailer.sendCredentials(username, pwd, email);
        } catch (AddFailedException e) {
            throw new RegistrationFailedException(e.getMessage());
        } catch (MessagingException e) {
            dao.delete(username);
            throw new RegistrationFailedException(e.getMessage());
        }
    }

    @Override
    public List<NoteMetaData> query(Predicate<NoteMetaData> predicate) throws RemoteException {
        try {
            return App.dht.query(predicate);
        } catch (DHTException e) {
            e.printStackTrace();
            throw new RemoteException(e.getMessage());
        }
    }
}
