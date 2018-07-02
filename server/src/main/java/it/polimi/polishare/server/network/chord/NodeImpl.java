package it.polimi.polishare.server.network.chord;

import it.polimi.polishare.common.chord.Key;
import it.polimi.polishare.common.chord.Node;
import it.polimi.polishare.common.chord.Operation;

import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class NodeImpl extends UnicastRemoteObject implements Node {
    private static final int R = 10;
    private static final long serialVersionUID = 1337L;
    private static final int RMI_DEFAULT_PORT = 1099;

    private String name;
    private Key key;
    private Node pred;
    private Node[] succList;

    private Node[] fingers;

    public TreeMap<Key, Object> storage;
    public TreeMap<Key, Map<Key, Object>> replicas;

    private NodeWorkers nodeWorkers;

    public NodeImpl(String name) throws RemoteException {
        this.name = name;
        this.key = new Key(name);
        this.nodeWorkers = new NodeWorkers(this);
        this.storage = new TreeMap<>();
        this.replicas = new TreeMap<>();
        startRMI();
        nodeWorkers.start();
    }

    @Override
    public Object get(Key k) throws RemoteException {
        return lookupSuccessor(k).getStored(k);
    }

    @Override
    public void repliedPut(Key k, Object v) throws RemoteException {
        lookupSuccessor(k).putStored(k, v);
    }

    @Override
    public void repliedRemove(Key k) throws RemoteException {
        lookupSuccessor(k).repliedRemove(k);
    }

    @Override
    public void exec(Key k, Operation op) throws RemoteException {
        lookupSuccessor(k).execStored(k, op);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Object> get(Predicate predicate) throws RemoteException {
        List<Object> result = storage.values().stream().filter((Predicate<? super Object>) predicate).collect(Collectors.toList());
        List<Object> tmp;
        Node limit;

        int i = 0;
        while (i < Key.m - 1) {
            if (!fingers[i].getKey().equals(fingers[i+1].getKey())){
                limit = fingers[i+1];

                tmp = fingers[i].broadcast(predicate, limit);
                result.addAll(tmp);
            }
            i++;
        }


        if(!fingers[i].getKey().equals(this.key)) { //avoid broadcast themselves
            tmp = fingers[i].broadcast(predicate, this);
            result.addAll(tmp);
        }

        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Object> broadcast(Predicate predicate, Node limit) throws RemoteException {
        List<Object> result = storage.values().stream().filter((Predicate<? super Object>) predicate).collect(Collectors.toList());
        List<Object> tmp;
        Node newLimit;


        int i = 0;
        while (i < Key.m - 1) {
            if (!fingers[i].getKey().equals(fingers[i+1].getKey())) {
                if(!this.key.equals(limit.getKey()) && fingers[i].getKey().isRingBetween(this.key, limit.getKey())) {
                    if(fingers[i+1].getKey().isRingBetween(this.key, limit.getKey()))
                        newLimit = fingers[i + 1];
                    else
                        newLimit = limit;

                    tmp = fingers[i].broadcast(predicate, newLimit);
                    result.addAll(tmp);
                } else {
                    break;
                }
            }
            i++;
        }

        if(!this.key.equals(limit.getKey()) && fingers[i].getKey().isRingBetween(this.key, limit.getKey())) {
            tmp = fingers[i].broadcast(predicate, limit);
            result.addAll(tmp);
        }

        return result;
    }

    @Override
    public Object getStored(Key k) {
        return storage.get(k);
    }

    @Override
    public void putStored(Key k, Object v) {
        storage.put(k, v);
    }

    @Override
    public void removeStored(Key k) {
        storage.remove(k);
    }

    @Override
    public void execStored(Key k, Operation op) {
        op.execute(storage.get(k));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Key getKey() {
        return key;
    }

    @Override
    public Node getPred() {
        return pred;
    }

    @Override
    public Node[] getSuccList() {
        return succList;
    }

    @Override
    public Node[] getFingers() {
        return fingers;
    }

    @Override
    public Node lookupSuccessor(Key k) throws RemoteException{
        if(k.equals(this.key)) return this;

        if(k.isRingBetween(this.key, this.getSuccList()[0].getKey()) || k.equals(succList[0].getKey()))
            return this.getSuccList()[0];

         return this.closestPreceedingNode(k).lookupSuccessor(k);
    }

    private Node closestPreceedingNode(Key k) throws RemoteException {
        for(int i = Key.m - 1; i >= 0; i--)
            if(fingers[i] != null && ping(fingers[i]) && fingers[i].getKey().isRingBetween(this.key, k))
                return fingers[i];

        return this.succList[0];
    }

    @Override
    public void create() {
        this.pred = null;

        this.succList = new Node[R];
        this.succList[0] = this;

        this.fingers = new Node[Key.m];
        Arrays.fill(this.fingers, this);
    }

    @Override
    public void join(Node known) throws RemoteException {
        Node newSucc;
        try{
            newSucc = known.lookupSuccessor(this.key);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        Node[] newSuccList;
        try{
            newSuccList = newSucc.getSuccList();
        } catch (Exception e){
            e.printStackTrace();
            return;
        }

        this.succList = new Node[R];
        this.succList[0] = newSucc;
        System.arraycopy(newSuccList, 0, this.succList, 1, R - 1);

        this.fingers = new Node[Key.m];

        this.pred = null;
    }

    @Override
    public void stabilize() {
        Node newSucc;
        Node[] sameSuccList, newSuccList;
        int i = 0;

        while(i < R) {
            try{
                newSucc = this.succList[i].getPred();
                sameSuccList = this.succList[i].getSuccList();

                this.succList[0] = this.succList[i];
                System.arraycopy(sameSuccList, 0, this.succList, 1, R - 1);

                if(newSucc != null && newSucc.getKey().isRingBetween(this.key, this.succList[0].getKey())){
                    newSuccList = newSucc.getSuccList();

                    this.succList[0] = newSucc;
                    System.arraycopy(newSuccList, 0, this.succList, 1, R - 1);
                }

                this.succList[0].rectify(this);
                break;
            } catch (Exception e){}

            i++;
        }
    }

    @Override
    public void rectify(Node newPred) {
        if(this.pred == null) {
            updatePredecessor(newPred);
        } else {
            Key predKey;
            try{
                predKey = this.pred.getKey();

                if(newPred.getKey().isRingBetween(predKey, this.getKey()))
                    updatePredecessor(newPred);
            } catch (Exception e){
                updatePredecessor(newPred);
            }

        }
    }

    private void updatePredecessor(Node newPred) {
        this.pred = newPred;
        ArrayList<Key> replicasToRemove = new ArrayList<>();
        ArrayList<Key> storageToRemove = new ArrayList<>();

        try{
            for(Key k : replicas.keySet()){
                if(k.isRingBetween(newPred.getKey(), this.key)){
                    replicasToRemove.add(k);
                    storage.putAll(replicas.get(k));
                }
            }

            for(Key k : storage.keySet()) {
                if(!(k.isRingBetween(newPred.getKey(), this.key) || k.equals(this.key))) {
                    newPred.putStored(k, storage.get(k));
                    storageToRemove.add(k);
                }
            }
        } catch (RemoteException e) { /*e.printStackTrace();*/}

        for(Key k : replicasToRemove)
            replicas.remove(k);

        for(Key k : storageToRemove)
            storage.remove(k);
    }

    @Override
    public void checkPred() {
        if(!ping(this.getPred()))
            this.pred = null;
    }

    @Override
    public void fixFingers() throws RemoteException{
        for(int i = 0; i < Key.m; i++) {
            fingers[i] = lookupSuccessor(this.key.sumPowerOf2(i));
        }
    }

    @Override
    public void replicate() throws RemoteException {
        for(Node n : succList)
            if(n != null) n.storeReply(this.key, this.storage);
    }

    @Override
    public void storeReply(Key key, Map<Key, Object> reply) {
        replicas.put(key, reply);
    }

    @Override
    public void stop(){
        try{
            UnicastRemoteObject.unexportObject(this, true);
        } catch (NoSuchObjectException e){
            e.printStackTrace();
        }
    }

    private void startRMI() throws RemoteException {
        Registry registry;
        try {
            registry = LocateRegistry.createRegistry(RMI_DEFAULT_PORT);
        } catch (Exception e) {
            registry = LocateRegistry.getRegistry(RMI_DEFAULT_PORT);
        }
        registry.rebind(name, this);
    }

    private boolean ping(Node node) {
        try{
            node.getName();
            return true;
        } catch (Exception e){
            return false;
        }
    }
}
