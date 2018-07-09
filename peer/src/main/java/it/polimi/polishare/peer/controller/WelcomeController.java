package it.polimi.polishare.peer.controller;

import io.datafx.controller.ViewController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import javax.annotation.PostConstruct;

/**
 * Manages the welcome screen.
 */
@ViewController(value = "/view/Welcome.fxml", title = "Polishare")
public class WelcomeController {
    @FXML
    private Label text;

    private static final String welcomeMessage =
            "Benvenuto su Polishare, la piattaforma che permette una libera condivisione di appunti tra gli studenti iscritti.\n" +
            "\n" +
            "Al momento stai utilizzando la versione dell'app per utenti non autenticati, la quale permette soltanto la ricerca" +
            " degli appunti disponibili sulla nostra rete.\n" +
            "\n" +
            "Registrati inserendo la tua email e accedi alle piene funzionalità di Polishare: potrai scaricare, " +
            "caricare e consultare tutti gli appunti disponibili in rete, oltre a chattare direttamente con gli utenti online.\n";

    /**
     * Displays the welcome message.
     */
    @PostConstruct
    public void init() {
        text.setText(welcomeMessage);
    }

}
