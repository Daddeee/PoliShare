package it.polimi.polishare.peer;

import com.jfoenix.controls.JFXDecorator;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.container.DefaultFlowContainer;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import it.polimi.polishare.common.DHT.DHT;
import it.polimi.polishare.common.Downloader;
import it.polimi.polishare.common.NoteMetaData;
import it.polimi.polishare.common.server.Message;
import it.polimi.polishare.common.server.SessionFactory;
import it.polimi.polishare.peer.controller.MainController;
import it.polimi.polishare.peer.network.DownloaderImpl;
import it.polimi.polishare.peer.utils.CurrentSession;
import it.polimi.polishare.peer.utils.DB;
import it.polimi.polishare.peer.utils.Notifications;
import it.polimi.polishare.peer.utils.Settings;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;

public class App  extends Application {
    public static SessionFactory sf;
    public static Downloader dw;

    @FXMLViewFlowContext
    private ViewFlowContext flowContext;

    public static void main( String[] args ) {
        System.setProperty("java.rmi.server.hostname", Settings.getProperty("my_ip"));

        try {
            DB.setUp();
            dw = new DownloaderImpl();

            Registry registry = LocateRegistry.getRegistry(Settings.getProperty("server_ip"));
            sf = (SessionFactory) registry.lookup("session_factory");
        } catch (SQLException | RemoteException | NotBoundException e) {
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

    @Override
    public void stop() throws Exception {
        super.stop();
        CurrentSession.shutDown();
        UnicastRemoteObject.unexportObject(dw, true);
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
