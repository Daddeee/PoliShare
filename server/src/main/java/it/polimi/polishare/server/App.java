package it.polimi.polishare.server;

import it.polimi.polishare.common.DHT.DHT;
import it.polimi.polishare.common.DHT.DHTException;
import it.polimi.polishare.common.NoteMetaData;
import it.polimi.polishare.common.unauthenticated.UnauthenticatedSessionFactory;
import it.polimi.polishare.server.Utils.DB;
import it.polimi.polishare.server.network.DHT.DHTImpl;
import it.polimi.polishare.server.network.unauthenticated.UnauthenticatedSessionFactoryImpl;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;

public class App {
    public static DHT<NoteMetaData> dht;
    public static String DHT_NAME = "_SERVER_";
    private static UnauthenticatedSessionFactory usf;

    public static void main( String[] args ) {
        try {
            DB.setUp();

            usf = new UnauthenticatedSessionFactoryImpl();
            dht = new DHTImpl<>(DHT_NAME, NoteMetaData.class);

            Registry registry = LocateRegistry.getRegistry();
            registry.rebind("session_factory", usf);
            dht.create();
        } catch (DHTException | RemoteException | SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
