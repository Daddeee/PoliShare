package it.polimi.polishare.server.network.server;


import it.polimi.polishare.common.AddFailedException;
import it.polimi.polishare.common.DHT.DHTException;
import it.polimi.polishare.common.DHT.model.NoteMetaData;
import it.polimi.polishare.common.server.exceptions.LoginFailedException;
import it.polimi.polishare.common.server.exceptions.RegistrationFailedException;
import it.polimi.polishare.common.server.Session;
import it.polimi.polishare.common.server.SessionFactory;
import it.polimi.polishare.server.App;
import it.polimi.polishare.server.utils.Mailer;
import it.polimi.polishare.server.utils.RandomString;
import it.polimi.polishare.server.model.UserDAO;

import javax.mail.MessagingException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class SessionFactoryImpl extends UnicastRemoteObject implements SessionFactory {
    public static final ConcurrentHashMap<String, Session> activeSessions = new ConcurrentHashMap<>();

    private UserDAO dao;
    private RandomString randomString;

    public SessionFactoryImpl() throws RemoteException {
        this.dao = new UserDAO();
        this.randomString = new RandomString(10);
    }

    @Override
    public Session login(String username, String password) throws LoginFailedException {
        if(dao.checkLogin(username, password)) {
            if(activeSessions.get(username) != null) {
                try {
                    activeSessions.get(username).getReverseSession().ping();
                    throw new LoginFailedException("Questo utente risulta già loggato nel sistema.");
                } catch (RemoteException e) {}
            }

            try{
                Session newSession = new SessionImpl(username);
                activeSessions.put(username, newSession);
                return newSession;
            } catch (RemoteException e) {
                throw new LoginFailedException("Errore di connessione.");
            }
        } else {
            throw new LoginFailedException("Combinazione username/password non valida.");
        }
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
