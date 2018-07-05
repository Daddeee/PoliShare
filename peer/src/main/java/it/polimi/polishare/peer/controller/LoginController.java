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
import it.polimi.polishare.common.NoteMetaData;
import it.polimi.polishare.common.server.LoginFailedException;
import it.polimi.polishare.common.server.Session;
import it.polimi.polishare.peer.App;
import it.polimi.polishare.peer.network.DHT.DHTImpl;
import it.polimi.polishare.peer.network.server.ReverseSessionImpl;
import it.polimi.polishare.peer.utils.CurrentSession;
import it.polimi.polishare.peer.utils.Notifications;
import it.polimi.polishare.peer.utils.Settings;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import javax.annotation.PostConstruct;
import java.rmi.RemoteException;

@ViewController(value = "/view/Login.fxml", title = "Polishare")
public class LoginController {
    @FXMLViewFlowContext
    private ViewFlowContext context;

    @FXML
    private JFXTextField username;
    @FXML
    private JFXPasswordField password;

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

    @FXML
    private void login() {
        if(!(username.validate() && password.validate()))
            return;

        try {
            Session session = App.sf.login(username.getText(), password.getText());

            CurrentSession.setSession(session);
            CurrentSession.setUsername(username.getText());
            CurrentSession.setReverseSession(new ReverseSessionImpl());
            CurrentSession.setDHT(new DHTImpl<>(username.getText(), NoteMetaData.class));
            CurrentSession.getDHT().join(Settings.getProperty("server_ip"), CurrentSession.getSession().getServerDHTName());
            CurrentSession.getSession().setReverseSession(CurrentSession.getReverseSession());

            switchToAuthenticatedUI();
        } catch (LoginFailedException e) {
            Notifications.exception(e);
        } catch (RemoteException | DHTException | FlowException e) {
            e.printStackTrace();
        }
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
