package it.polimi.polishare.common.DHT;

import it.polimi.polishare.common.DHT.chord.Operation;
import it.polimi.polishare.server.network.DHT.DHTImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;

import java.io.Serializable;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.Assert.*;

public class DHTTestBasic implements Serializable {
    private static DHT<StringBuffer> node1;
    private static DHT<StringBuffer> node2;
    private static DHT<StringBuffer> node3;

    @BeforeAll
    public static void setUp () throws DHTException, InterruptedException {
        node1 = new DHTImpl<>("Nodo1", StringBuffer.class);
        node2 = new DHTImpl<>("Nodo2", StringBuffer.class);
        node3 = new DHTImpl<>("Nodo3", StringBuffer.class);

        node1.create();
        node2.join("localhost", "Nodo1");
        node3.join("localhost", "Nodo1");

        int s = 5;
        System.out.println("Putting test at sleep for " + s + " seconds, need this to let the network stabilize");
        Thread.sleep(s*1000);
    }

    @Test
    public void putAndGet() throws DHTException {
        String key = "toGet";
        StringBuffer value = new StringBuffer("get");

        node1.put(key, value);

        assertEquals(node1.get(key).toString(), value.toString());
        assertEquals(node2.get(key).toString(), value.toString());
        assertEquals(node3.get(key).toString(), value.toString());

        assertNull(node1.get("dummy"));
        assertNull(node2.get("dummy"));
        assertNull(node3.get("dummy"));
    }

    @Test
    public void putReplaceOldValue() throws DHTException {
        node1.put("chiave", new StringBuffer("valore"));
        assertEquals(node3.get("chiave").toString(), "valore");
        node2.put("chiave", new StringBuffer("valore2"));
        assertEquals(node3.get("chiave").toString(), "valore2");
    }

    @Test
    public void putAndRemove() throws DHTException {
        String key = "toRemove";
        StringBuffer value = new StringBuffer("remove");

        node1.put(key, value);

        node2.remove(key);
        assertNull(node3.get(key));

        node2.put(key, value);

        node1.remove(key);
        assertNull(node3.get(key));

        node1.put(key, value);

        node3.remove(key);
        assertNull(node2.get(key));
    }

    @Test
    public void query() throws DHTException {
        Predicate<StringBuffer> query = (Serializable&Predicate<StringBuffer>) stringBuffer -> {
            return stringBuffer.charAt(0) == 'a';
        };

        node1.put("query1", new StringBuffer("alleluja"));
        node3.put("query2", new StringBuffer("bella"));

        List<StringBuffer> queryResult = node2.query(query);

        assert (queryResult.size() == 1 && queryResult.get(0).toString().equals("alleluja"));
    }

    @Test
    public void exec() throws DHTException {
        Operation toUpper = new Operation() {
            @Override
            public void execute(Object o) {
                ((StringBuffer) o).replace(0, ((StringBuffer) o).length(), o.toString().toUpperCase());
            }
        };

        String key = "exec";
        StringBuffer value = new StringBuffer("min");
        node1.put(key, value);

        node2.exec(key, toUpper);

        assertEquals(value.toString().toUpperCase(), node3.get(key).toString());
    }

    @AfterAll
    public static void tearDown() throws DHTException {
        node3.leave();
        node2.leave();
        node1.leave();
    }
}