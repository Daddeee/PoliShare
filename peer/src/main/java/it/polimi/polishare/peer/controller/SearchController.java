package it.polimi.polishare.peer.controller;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import it.polimi.polishare.common.DHT.DHTException;
import it.polimi.polishare.common.Downloader;
import it.polimi.polishare.common.NoteMetaData;
import it.polimi.polishare.common.NoteMetaDataQueryGenerator;
import it.polimi.polishare.peer.App;
import it.polimi.polishare.peer.model.Note;
import it.polimi.polishare.peer.model.NoteDAO;
import it.polimi.polishare.common.AddOwnerOperation;
import it.polimi.polishare.common.RemoveOwnerOperation;
import it.polimi.polishare.common.AddFailedException;
import it.polimi.polishare.peer.utils.CurrentSession;
import it.polimi.polishare.peer.utils.DownloadManager;
import it.polimi.polishare.peer.utils.Notifications;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.annotation.PostConstruct;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

@ViewController(value = "/view/Search.fxml", title = "Polishare")
public class SearchController {
    @FXMLViewFlowContext
    private ViewFlowContext context;

    @FXML
    private StackPane root;
    @FXML
    private JFXComboBox<Integer> rating;
    @FXML
    private JFXTextField titleField;
    @FXML
    private JFXTextField authorField;
    @FXML
    private JFXTextField subjectField;
    @FXML
    private JFXTextField teacherField;
    @FXML
    private JFXTextField yearField;
    @FXML
    private JFXTreeTableView<SearchTreeTableNoteMetaData> catalogTreeTableView;
    @FXML
    private JFXTreeTableColumn<SearchTreeTableNoteMetaData, String> titleColumn;
    @FXML
    private JFXTreeTableColumn<SearchTreeTableNoteMetaData, String> subjectColumn;
    @FXML
    private JFXTreeTableColumn<SearchTreeTableNoteMetaData, String> authorColumn;
    @FXML
    private JFXTreeTableColumn<SearchTreeTableNoteMetaData, String> teacherColumn;
    @FXML
    private JFXTreeTableColumn<SearchTreeTableNoteMetaData, Integer> yearColumn;
    @FXML
    private HBox filterBox;
    @FXML
    private JFXDialog downloadDialog;
    @FXML
    private JFXTextField path;

    public ObservableList<SearchTreeTableNoteMetaData> data = FXCollections.observableArrayList();

    @FXML
    public void query() {
        if(!yearField.validate()) return;
        if(rating.getSelectionModel().getSelectedItem() == null) rating.getSelectionModel().selectFirst();

        List<NoteMetaData> notes = new ArrayList<>();
        try{
            NoteMetaDataQueryGenerator generator = new NoteMetaDataQueryGenerator();
            Predicate<NoteMetaData> queryPredicate = generator.getPredicate(
                    titleField.getText(),
                    authorField.getText(),
                    subjectField.getText(),
                    teacherField.getText(),
                    yearField.getText(),
                    rating.getSelectionModel().getSelectedItem()
            );

            notes = CurrentSession.getDHT().query(queryPredicate);
        } catch (DHTException e) {
            e.printStackTrace();
        }

        updateTableData(notes);
    }

    @FXML
    public void clear() {
        rating.getSelectionModel().clearSelection();
        titleField.clear();
        authorField.clear();
        subjectField.clear();
        teacherField.clear();
        yearField.clear();
    }

    @FXML
    public void showDownload() {
        downloadDialog.setTransitionType(JFXDialog.DialogTransition.CENTER);
        downloadDialog.show((StackPane) context.getRegisteredObject("Root"));
    }

    @FXML
    public void download() {
        if(catalogTreeTableView.getSelectionModel().getSelectedItem() == null) return;
        NoteDAO noteDAO = new NoteDAO();
        NoteMetaData info = catalogTreeTableView.getSelectionModel().getSelectedItem().getValue().getNoteMetaData();

        Note n = noteDAO.read(info.getTitle());
        if(n != null) {
            Notifications.exception(new Exception("Possiedi già questo file."));
            return;
        }

        String path = this.path.getText();
        Note newNote = new Note(info.getTitle(), path + "/" + info.getTitle() + ".pdf");
        newNote.setNoteMetaData(info);
        new Thread(() -> DownloadManager.download(newNote) ).start();
        downloadDialog.close();
    }

