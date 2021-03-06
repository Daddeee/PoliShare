package it.polimi.polishare.peer.controller;

import com.jfoenix.controls.JFXListView;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.FlowHandler;
import io.datafx.controller.flow.action.ActionTrigger;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import io.datafx.controller.util.VetoException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;

import javax.annotation.PostConstruct;
import java.util.Objects;

/**
 * Manages the unauthenticated side menu's interface.
 */
@ViewController(value = "/view/UnauthenticatedSideMenu.fxml")
public class UnauthenticatedSideMenuController {

    @FXMLViewFlowContext
    private ViewFlowContext context;
    @FXML
    @ActionTrigger("login")
    private Label login;
    @FXML
    @ActionTrigger("register")
    private Label register;
    @FXML
    @ActionTrigger("search")
    private Label search;
    @FXML
    @ActionTrigger("about")
    private Label about;

    @FXML
    private JFXListView<Label> sideList;

    /**
     * Load the side menu and links each entry with the corresponding view that will be shown on click.
     */
    @PostConstruct
    public void init() {
        Objects.requireNonNull(context, "context");
        FlowHandler contentFlowHandler = (FlowHandler) context.getRegisteredObject("ContentFlowHandler");
        sideList.propagateMouseEventsToParent();
        sideList.getSelectionModel().selectedItemProperty().addListener((o, oldVal, newVal) -> {
            new Thread(()->{
                Platform.runLater(()->{
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
        bindNodeToController(login, LoginController.class, contentFlow);
        bindNodeToController(register, RegisterController.class, contentFlow);
        bindNodeToController(search, UnauthenticatedSearchController.class, contentFlow);
        bindNodeToController(about, WelcomeController.class, contentFlow);
    }

    private void bindNodeToController(Node node, Class<?> controllerClass, Flow flow) {
        flow.withGlobalLink(node.getId(), controllerClass);
    }

}

