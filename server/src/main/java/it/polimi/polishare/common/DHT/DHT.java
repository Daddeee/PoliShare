package it.polimi.polishare.common.DHT;

import it.polimi.polishare.common.chord.Operation;

import java.util.List;
import java.util.function.Predicate;

public interface DHT<T> {
    void join(String ip, String nodeName) throws DHTException;

    void create() throws DHTException;

    void leave() throws DHTException;

    T get(String key) throws DHTException;

    void put(String key, T value) throws DHTException;

    void remove(String key) throws DHTException;

    List<T> query(Predicate<T> predicate) throws DHTException;

    void exec(String key, Operation op) throws DHTException;
}
