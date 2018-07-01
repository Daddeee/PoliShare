package it.polimi.polishare.peer.utils;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.j256.simplemagic.ContentType;
import com.jfoenix.validation.base.ValidatorBase;
import javafx.beans.DefaultProperty;
import javafx.scene.control.TextInputControl;

import java.io.File;
import java.io.IOException;

@DefaultProperty("icon")
public class PdfValidator extends ValidatorBase {
    @Override
    protected void eval() {
        ContentInfoUtil util = new ContentInfoUtil();
        TextInputControl textField = (TextInputControl)this.srcControl.get();
        if (textField.getText() != null && !textField.getText().isEmpty() && isFile(textField.getText())) {
            File possiblePdf = new File(textField.getText());
            try{
                ContentInfo info = util.findMatch(possiblePdf);
                if(info.getContentType().equals(ContentType.PDF)) {
                    if(possiblePdf.length() <= 10000000){
                        this.hasErrors.set(false);
                    } else {
                        message.set("Il file selezionato può essere grande al massimo 10 MB.");
                        this.hasErrors.set(true);
                    }
                } else {
                    message.set("Il file selezionato deve essere in formato pdf.");
                    this.hasErrors.set(true);
                }
            } catch (IOException e) {
                message.set("Il percorso selezionato non corrisponde ad un file.");
                this.hasErrors.set(true);
            }

        } else {
            message.set("Il percorso selezionato non corrisponde ad un file.");
            this.hasErrors.set(true);
        }
    }

    private boolean isFile(String path) {
        File f = new File(path);
        return f.exists() && !f.isDirectory();
    }
}