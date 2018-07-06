package it.polimi.polishare.peer.utils;

import java.util.*;

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

    public List<T> getAll() {
        return new ArrayList<>(queue.values());
    }
}
