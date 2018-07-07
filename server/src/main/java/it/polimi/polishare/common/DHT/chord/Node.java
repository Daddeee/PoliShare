package it.polimi.polishare.common.DHT.chord;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Remote interface representing a chord's node on the network.
 */
public interface Node extends Remote {
    /**
     * @return the Node's name.
     * @throws RemoteException
     */
    String getName() throws RemoteException;

    /**
     * @return the Node's key.
     * @throws RemoteException
     */
    Key getKey() throws RemoteException;

    /**
     * @return the Node's predecessor on the ring.
     * @throws RemoteException
     */
    Node getPred() throws RemoteException;

    /**
     * @return a list of the Node's successors on the ring.
     * @throws RemoteException
     */
    Node[] getSuccList() throws RemoteException;

    /**
     * Query the ring asking for the successor of the given key. A node x is the successor of this key if:
     * <ul>
     *     <li>k.isRingBetween(x.getPred().getKey(), x.getKey())</li>
     * </ul>
     *
     * @param k the provided Key.
     * @return the successor of the given key.
     * @throws RemoteException
     */
    Node lookupSuccessor(Key k) throws RemoteException;

    /**
     * Creates a new chord ring containing only this node.
     * @throws RemoteException
     */
    void create() throws RemoteException;

    /**
     * Entra nell'anello a cui appartiene il nodo passato come parametro.
     *
     * @param known
     * @throws RemoteException
     */
    void join(Node known) throws RemoteException;

    /**
     * Check to see if the next node has a new precedent whose key is bigger than that of this node. In this case it
     * updates the list of successors. Finally it informs the next node of its existence calling {@link #rectify(Node)}.
     *
     * @throws RemoteException
     */
    void stabilize() throws RemoteException;

    /**
     * Check if the candidate is a better predecessor than the current one. If so, update its predecessor.
     *
     * @param newPred the new predecessor candidate.
     * @throws RemoteException
     */
    void rectify(Node newPred) throws RemoteException;

    /**
     * Check if the current predecessor is still alive.
     * @throws RemoteException
     */
    void checkPred() throws RemoteException;

    /**
     * Recalculates the node fingers.
     *
     * @throws RemoteException
     */
    void fixFingers() throws RemoteException;

    /**
     * Replicates the storage of this node on all the nodes of the successor's list. Replication is used to achieve fault
     * tolerance.
     *
     * @throws RemoteException
     */
    void replicate() throws RemoteException;

    /**
     * Kill this node.
     *
     * @throws RemoteException
     */
    void stop() throws RemoteException;

    /**
     * Store on this node the storage of another node.
     * @param key the node to which the storage originally belongs
     * @param reply the actual replicated storage
     * @throws RemoteException
     */
    void storeReply(Key key, Map<Key, Object> reply) throws RemoteException;

    /**
     * Returns the value to which the specified key is mapped on this node's storage, or null if this map contains no mapping for the key.
     * @param k the key whose associated value is to be returned
     * @return Returns the value to which the specified key is mapped on this node's storage, or null if this map contains no mapping for the key.
     * @throws RemoteException
     */
    Object getStored(Key k) throws RemoteException;

    /**
     * Associates the specified value with the specified key in this node's storage. If the map previously contained a
     * mapping for the key, the old value is replaced by the specified value.
     * @param k key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @throws RemoteException
     */
    void putStored(Key k, Object value) throws RemoteException;

    /**
     * Removes the mapping for a key from this node's storage if it is present.
     * @param k key whose mapping is to be removed from the map.
     * @throws RemoteException
     */
    void removeStored(Key k) throws RemoteException;

    /**
     * Executes the given {@link it.polimi.polishare.common.DHT.chord.Operation Operation} on the element associated
     * with the specified key in this node's storage. If no association is found, the value null is passed to the
     * Operation.
     *
     * @param k key associated to the value on which executes the operation.
     * @param op the given operation.
     * @throws RemoteException
     */
    void execStored(Key k, Operation op) throws RemoteException;

    /**
     * Returns the value to which the specified key is mapped on the ring's distributed storage, or null if the storage
     * contains no mapping for the key.
     *
     * @param k the key whose associated value is to be returned
     * @return Returns the mapped value, or null if the distributed storage contains no mapping for the key.
     * @throws RemoteException
     */
    Object get(Key k) throws RemoteException;

    /**
     * Broadcast the given query on the ring. Then it collects the results and returns them.
     *
     * @param predicate the query's predicate
     * @return the results of the query.
     * @throws RemoteException
     */
    List<Object> get(Predicate predicate) throws RemoteException;

    /**
     * Broadcast the given query on the ring whitout exceeding the limit passed as a parameter. It collects the
     * results of the query and returns them.
     * @param predicate the query's predicate
     * @param limit the limit for this broadcast
     * @return the results of the query collected from all the nodes to which the query was sent.
     * @throws RemoteException
     */
    List<Object> broadcast(Predicate predicate, Node limit) throws RemoteException;

    /**
     * Associates the specified value with the specified key in the ring distributed storage. If the ring previously contained a
     * mapping for the key, the old value is replaced by the specified value.
     *
     * @param k key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @throws RemoteException
     */
    void put(Key k, Object value) throws RemoteException;

    /**
     * Removes the mapping for a key from the ring's distributed storage if it is present.
     * @param k key whose mapping is to be removed from the storage.
     * @throws RemoteException
     */
    void remove(Key k) throws RemoteException;

    /**
     * Executes the given {@link it.polimi.polishare.common.DHT.chord.Operation Operation} on the element associated
     * with the specified key in the ring's distributed storage. If no association is found, the value null is passed to the
     * Operation.
     *
     * @param k key associated to the value on which executes the operation.
     * @param op the given operation.
     * @throws RemoteException
     */
    void exec(Key k, Operation op) throws RemoteException;
}
