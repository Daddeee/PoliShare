package it.polimi.polishare.common.DHT;

import java.util.List;

public interface DHT<T> {
    void join(String ip, String nodeName) throws DHTException;

    void create() throws DHTException;

    void leave() throws DHTException;

    T get(String key) throws DHTException;

    void put(String key, T value) throws DHTException;

    void remove(String key) throws DHTException;

    List<T> getAll() throws DHTException;
}
