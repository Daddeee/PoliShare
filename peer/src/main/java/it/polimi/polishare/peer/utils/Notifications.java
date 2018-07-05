package it.polimi.polishare.peer.utils;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class Notifications {
    private static ViewFlowContext context;

    public static void setContext(ViewFlowContext context) {
        Notifications.context = context;
    }

    public static void exception(Exception e) {
        JFXDialog dialog = new JFXDialog();

        JFXButton closeButton = new JFXButton("Chiudi");
        closeButton.setOnAction(action -> dialog.close());
        closeButton.setStyle("-fx-text-fill: rgba(255.0, 255.0, 255.0, 1.0)");

        JFXDialogLayout layout = new JFXDialogLayout();
        Label headLabel = new Label("Errore");
        Label bodyLabel = new Label(e.getMessage());
        headLabel.setStyle("-fx-text-fill: rgba(255.0, 255.0, 255.0, 1.0)");
        bodyLabel.setStyle("-fx-text-fill: rgba(255.0, 255.0, 255.0, 1.0)");
        layout.setHeading(headLabel);
        layout.setBody(bodyLabel);
        layout.setActions(closeButton);

        dialog.setContent(layout);
        dialog.setOverlayClose(true);
        dialog.setDialogContainer((StackPane) context.getRegisteredObject("Root"));
        dialog.setTransitionType(JFXDialog.DialogTransition.RIGHT);

        layout.getStyleClass().add("error-dialog");

        dialog.show();
    }
}
