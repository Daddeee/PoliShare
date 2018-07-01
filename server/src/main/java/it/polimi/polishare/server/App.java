package it.polimi.polishare.server;

import it.polimi.polishare.common.DHT.DHT;
import it.polimi.polishare.common.DHT.DHTException;
import it.polimi.polishare.common.NoteMetaData;
import it.polimi.polishare.server.network.DHT.DHTImpl;

public class App {
    private static DHT<NoteMetaData> dht;

    public static void main( String[] args ) {
        try {
            dht = new DHTImpl<>("_SERVER_", NoteMetaData.class);
            dht.create();
        } catch (DHTException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
