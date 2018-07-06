package it.polimi.polishare.peer.network.server;

import it.polimi.polishare.common.server.Message;
import it.polimi.polishare.common.server.ReverseSession;
import it.polimi.polishare.peer.GroupChat;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ReverseSessionImpl extends UnicastRemoteObject implements ReverseSession {
    public ReverseSessionImpl() throws RemoteException {}

    @Override
    public void ping() {}

    @Override
    public void chatNotify(Message message) {
        GroupChat.getInstance().addMessage(message);
    }
}
