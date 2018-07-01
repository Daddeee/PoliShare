package it.polimi.polishare.peer;

import com.jfoenix.controls.JFXDecorator;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.container.DefaultFlowContainer;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import it.polimi.polishare.common.DHT.DHT;
import it.polimi.polishare.common.DHT.DHTException;
import it.polimi.polishare.common.NoteMetaData;
import it.polimi.polishare.peer.controller.MainController;
import it.polimi.polishare.peer.network.DHT.DHTImpl;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class App  extends Application {
    public static DHT<NoteMetaData> dht;
    public static final String USERNAME = "User1";
    private static final String MY_IP = "localhost";
    private static final String SERVER_IP = "localhost";
    private static final String SERVER_NAME = "_SERVER_";

    @FXMLViewFlowContext
    private ViewFlowContext flowContext;

    public static void main( String[] args ) {
        System.setProperty("java.rmi.server.hostname", MY_IP);

        try {
            dht = new DHTImpl<>(USERNAME, NoteMetaData.class);
            dht.join(SERVER_IP, SERVER_NAME);
        } catch (DHTException e) {
            e.printStackTrace();
            System.exit(1);
        }

        launch(args);
    }

    @Override
    public void start(Stage stage) throws FlowException {
        Flow flow = new Flow(MainController.class);

        DefaultFlowContainer container = new DefaultFlowContainer();

        flowContext = new ViewFlowContext();
        flowContext.register("Stage", stage);

        flow.createHandler(flowContext).start(container);

        Scene scene = setUpFirstScene(stage, container);
        setUpFileSheets(scene);

        stage.setScene(scene);
        stage.show();
    }

    private Scene setUpFirstScene(Stage stage, DefaultFlowContainer container) {
        JFXDecorator decorator = new JFXDecorator(stage, container.getView());
        decorator.setCustomMaximize(true);

        double width = 800;
        double height = 600;
        try {
            Rectangle2D bounds = Screen.getScreens().get(0).getBounds();
            width = bounds.getWidth() / 2.5;
            height = bounds.getHeight() / 1.35;
        }catch (Exception e){ }
        return new Scene(decorator, width, height);
    }

    private void setUpFileSheets(Scene scene) {
        final ObservableList<String> stylesheets = scene.getStylesheets();
        stylesheets.addAll(App.class.getResource("/css/jfoenix-fonts.css").toExternalForm(),
                App.class.getResource("/css/jfoenix-design.css").toExternalForm(),
                App.class.getResource("/css/main.css").toExternalForm());
    }
}
