package it.polimi.polishare.common.DHT;

import it.polimi.polishare.common.DHT.chord.Node;
import it.polimi.polishare.server.network.DHT.DHTImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class DHTTestFaultTolerance implements Serializable {
    private static DHT<Integer> node1;
    private static DHT<Integer> node2;
    private static DHT<Integer> node3;
    private static List<Integer> values;

    @BeforeAll
    public static void setUp () throws DHTException, InterruptedException {
        node1 = new DHTImpl<>("Nodo1", Integer.class);
        node2 = new DHTImpl<>("Nodo2", Integer.class);
        node3 = new DHTImpl<>("Nodo3", Integer.class);

        node1.create();
        node2.join("localhost", "Nodo1");
        node3.join("localhost", "Nodo1");

        int s = 5;
        System.out.println("Putting test at sleep for " + s + " seconds, need this to let the network stabilize");
        Thread.sleep(s*1000);

        values = new ArrayList<>();
        for(int i = 0; i < 50; i++) {
            node1.put(Integer.toString(i), i);
            values.add(i);
        }
    }

    @Test
    public void testFaultTolerance() throws InterruptedException, DHTException, RemoteException, NoSuchFieldException, IllegalAccessException, NotBoundException {
        Predicate<Integer> getAll = (Serializable&Predicate<Integer>) integer -> true;

        List<Integer> result = node2.query(getAll);
        assert (result.size() == values.size() && result.containsAll(values));

        System.out.println("Killing Node1");
        killDHTNode(node1);
        int s = 5;
        System.out.println("Putting test at sleep for " + s + " seconds, need this to let the network stabilize");
        Thread.sleep(s*1000);

        result = node2.query(getAll);
        assert (result.size() == values.size() && result.containsAll(values));

        System.out.println("Killing Node3");
        killDHTNode(node3);
        s = 5;
        System.out.println("Putting test at sleep for " + s + " seconds, need this to let the network stabilize");
        Thread.sleep(s*1000);

        result = node2.query(getAll);
        assert (result.size() == values.size() && result.containsAll(values));

        node1 = new DHTImpl<>("Nodo1", Integer.class);
        node3 = new DHTImpl<>("Nodo3", Integer.class);

        System.out.println("Rejoining Node3");
        node3.join("localhost", "Nodo2");
        s = 5;
        System.out.println("Putting test at sleep for " + s + " seconds, need this to let the network stabilize");
        Thread.sleep(s*1000);

        result = node3.query(getAll);
        assert (result.size() == values.size() && result.containsAll(values));

        System.out.println("Rejoining Node1");
        node1.join("localhost", "Nodo2");
        s = 5;
        System.out.println("Putting test at sleep for " + s + " seconds, need this to let the network stabilize");
        Thread.sleep(s*1000);

        result = node1.query(getAll);
        assert (result.size() == values.size() && result.containsAll(values));
    }

    private void killDHTNode(DHT node1) throws NoSuchFieldException, IllegalAccessException, RemoteException, NotBoundException {
        Field f = node1.getClass().getDeclaredField("node");
        f.setAccessible(true);
        UnicastRemoteObject.unexportObject((Node)f.get(node1), true);
        LocateRegistry.getRegistry().unbind(((Node) f.get(node1)).getName());
        f.setAccessible(false);
    }

    @AfterAll
    public static void tearDown() throws DHTException {
        node1.leave();
        node2.leave();
        node3.leave();
    }
}
