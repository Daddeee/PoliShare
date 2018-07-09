package it.polimi.polishare.peer.controller.validators;

import com.jfoenix.validation.base.ValidatorBase;
import javafx.beans.DefaultProperty;
import javafx.scene.control.TextInputControl;

import java.io.File;

/**
 * A validator to test if the path entered by the user in a JFXTextField corresponds to a directory.
 */
@DefaultProperty("icon")
public class DirectoryValidator extends ValidatorBase {
    /**
     * Check if the entered path is not null and corresponds to a directory.
     */
    @Override
    protected void eval() {
        TextInputControl textField = (TextInputControl)this.srcControl.get();
        if (textField.getText() != null && !textField.getText().isEmpty() && isDirectory(textField.getText())) {
            this.hasErrors.set(false);
        } else {
            this.hasErrors.set(true);
            message.set("Il percorso selezionato non corrisponde ad una cartella.");
        }
    }

    private boolean isDirectory(String path) {
        File f = new File(path);
        return f.exists() && f.isDirectory();
    }
}
