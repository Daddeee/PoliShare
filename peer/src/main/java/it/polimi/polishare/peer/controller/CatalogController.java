package it.polimi.polishare.peer.controller;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import it.polimi.polishare.common.DHT.DHTException;
import it.polimi.polishare.common.NoteMetaData;
import it.polimi.polishare.peer.App;
import it.polimi.polishare.peer.model.Note;
import it.polimi.polishare.peer.model.NoteDAO;
import it.polimi.polishare.common.RemoveOwnerOperation;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;

@ViewController(value = "/view/Catalog.fxml")
public class CatalogController {
    public static final String CONTENT_PANE = "ContentPane";

    @FXMLViewFlowContext
    private ViewFlowContext context;

    @FXML
    private StackPane root;
    @FXML
    private HBox filterBox;
    @FXML
    private JFXTreeTableView<CatalogTreeTableNoteMetaData> catalogTreeTableView;
    @FXML
    private JFXTreeTableColumn<CatalogTreeTableNoteMetaData, String> titleColumn;
    @FXML
    private JFXTreeTableColumn<CatalogTreeTableNoteMetaData, String> subjectColumn;
    @FXML
    private JFXTreeTableColumn<CatalogTreeTableNoteMetaData, String> authorColumn;
    @FXML
    private JFXTreeTableColumn<CatalogTreeTableNoteMetaData, String> teacherColumn;
    @FXML
    private JFXTreeTableColumn<CatalogTreeTableNoteMetaData, Integer> yearColumn;
    @FXML
    private JFXButton closeButton;

    private JFXPopup popup;
    private ObservableList<CatalogTreeTableNoteMetaData> data;

    @PostConstruct
    public void init() {
        setupCatalogTableView();
        setupContextMenu();
    }

    public void updateData(NoteMetaData noteMetaData) {
        CatalogTreeTableNoteMetaData toUpdate = null;
        for (CatalogTreeTableNoteMetaData c : data) {
            if (c.title.get().equals(noteMetaData.getTitle())) {
                toUpdate = c;
                break;
            }
        }

        if(toUpdate != null) toUpdate.getNote().setNoteMetaData(noteMetaData);
    }

    private void setupCatalogTableView() {
        setupCellValueFactory(titleColumn, CatalogTreeTableNoteMetaData::titleProperty);
        setupCellValueFactory(authorColumn, CatalogTreeTableNoteMetaData::authorProperty);
        setupCellValueFactory(subjectColumn, CatalogTreeTableNoteMetaData::subjectProperty);
        setupCellValueFactory(teacherColumn, CatalogTreeTableNoteMetaData::teacherProperty);
        setupCellValueFactory(yearColumn, p -> p.year.asObject());

        catalogTreeTableView.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);
        titleColumn.prefWidthProperty().bind(catalogTreeTableView.widthProperty().divide(catalogTreeTableView.getColumns().size()));
        authorColumn.prefWidthProperty().bind(catalogTreeTableView.widthProperty().divide(catalogTreeTableView.getColumns().size()));
        subjectColumn.prefWidthProperty().bind(catalogTreeTableView.widthProperty().divide(catalogTreeTableView.getColumns().size()));
        teacherColumn.prefWidthProperty().bind(catalogTreeTableView.widthProperty().divide(catalogTreeTableView.getColumns().size()));
        yearColumn.prefWidthProperty().bind(catalogTreeTableView.widthProperty().divide(catalogTreeTableView.getColumns().size()));

        data = getTableData();
        catalogTreeTableView.setRoot(new RecursiveTreeItem<>(data, RecursiveTreeObject::getChildren));

