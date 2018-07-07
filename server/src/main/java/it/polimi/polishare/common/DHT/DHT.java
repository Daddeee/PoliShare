package it.polimi.polishare.common.DHT;

import it.polimi.polishare.common.DHT.chord.Operation;

import java.util.List;
import java.util.function.Predicate;

/**
 * Represents the interface used to interact with the distributed hash table (DHT).
 * @param <T> the type of the objects managed by the table.
 */
public interface DHT<T> {
    /**
     * Create a new DHT.
     *
     * @throws DHTException
     */
    void create() throws DHTException;

    /**
     * Join a DHT from the given entry point.
     *
     * @param ip the IP of the entry point.
     * @param nodeName the entry point's name.
     * @throws DHTException
     */
    void join(String ip, String nodeName) throws DHTException;

    /**
     * Leaves the current DHT.
     *
     * @throws DHTException
     */
    void leave() throws DHTException;

    /**
     * Returns the value to which the specified key is mapped, or null if this DHT contains no mapping for the key.
     *
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or null if this DHT contains no mapping for the key.
     * @throws DHTException
     */
    T get(String key) throws DHTException;

    /**
     * Associates the specified value with the specified key in this DHT (optional operation). If the DHT previously
     * contained a mapping for the key, the old value is replaced by the specified value.
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @throws DHTException
     */
    void put(String key, T value) throws DHTException;

    /**
     * Removes the mapping for a key from this DHT if it is present.
     *
     * @param key key whose mapping is to be removed
     * @throws DHTException
     */
    void remove(String key) throws DHTException;

    /**
     * Returns a List of element from the DHT matching the given predicate.
     *
     * @param predicate the predicate that the return values must match.
     * @return a List of element from the DHT matching the given predicate.
     * @throws DHTException
     */
    List<T> query(Predicate<T> predicate) throws DHTException;

    /**
     * Executes the given {@link it.polimi.polishare.common.DHT.chord.Operation Operation} on the element associated
     * with the specified key in the DHT. If no association is found, the value null is passed to the
     * Operation.
     *
     * @param key key associated to the value on which executes the operation.
     * @param op the given operation.
     * @throws DHTException
     */
    void exec(String key, Operation op) throws DHTException;
}
