package it.polimi.polishare.common.chord;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface Node extends Remote {
    String getName() throws RemoteException;

    Key getKey() throws RemoteException;

    Node getPred() throws RemoteException;

    Node[] getSuccList() throws RemoteException;

    Node[] getFingers() throws RemoteException;

    Node lookupSuccessor(Key k) throws RemoteException;

    void create() throws RemoteException;

    void join(Node known) throws RemoteException;

    void stabilize() throws RemoteException;

    void rectify(Node newPred) throws RemoteException;

    void checkPred() throws RemoteException;

    void fixFingers() throws RemoteException;

    void replicate() throws RemoteException;

    void stop() throws RemoteException;

    void storeReply(Key key, Map<Key, Object> reply) throws RemoteException;

    Object getStored(Key k) throws RemoteException;

    void putStored(Key k, Object value) throws RemoteException;

    void removeStored(Key k) throws RemoteException;

    List<Object> broadcast(Node limit) throws RemoteException;

    Object get(Key k) throws RemoteException;

    List<Object> getAll() throws RemoteException;

    void repliedPut(Key k, Object value) throws RemoteException;

    void repliedRemove(Key k) throws RemoteException;
}