        catalogTreeTableView.setShowRoot(false);
    }

    private <T> void setupCellValueFactory(JFXTreeTableColumn<CatalogTreeTableNoteMetaData, T> column, Function<CatalogTreeTableNoteMetaData, ObservableValue<T>> mapper) {
        column.setCellValueFactory((TreeTableColumn.CellDataFeatures<CatalogTreeTableNoteMetaData, T> param) -> {
            if (column.validateValue(param)) {
                return mapper.apply(param.getValue().getValue());
            } else {
                return column.getComputedValue(param);
            }
        });
    }

    private ChangeListener<String> setupSearchField(final JFXTreeTableView<CatalogTreeTableNoteMetaData> tableView) {
        return (o, oldVal, newVal) ->
                tableView.setPredicate(noteMetaDataProp -> {
                    final CatalogTreeTableNoteMetaData noteMetaData = noteMetaDataProp.getValue();
                    return noteMetaData.title.get().toLowerCase().contains(newVal.toLowerCase())
                            || noteMetaData.author.get().toLowerCase().contains(newVal.toLowerCase())
                            || noteMetaData.subject.get().toLowerCase().contains(newVal.toLowerCase())
                            || noteMetaData.teacher.get().toLowerCase().contains(newVal.toLowerCase())
                            || Integer.toString(noteMetaData.year.get()).contains(newVal.toLowerCase());
                });
    }

    private ObservableList<CatalogTreeTableNoteMetaData> getTableData() {
        final ObservableList<CatalogTreeTableNoteMetaData> data = FXCollections.observableArrayList();
        NoteDAO noteDAO = new NoteDAO();

        List<Note> myNotes = noteDAO.getAll();

        for(Note n : myNotes) {
            try {
                NoteMetaData noteMetaData = App.dht.get(n.getTitle());
                n.setNoteMetaData(noteMetaData);

                data.add(new CatalogTreeTableNoteMetaData(n));
            } catch (DHTException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    private void setupContextMenu() {
        catalogTreeTableView.setRowFactory(noteInfosTreeTableView -> {
            final TreeTableRow<CatalogTreeTableNoteMetaData> row = new TreeTableRow<>();
            final ContextMenu contextMenu = new ContextMenu();

            MenuItem reviews = new MenuItem("Visualizza Recensioni");
            reviews.setOnAction(e -> {
                double popupHeight = catalogTreeTableView.getHeight()*4/5;
                double popupWidth = catalogTreeTableView.getWidth()*4/5;

                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/popup/Reviews.fxml"));
                    popup = new JFXPopup(loader.load());

                    ((ReviewsController) loader.getController()).initData(row.getTreeItem().getValue().getNote(), popupHeight, popupWidth);
                } catch (IOException ioExc) {
                    ioExc.printStackTrace();
                }
                Parent root = (Parent) context.getRegisteredObject("Root");
                Bounds rootBounds = root.getLayoutBounds();

                popup.show(root, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT,
                        (rootBounds.getWidth() - popupWidth) / 2,
                        (rootBounds.getHeight() - popupHeight) / 2);
            });

            MenuItem addReview = new MenuItem("Aggiungi Recensione");
            addReview.setOnAction(e -> {
                double popupHeight = catalogTreeTableView.getHeight()*4/5;
                double popupWidth = catalogTreeTableView.getWidth()*4/5;

                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/popup/AddReview.fxml"));
                    popup = new JFXPopup(loader.load());

                    ((AddReviewController) loader.getController()).initData(this, row.getTreeItem().getValue().getNote(), popupHeight, popupWidth);
                } catch (IOException ioExc) {
                    ioExc.printStackTrace();
                }
                Parent root = (Parent) context.getRegisteredObject("Root");
                Bounds rootBounds = root.getLayoutBounds();

                popup.show(root, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT,
                        (rootBounds.getWidth() - popupWidth) / 2,
                        (rootBounds.getHeight() - popupHeight) / 2);
            });

            MenuItem delete = new MenuItem("Elimina");
            delete.setOnAction(e -> {
                try {
                    App.dht.exec(row.getTreeItem().getValue().title.get(), new RemoveOwnerOperation(App.dw));

                    NoteDAO noteDAO = new NoteDAO();
                    noteDAO.delete(row.getTreeItem().getValue().title.get());
                } catch (DHTException ex) {
                    //TODO errori
                }
            });

            contextMenu.getItems().addAll(reviews, addReview, delete);

            row.setContextMenu(contextMenu);
            return row;
        });
    }

    private class CatalogTreeTableNoteMetaData extends RecursiveTreeObject<CatalogController.CatalogTreeTableNoteMetaData> {
        final StringProperty title;
        final StringProperty subject;
        final StringProperty author;
        final StringProperty teacher;
        final SimpleIntegerProperty year;
        final Note note;

        CatalogTreeTableNoteMetaData(Note note) {
            this.title = new SimpleStringProperty(note.getNoteMetaData().getTitle());
            this.subject = new SimpleStringProperty(note.getNoteMetaData().getSubject());
            this.author = new SimpleStringProperty(note.getNoteMetaData().getAuthor());
            this.teacher = new SimpleStringProperty(note.getNoteMetaData().getTeacher());
            this.year = new SimpleIntegerProperty(note.getNoteMetaData().getYear());

            this.note = note;
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

        Note getNote() {
            return note;
        }


    }
}