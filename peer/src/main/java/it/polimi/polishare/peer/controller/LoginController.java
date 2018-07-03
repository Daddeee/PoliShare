package it.polimi.polishare.peer.controller;

import com.jfoenix.controls.JFXDrawer;
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
import it.polimi.polishare.common.unauthenticated.LoginFailedException;
import it.polimi.polishare.peer.App;
import it.polimi.polishare.peer.model.User;
import it.polimi.polishare.peer.network.DHT.DHTImpl;
import it.polimi.polishare.peer.utils.Notifications;
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
    private JFXTextField password;

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

        String serverName;
        try {
            serverName = App.us.login(username.getText(), password.getText());

            User.getInstance().setUsername(username.getText());

            App.dht = new DHTImpl<>(username.getText(), NoteMetaData.class);
            App.dht.join(App.SERVER_IP, serverName);

            switchToAuthenticatedUI();
        } catch (LoginFailedException e) {
            StackPane root = (StackPane) context.getRegisteredObject("Root");
            Notifications.exception(root, e);
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
    }
}
