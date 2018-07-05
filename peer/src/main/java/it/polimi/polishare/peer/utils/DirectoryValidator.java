package it.polimi.polishare.peer.utils;

import com.jfoenix.validation.base.ValidatorBase;
import javafx.beans.DefaultProperty;
import javafx.scene.control.TextInputControl;

import java.io.File;

@DefaultProperty("icon")
public class DirectoryValidator extends ValidatorBase {
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
