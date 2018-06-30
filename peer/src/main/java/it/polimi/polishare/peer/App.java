package it.polimi.polishare.peer;

import it.polimi.polishare.common.DHT.DHT;
import it.polimi.polishare.common.DHT.DHTException;
import it.polimi.polishare.peer.network.DHT.DHTImpl;
import javafx.application.Application;
import javafx.stage.Stage;

public class App  extends Application {
    private static DHT<String> dht;
    private static final String USERNAME = "User1";
    private static final String MY_IP = "localhost";
    private static final String SERVER_IP = "localhost";
    private static final String SERVER_NAME = "_SERVER_";

    public static void main( String[] args ) {
        System.setProperty("java.rmi.server.hostname", MY_IP);

        try {
            dht = new DHTImpl<>(USERNAME);
            dht.join(SERVER_IP, SERVER_NAME);
        } catch (DHTException e) {
            e.printStackTrace();
            System.exit(1);
        }

        launch(args);
    }

    @Override
    public void start(Stage stage) {
    }
}