    @PostConstruct
    public void init() {
        setupCatalogTableView();

        ObservableList<Integer> ratingsValue = FXCollections.observableArrayList();
        ratingsValue.addAll(0, 1, 2, 3, 4, 5);
        rating.setItems(ratingsValue);
    }

    @FXML
    private void searchPath(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        Stage stage = (Stage) context.getRegisteredObject("Stage");
        File file = directoryChooser.showDialog(stage);

        if(file != null) {
            path.setText(file.getPath());
        }
    }

    private void setupCatalogTableView() {
        setupCellValueFactory(titleColumn, SearchTreeTableNoteMetaData::titleProperty);
        setupCellValueFactory(authorColumn, SearchTreeTableNoteMetaData::authorProperty);
        setupCellValueFactory(subjectColumn, SearchTreeTableNoteMetaData::subjectProperty);
        setupCellValueFactory(teacherColumn, SearchTreeTableNoteMetaData::teacherProperty);
        setupCellValueFactory(yearColumn, p -> p.year.asObject());

        catalogTreeTableView.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);
        titleColumn.prefWidthProperty().bind(catalogTreeTableView.widthProperty().divide(catalogTreeTableView.getColumns().size()));
        authorColumn.prefWidthProperty().bind(catalogTreeTableView.widthProperty().divide(catalogTreeTableView.getColumns().size()));
        subjectColumn.prefWidthProperty().bind(catalogTreeTableView.widthProperty().divide(catalogTreeTableView.getColumns().size()));
        teacherColumn.prefWidthProperty().bind(catalogTreeTableView.widthProperty().divide(catalogTreeTableView.getColumns().size()));
        yearColumn.prefWidthProperty().bind(catalogTreeTableView.widthProperty().divide(catalogTreeTableView.getColumns().size()));

        catalogTreeTableView.setRoot(new RecursiveTreeItem<>(data, RecursiveTreeObject::getChildren));
        catalogTreeTableView.setShowRoot(false);
    }

    private <T> void setupCellValueFactory(JFXTreeTableColumn<SearchTreeTableNoteMetaData, T> column, Function<SearchTreeTableNoteMetaData, ObservableValue<T>> mapper) {
        column.setCellValueFactory((TreeTableColumn.CellDataFeatures<SearchTreeTableNoteMetaData, T> param) -> {
            if (column.validateValue(param)) {
                return mapper.apply(param.getValue().getValue());
            } else {
                return column.getComputedValue(param);
            }
        });
    }

    private void updateTableData(List<NoteMetaData> notes) {
        data.clear();

        for(NoteMetaData n : notes)
            data.add(new SearchTreeTableNoteMetaData(n));
    }

    private class SearchTreeTableNoteMetaData extends RecursiveTreeObject<SearchTreeTableNoteMetaData> {
        final StringProperty title;
        final StringProperty subject;
        final StringProperty author;
        final StringProperty teacher;
        final SimpleIntegerProperty year;
        final NoteMetaData noteMetaData;

        SearchTreeTableNoteMetaData(NoteMetaData noteMetaData) {
            this.title = new SimpleStringProperty(noteMetaData.getTitle());
            this.subject = new SimpleStringProperty(noteMetaData.getSubject());
            this.author = new SimpleStringProperty(noteMetaData.getAuthor());
            this.teacher = new SimpleStringProperty(noteMetaData.getTeacher());
            this.year = new SimpleIntegerProperty(noteMetaData.getYear());

            this.noteMetaData = noteMetaData;
        }

        StringProperty titleProperty() {
            return title;
        }

        StringProperty subjectProperty() {
            return subject;
        }

        StringProperty authorProperty() {
            return author;
        }

        StringProperty teacherProperty() {
            return teacher;
        }

        NoteMetaData getNoteMetaData() {
            return noteMetaData;
        }
    }
}
