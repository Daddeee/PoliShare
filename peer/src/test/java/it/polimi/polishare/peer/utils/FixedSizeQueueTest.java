package it.polimi.polishare.peer.utils;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;


public class FixedSizeQueueTest {
    @Test
    public void testBasicQueue() {
        List<Integer> addedValues = new ArrayList<>();
        FixedSizeQueue<Integer> queue = new FixedSizeQueue<>(20);

        for(int i = 0; i < 20; i++) {
            addedValues.add(i);
            queue.add(i);
        }

        assert (addedValues.size() == queue.getAll().size());
        for(int i = 0; i < addedValues.size(); i++) {
            assert (addedValues.get(i).equals(queue.getAll().get(i)));
        }
    }

    @Test
    public void testExceedingSize() {
        List<Integer> addedValues = new ArrayList<>();
        FixedSizeQueue<Integer> queue = new FixedSizeQueue<>(10);

        for(int i = 0; i < 20; i++) {
            addedValues.add(i);
            queue.add(i);
        }

        assert (queue.getAll().size() == 10);
        for(int i = 0; i < queue.getAll().size(); i++) {
            assert (addedValues.get(i + 10).equals(queue.getAll().get(i)));
        }
    }
}