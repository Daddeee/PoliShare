package it.polimi.polishare.server;

import it.polimi.polishare.common.DHT.DHT;
import it.polimi.polishare.common.DHT.DHTException;
import it.polimi.polishare.common.DHT.model.NoteMetaData;
import it.polimi.polishare.common.server.SessionFactory;
import it.polimi.polishare.server.utils.DB;
import it.polimi.polishare.server.utils.Settings;
import it.polimi.polishare.server.network.DHT.DHTImpl;
import it.polimi.polishare.server.network.server.SessionFactoryImpl;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;

public class App {
    public static DHT<NoteMetaData> dht;
    public static String DHT_NAME = "_SERVER_";
    private static SessionFactory sf;

    public static void main( String[] args ) {
        System.setProperty("java.rmi.server.hostname", Settings.getProperty("my_ip"));

        try {
            DB.setUp();

            sf = new SessionFactoryImpl();
            dht = new DHTImpl<>(DHT_NAME, NoteMetaData.class);

            Registry registry = LocateRegistry.getRegistry();
            registry.rebind("session_factory", sf);
            dht.create();
        } catch (DHTException | RemoteException | SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
