package it.polimi.polishare.peer.controller;

import com.jfoenix.controls.JFXTextField;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import it.polimi.polishare.common.DHT.DHTException;
import it.polimi.polishare.common.unauthenticated.LoginFailedException;
import it.polimi.polishare.common.unauthenticated.RegistrationFailedException;
import it.polimi.polishare.peer.App;
import javafx.fxml.FXML;

import javax.annotation.PostConstruct;
import java.rmi.RemoteException;

@ViewController(value = "/view/Register.fxml", title = "Polishare")
public class RegisterController {
    @FXMLViewFlowContext
    private ViewFlowContext flowContext;

    @FXML
    private JFXTextField email;
    @FXML
    private JFXTextField username;

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

    @FXML
    private void register() {
        if(!(username.validate() && email.validate()))
            return;

        try {
            App.us.register(email.getText(), username.getText());

            email.clear();
            username.clear();
            //TODO FLOW ?
        } catch (RegistrationFailedException e) {
            //TODO ERRORE
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
