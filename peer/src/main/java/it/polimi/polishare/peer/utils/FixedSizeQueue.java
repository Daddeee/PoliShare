package it.polimi.polishare.peer.utils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class FixedSizeQueue<T extends Object> {
    private LinkedHashMap<Integer, T> queue;
    private int size;

    public FixedSizeQueue(int size) {
        this.size = size;
        this.queue = new LinkedHashMap<Integer, T>() {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Integer, T> eldest) {
                return this.size() > size;
            }
        };
    }

    public void add(T e) {
        queue.put(e.hashCode(), e);
    }

    public Collection<T> getAll() {
        return queue.values();
    }
}
