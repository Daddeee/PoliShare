package it.polimi.polishare.peer.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.FlowHandler;
import io.datafx.controller.flow.container.AnimatedFlowContainer;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import it.polimi.polishare.common.DHT.DHTException;
import it.polimi.polishare.common.DHT.model.NoteMetaData;
import it.polimi.polishare.common.server.exceptions.LoginFailedException;
import it.polimi.polishare.common.server.Session;
import it.polimi.polishare.peer.App;
import it.polimi.polishare.peer.network.DHT.DHTImpl;
import it.polimi.polishare.peer.network.download.DownloaderImpl;
import it.polimi.polishare.peer.network.server.ReverseSessionImpl;
import it.polimi.polishare.peer.CurrentSession;
import it.polimi.polishare.peer.utils.Notifications;
import it.polimi.polishare.peer.utils.Settings;
import it.polimi.polishare.peer.utils.ThreadPool;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.util.Duration;

import javax.annotation.PostConstruct;
import java.rmi.RemoteException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Manages the login interface.
 */
@ViewController(value = "/view/Login.fxml", title = "Polishare")
public class LoginController {
    @FXMLViewFlowContext
    private ViewFlowContext context;

    @FXML
    private JFXTextField username;
    @FXML
    private JFXPasswordField password;

    /**
     * Loads the JFXTextFields with the given validators.
     */
    @PostConstruct
    public void init() {
        username.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                username.validate();
            }
        });

        password.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                password.validate();
            }
        });
    }

    /**
     * Try to login the user on the server with the provided credentials. If the login is succesfull, the UI is switched
     * to the authenticated one, otherwise an error message is shown.
     */
    @FXML
    private void login() {
        if(!(username.validate() && password.validate()))
            return;

        ThreadPool.getInstance().execute(() -> {
            try {
                Session session = App.sf.login(username.getText(), password.getText());

                CurrentSession.setSession(session);
                CurrentSession.setUsername(username.getText());
                CurrentSession.setReverseSession(new ReverseSessionImpl());
                CurrentSession.setDHT(new DHTImpl<>(username.getText(), NoteMetaData.class));
                CurrentSession.getDHT().join(Settings.getProperty("server_ip"), CurrentSession.getSession().getServerDHTName());
                CurrentSession.getSession().setReverseSession(CurrentSession.getReverseSession());

                App.dw = new DownloaderImpl(username.getText());

                Platform.runLater(() -> {
                    try {
                        switchToAuthenticatedUI();
                    } catch (FlowException e) {
                        e.printStackTrace();
                    }
                });
            } catch (LoginFailedException e) {
                Platform.runLater(() -> Notifications.exception(e));
            } catch (RemoteException | DHTException e) {
                e.printStackTrace();
            }
        });
    }

    private void switchToAuthenticatedUI() throws FlowException {
        JFXDrawer drawer = (JFXDrawer) context.getRegisteredObject("Drawer");
        Flow innerFlow = new Flow(CatalogController.class);
        final FlowHandler flowHandler = innerFlow.createHandler(context);
        drawer.setContent(flowHandler.start(new AnimatedFlowContainer(Duration.millis(320))));

        context.register("ContentFlowHandler", flowHandler);
        context.register("ContentFlow", innerFlow);

        Flow sideMenuFlow = new Flow(SideMenuController.class);
        final FlowHandler sideMenuFlowHandler = sideMenuFlow.createHandler(context);
        drawer.setSidePane(sideMenuFlowHandler.start(new AnimatedFlowContainer(Duration.millis(320))));

        JFXButton showChatButton = (JFXButton) context.getRegisteredObject("ShowChatButton");
        showChatButton.setDisable(false);
    }
}
