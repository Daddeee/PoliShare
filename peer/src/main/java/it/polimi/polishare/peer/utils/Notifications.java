package it.polimi.polishare.peer.utils;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class Notifications {
    public static void exception(StackPane container, Exception e) {
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
        dialog.setDialogContainer(container);
        dialog.setTransitionType(JFXDialog.DialogTransition.RIGHT);

        layout.getStyleClass().add("error-dialog");

        dialog.show();
    }
}
