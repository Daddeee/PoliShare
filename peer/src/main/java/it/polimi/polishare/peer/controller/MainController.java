package it.polimi.polishare.peer.controller;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXPopup;
import com.jfoenix.controls.JFXRippler;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.FlowHandler;
import io.datafx.controller.flow.container.AnimatedFlowContainer;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.animation.Transition;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import javax.annotation.PostConstruct;

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
    private StackPane optionsBurger;
    @FXML
    private JFXRippler optionsRippler;

    private JFXPopup toolbarPopup;

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
        Flow innerFlow = new Flow(SearchController.class);
        final FlowHandler flowHandler = innerFlow.createHandler(context);
        context.register("ContentFlowHandler", flowHandler);
        context.register("ContentFlow", innerFlow);
        drawer.setContent(flowHandler.start(new AnimatedFlowContainer(Duration.millis(320))));
        context.register("ContentPane", drawer.getContent().get(0));
        context.register("Root", root);

        Flow sideMenuFlow = new Flow(SideMenuController.class);
        final FlowHandler sideMenuFlowHandler = sideMenuFlow.createHandler(context);
        drawer.setSidePane(sideMenuFlowHandler.start(new AnimatedFlowContainer(Duration.millis(320))));
    }
}
