package it.polimi.polishare.peer.controller;

import com.jfoenix.controls.JFXTextField;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import it.polimi.polishare.common.DHT.DHTException;
import it.polimi.polishare.common.DHT.model.NoteMetaData;
import it.polimi.polishare.peer.App;
import it.polimi.polishare.peer.model.Note;
import it.polimi.polishare.peer.model.NoteDAO;
import it.polimi.polishare.common.AddFailedException;
import it.polimi.polishare.peer.CurrentSession;
import it.polimi.polishare.peer.utils.Notifications;
import it.polimi.polishare.peer.utils.ThreadPool;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Manages the interface used to publish a note.
 */
@ViewController(value = "/view/Publish.fxml")
public class PublishController {
    @FXMLViewFlowContext
    private ViewFlowContext flowContext;

    @FXML
    private JFXTextField title;
    @FXML
    private JFXTextField author;
    @FXML
    private JFXTextField subject;
    @FXML
    private JFXTextField teacher;
    @FXML
    private JFXTextField year;
    @FXML
    private JFXTextField path;

    private final FileChooser fileChooser = new FileChooser();

    /**
     * Loads every input field with its corresponding validators.
     */
    @PostConstruct
    public void init() {
        title.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                title.validate();
            }
        });

        author.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                author.validate();
            }
        });

        subject.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                subject.validate();
            }
        });

        teacher.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                teacher.validate();
            }
        });

        year.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                year.validate();
            }
        });

        path.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                path.validate();
            }
        });
    }

    /**
     * Load a system-dependent interface to select a file to upload.
     */
    @FXML
    private void searchPath(){
        Stage stage = (Stage) flowContext.getRegisteredObject("Stage");
        File file = fileChooser.showOpenDialog(stage);

        if(file != null) {
            path.setText(file.getPath());
        }
    }

    /**
     * Try to save a note in the DHT. Before saving every input field is validated and the DHT is queried to see if this
     * title is already present on the network. If those conditions aren't met or something goes wrong in the file
     * publication, an error notification is shown.
     */
    @FXML
    private void save() {
        if(!(title.validate() && author.validate() && subject.validate() && teacher.validate() && year.validate() && path.validate()))
            return;

        try{
            NoteMetaData fetch = CurrentSession.getDHT().get(title.getText());
            if(fetch != null) {
                Notifications.exception(new Exception("Un file con il titolo selezionato è già presente nel sistema."));
                return;
            }
        } catch (DHTException e) {
            Notifications.exception(e);
            return;
        }

        NoteDAO noteDAO = new NoteDAO();

        NoteMetaData newNoteMetaData = new NoteMetaData(title.getText(), author.getText(), subject.getText(), teacher.getText(), Integer.parseInt(year.getText()));
        newNoteMetaData.addOwner(App.dw);
        Note newNote = new Note(title.getText(), path.getText());
        newNote.setNoteMetaData(newNoteMetaData);

        ThreadPool.getInstance().execute(() -> {
            try {
                CurrentSession.getDHT().put(newNoteMetaData.getTitle(), newNoteMetaData);
                noteDAO.create(newNote);

                Platform.runLater(() -> {
                    Notifications.confirmation("Appunto pubblicato correttamente");
                    title.clear();
                    author.clear();
                    subject.clear();
                    teacher.clear();
                    year.clear();
                    path.clear();
                });
            } catch (DHTException | AddFailedException e) {
                Platform.runLater(() -> Notifications.exception(e));
                e.printStackTrace();
            }
        });
    }
}
