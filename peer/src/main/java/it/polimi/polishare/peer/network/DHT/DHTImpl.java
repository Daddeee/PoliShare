package it.polimi.polishare.peer.network.DHT;

import it.polimi.polishare.common.DHT.DHT;
import it.polimi.polishare.common.DHT.DHTException;
import it.polimi.polishare.common.chord.Key;
import it.polimi.polishare.common.chord.Node;
import it.polimi.polishare.peer.network.chord.NodeImpl;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.function.Predicate;

public class DHTImpl<T> implements DHT<T> {
    private Node node;
    private Class<T> clazz;

    public DHTImpl(String name, Class<T> clazz) throws DHTException {
        this.clazz = clazz;
        try{
            this.node = new NodeImpl(name);
        } catch (RemoteException e) {
            throw new DHTException("Cannot initialize node: " + e.getMessage());
        }
    }

    @Override
    public void join(String ip, String nodeName) throws DHTException {
        try {
            Node known = getRemoteNode(ip, nodeName);
            this.node.join(known);
        } catch (RemoteException e) {
            throw new DHTException("Cannot join the given node: " + e.getMessage());
        }
    }

    @Override
    public void create() throws DHTException {
        try {
            this.node.create();
        } catch (RemoteException e) {
            throw new DHTException("Cannot create a new DHT: " + e.getMessage());
        }
    }

    @Override
    public void leave() throws DHTException {
        try {
            this.node.stop();
        } catch (RemoteException e) {
            throw new DHTException("Cannot create a new DHT: " + e.getMessage());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public T get(String key) throws DHTException {
        try{
            Key k = new Key(key);
            return (T)node.get(k);
        } catch (Exception e) {
            throw new DHTException("Cannot read the given key: " + e.getMessage());
        }
    }

    @Override
    public void put(String key, T value) throws DHTException {
        try{
            Key k = new Key(key);
            node.repliedPut(k, value);
        } catch (Exception e) {
            throw new DHTException("Cannot store value: " + e.getMessage());
        }
    }

    @Override
    public void remove(String key) throws DHTException {
        try{
            Key k = new Key(key);
            node.repliedRemove(k);
        } catch (Exception e) {
            throw new DHTException("Cannot remove the given key: " + e.getMessage());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> query(Predicate<T> predicate) throws DHTException {
        try{
            Predicate<Object> genericPredicate = o ->  clazz.isInstance(o) && predicate.test((T) o);
            return (List<T>)node.get(genericPredicate);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DHTException("Cannot broadcast read all: " + e.getMessage());
        }
    }

    private static Node getRemoteNode(String ip, String name) throws RemoteException{
        Registry registry = LocateRegistry.getRegistry(ip);
        try{
            return (Node) registry.lookup(name);
        } catch (NotBoundException e){
            e.printStackTrace();
            throw new RemoteException(e.getMessage());
        }
    }
}
