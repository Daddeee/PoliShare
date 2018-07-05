package it.polimi.polishare.peer.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXListView;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.FlowHandler;
import io.datafx.controller.flow.action.ActionTrigger;
import io.datafx.controller.flow.container.AnimatedFlowContainer;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import io.datafx.controller.util.VetoException;
import it.polimi.polishare.common.DHT.DHTException;
import it.polimi.polishare.common.Downloader;
import it.polimi.polishare.peer.utils.CurrentSession;
import it.polimi.polishare.peer.utils.Notifications;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import javax.annotation.PostConstruct;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Objects;

@ViewController(value = "/view/SideMenu.fxml")
public class SideMenuController {

    @FXMLViewFlowContext
    private ViewFlowContext context;
    @FXML
    @ActionTrigger("catalog")
    private Label catalog;
    @FXML
    @ActionTrigger("publish")
    private Label publish;
    @FXML
    @ActionTrigger("search")
    private Label search;
    @FXML
    @ActionTrigger("download")
    private Label download;

    @FXML
    private JFXListView<Label> sideList;

    @PostConstruct
    public void init() {
        Objects.requireNonNull(context, "context");
        context.register("SideMenuController", this);
        FlowHandler contentFlowHandler = (FlowHandler) context.getRegisteredObject("ContentFlowHandler");
        sideList.propagateMouseEventsToParent();
        sideList.getSelectionModel().selectedItemProperty().addListener((o, oldVal, newVal) -> {
            new Thread(() -> {
                Platform.runLater(() -> {
                    if (newVal != null) {
                        try {
                            contentFlowHandler.handle(newVal.getId());
                        } catch (VetoException | FlowException exc) {
                            exc.printStackTrace();
                        }
                    }
                });
            }).start();
        });
        Flow contentFlow = (Flow) context.getRegisteredObject("ContentFlow");
        bindNodeToController(catalog, CatalogController.class, contentFlow);
        bindNodeToController(publish, PublishController.class, contentFlow);
        bindNodeToController(search, SearchController.class, contentFlow);
        bindNodeToController(download, DownloadController.class, contentFlow);
    }

    private void bindNodeToController(Node node, Class<?> controllerClass, Flow flow) {
        flow.withGlobalLink(node.getId(), controllerClass);
    }

    @FXML
    public void logout() {
        try {
            switchToUnauthenticatedUI();

            CurrentSession.shutDown();
        } catch (FlowException | RemoteException | DHTException e) {
            Notifications.exception((StackPane) context.getRegisteredObject("Root"), e);
        }
    }

    private void switchToUnauthenticatedUI() throws FlowException {
        JFXDrawer drawer = (JFXDrawer) context.getRegisteredObject("Drawer");
        Flow innerFlow = new Flow(UnauthenticatedSearchController.class);
        final FlowHandler flowHandler = innerFlow.createHandler(context);
        drawer.setContent(flowHandler.start(new AnimatedFlowContainer(Duration.millis(320))));

        context.register("ContentFlowHandler", flowHandler);
        context.register("ContentFlow", innerFlow);

        Flow sideMenuFlow = new Flow(UnauthenticatedSideMenuController.class);
        final FlowHandler sideMenuFlowHandler = sideMenuFlow.createHandler(context);
        drawer.setSidePane(sideMenuFlowHandler.start(new AnimatedFlowContainer(Duration.millis(320))));

        JFXButton showChatButton = (JFXButton) context.getRegisteredObject("ShowChatButton");
        showChatButton.setDisable(true);
    }
}
