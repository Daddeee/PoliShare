package it.polimi.polishare.peer.controller;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import it.polimi.polishare.common.DHT.DHTException;
import it.polimi.polishare.common.DHT.model.NoteMetaData;
import it.polimi.polishare.common.DHT.model.NoteMetaDataQueryGenerator;
import it.polimi.polishare.peer.App;
import it.polimi.polishare.peer.CurrentSession;
import it.polimi.polishare.peer.utils.Notifications;
import it.polimi.polishare.peer.utils.ThreadPool;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import javax.annotation.PostConstruct;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Manages the interface used by unauthenticated users to query the DHT.
 */
@ViewController(value = "/view/Search.fxml", title = "Polishare")
public class UnauthenticatedSearchController {
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
    private JFXButton downloadButton;
    @FXML
    private JFXSpinner searchSpinner;

    private BooleanProperty isSearching = new SimpleBooleanProperty(false);

    private ObservableList<SearchTreeTableNoteMetaData> data = FXCollections.observableArrayList();

    /**
     * Query the DHT with the given values of each filter and displays the results in the result's table.
     * If something with the query goes wrong, an error notification is shown.
     */
    @FXML
    public void query() {
        if(!yearField.validate()) return;
        if(rating.getSelectionModel().getSelectedItem() == null) rating.getSelectionModel().selectFirst();

        NoteMetaDataQueryGenerator generator = new NoteMetaDataQueryGenerator();
        Predicate<NoteMetaData> queryPredicate = generator.getPredicate(
                titleField.getText(),
                authorField.getText(),
                subjectField.getText(),
                teacherField.getText(),
                yearField.getText(),
                rating.getSelectionModel().getSelectedItem()
        );

        isSearching.setValue(true);
        ThreadPool.getInstance().execute(() -> {
            try{
                List<NoteMetaData>  notes = CurrentSession.getDHT().query(queryPredicate);
                isSearching.setValue(false);
                updateTableData(notes);
            } catch (DHTException e) {
                Platform.runLater(() -> Notifications.exception(e));
            }
        });
    }

    /**
     * Resets the filter fields.
     */
    @FXML
    public void clear() {
        rating.getSelectionModel().clearSelection();
        titleField.clear();
        authorField.clear();
        subjectField.clear();
        teacherField.clear();
        yearField.clear();
    }

    /**
     * Unused in this controller.
     */
    @FXML
    public void searchPath() {}

    /**
     * Unused in this controller.
     */
    @FXML
    public void download() {}

    /**
     * Unused in this controller.
     */
    @FXML
    public void showDownload() {}

    /**
     * Loads the table and the filter's components.
     */
    @PostConstruct
    public void init() {
        downloadButton.setVisible(false);
        setupCatalogTableView();
        searchSpinner.visibleProperty().bind(isSearching);

        ObservableList<Integer> ratingsValue = FXCollections.observableArrayList();
        ratingsValue.addAll(0, 1, 2, 3, 4, 5);
        rating.setItems(ratingsValue);
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
