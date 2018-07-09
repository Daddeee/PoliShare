package it.polimi.polishare.peer.controller;

import com.jfoenix.controls.*;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.FlowHandler;
import io.datafx.controller.flow.container.AnimatedFlowContainer;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import it.polimi.polishare.peer.utils.Notifications;
import javafx.animation.Transition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * Manages the main unauthenticated view.
 */
@ViewController(value = "/view/Main.fxml", title = "Polishare")
public class MainController {
    @FXMLViewFlowContext
    private ViewFlowContext context;

    @FXML
    private StackPane root;
    @FXML
    private StackPane titleBurgerContainer;
    @FXML
    private JFXHamburger titleBurger;
    @FXML
    private JFXDrawer drawer;
    @FXML
    private JFXButton showChatButton;

    private JFXPopup chatPopup;


    /**
     * Initialize the menus and the context.
     * @throws FlowException
     */
    @PostConstruct
    public void init() throws FlowException {
        drawer.setOnDrawerOpening(e -> {
            final Transition animation = titleBurger.getAnimation();
            animation.setRate(1);
            animation.play();
        });
        drawer.setOnDrawerClosing(e -> {
            final Transition animation = titleBurger.getAnimation();
            animation.setRate(-1);
            animation.play();
        });
        titleBurgerContainer.setOnMouseClicked(e -> {
            if (drawer.isClosed() || drawer.isClosing()) {
                drawer.open();
            } else {
                drawer.close();
            }
        });

        context = new ViewFlowContext();
        Flow innerFlow = new Flow(WelcomeController.class);
        final FlowHandler flowHandler = innerFlow.createHandler(context);
        drawer.setContent(flowHandler.start(new AnimatedFlowContainer(Duration.millis(320))));

        context.register("ContentFlowHandler", flowHandler);
        context.register("ContentFlow", innerFlow);
        context.register("ContentPane", drawer.getContent().get(0));
        context.register("Root", root);
        context.register("Drawer", drawer);
        context.register("ShowChatButton", showChatButton);

        Notifications.setContext(context);

        Flow sideMenuFlow = new Flow(UnauthenticatedSideMenuController.class);
        final FlowHandler sideMenuFlowHandler = sideMenuFlow.createHandler(context);
        drawer.setSidePane(sideMenuFlowHandler.start(new AnimatedFlowContainer(Duration.millis(320))));
    }

    /**
     * Display the chat's popup.
     */
    @FXML
    public void showChat() {
        double popupHeight = drawer.getHeight()*4/5;
        double popupWidth = drawer.getWidth()*4/5;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/popup/Chat.fxml"));
            chatPopup = new JFXPopup(loader.load());

            ((ChatController) loader.getController()).initData(popupHeight, popupWidth);
        } catch (IOException ioExc) {
            ioExc.printStackTrace();
        }
        Bounds rootBounds = drawer.getLayoutBounds();

        chatPopup.show(drawer, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT,
                (rootBounds.getWidth() - popupWidth) / 2,
                (rootBounds.getHeight() - popupHeight) / 2);
    }
}
