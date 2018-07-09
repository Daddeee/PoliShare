package it.polimi.polishare.peer.controller;

import com.jfoenix.controls.JFXTextField;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import it.polimi.polishare.common.server.exceptions.RegistrationFailedException;
import it.polimi.polishare.peer.App;
import it.polimi.polishare.peer.utils.Notifications;
import it.polimi.polishare.peer.utils.ThreadPool;
import javafx.application.Platform;
import javafx.fxml.FXML;

import javax.annotation.PostConstruct;
import java.rmi.RemoteException;

/**
 * Manages the interface used to register a new user on the platform.
 */
@ViewController(value = "/view/Register.fxml", title = "Polishare")
public class RegisterController {
    @FXMLViewFlowContext
    private ViewFlowContext context;

    @FXML
    private JFXTextField email;
    @FXML
    private JFXTextField username;

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

        email.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                email.validate();
            }
        });
    }

    /**
     * Validates the user input and call the server to register a new user with the given mail and username values.
     * If the registration is succesfull a confirm notification is shown, otherwise an error notification is shown.
     */
    @FXML
    private void register() {
        if(!(username.validate() && email.validate()))
            return;

        ThreadPool.getInstance().execute(() -> {
            try {
                App.sf.register(email.getText(), username.getText());

                email.clear();
                username.clear();
                Platform.runLater(() -> Notifications.confirmation("Registrazione avvenuta con successo"));
            } catch (RemoteException | RegistrationFailedException e) {
                Platform.runLater(() -> Notifications.exception(e));
            }
        });
    }
}
