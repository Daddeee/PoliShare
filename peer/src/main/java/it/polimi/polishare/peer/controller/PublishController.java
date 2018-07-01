package it.polimi.polishare.peer.controller;

import com.jfoenix.controls.JFXTextField;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import it.polimi.polishare.common.DHT.DHTException;
import it.polimi.polishare.common.DHT.NoteMetaData;
import it.polimi.polishare.peer.App;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.annotation.PostConstruct;
import java.io.File;

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

        /*path.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                path.validate();
            }
        });*/
    }

    @FXML
    private void searchPath(){
        Stage stage = (Stage) flowContext.getRegisteredObject("Stage");
        File file = fileChooser.showOpenDialog(stage);

        if(file != null) {
            path.setText(file.getPath());
        }
    }

    @FXML
    private void save() {
        if(!(title.validate() && author.validate() && subject.validate() && teacher.validate() && year.validate() /*&& path.validate()*/))
            return;

        NoteMetaData newNote = new NoteMetaData(title.getText(), author.getText(), subject.getText(), teacher.getText(), Integer.parseInt(year.getText()));

        try {
            App.dht.put(newNote.getTitle(), newNote);
            title.clear();
            author.clear();
            subject.clear();
            teacher.clear();
            year.clear();
            path.clear();
        } catch (DHTException e) {
            e.printStackTrace();

        }
    }
}
