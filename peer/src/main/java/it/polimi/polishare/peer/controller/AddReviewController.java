package it.polimi.polishare.peer.controller;

import com.jfoenix.controls.JFXButton;
import it.polimi.polishare.common.DHT.DHTException;
import it.polimi.polishare.common.DHT.model.NoteMetaData;
import it.polimi.polishare.common.DHT.model.ReviewMetaData;
import it.polimi.polishare.peer.model.Note;
import it.polimi.polishare.common.DHT.operations.UpdateReviewOperation;
import it.polimi.polishare.peer.CurrentSession;
import it.polimi.polishare.peer.utils.Notifications;
import it.polimi.polishare.peer.utils.ThreadPool;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class AddReviewController {
    @FXML
    private VBox main;
    @FXML
    private Button newReviewFiveStar;
    @FXML
    private Button newReviewFourStar;
    @FXML
    private Button newReviewThreeStar;
    @FXML
    private Button newReviewTwoStar;
    @FXML
    private Button newReviewOneStar;
    @FXML
    private Label wordsCount;
    @FXML
    private TextArea newReviewBody;
    @FXML
    private JFXButton addReviewButton;

    private Note note;
    private CatalogController catalogController;
    private long count;

    private static ReviewMetaData newReview = null;

    public void initData(CatalogController catalogController, Note note, double height, double width){
        this.note = note;
        this.catalogController = catalogController;

        setSavingOrModifying(note);
        initNewReviewRating();

        newReviewBody.setWrapText(true);
        newReviewBody.setText(newReview.getBody());

        count = Arrays.stream(newReview.getBody().split("[ \t\n]")).count();
        wordsCount.setText(Long.toString(count));
        initWordsCount();

        setSize(height, width);
    }

    private void setSize(double height, double width) {
        main.setPrefHeight(height);
        main.setMaxHeight(height);
        main.setMinHeight(height);

        main.setPrefWidth(width);
        main.setMaxWidth(width);
        main.setMinWidth(width);
    }

    private void setSavingOrModifying(Note note) {
        if(newReview == null || !newReview.getNoteMetaData().equals(note.getNoteMetaData())) {
            ReviewMetaData myReview = null;

            for (ReviewMetaData r : note.getNoteMetaData().getReviewsMetaData()) {
                if (r.getAuthor().equals(CurrentSession.getCurrentUsername())) {
                    myReview = r;
                    break;
                }
            }

            if(myReview != null) {
                newReview = myReview;
                addReviewButton.setText("Modifica");
            } else {
                newReview = new ReviewMetaData(note.getNoteMetaData(), CurrentSession.getCurrentUsername(), "", 0);
                addReviewButton.setText("Salva");
            }
        }
    }

    @FXML
    public void setOneStar(ActionEvent e){
        newReview.setRating(1);
        initNewReviewRating();
    }

    @FXML
    public void setTwoStar(ActionEvent e){
        newReview.setRating(2);
        initNewReviewRating();
    }

    @FXML
    public void setThreeStar(ActionEvent e){
        newReview.setRating(3);
        initNewReviewRating();
    }

    @FXML
    public void setFourStar(ActionEvent e){
        newReview.setRating(4);
        initNewReviewRating();
    }

    @FXML
    public void setFiveStar(ActionEvent e){
        newReview.setRating(5);
        initNewReviewRating();
    }

    @FXML
    public void save(ActionEvent e){
        newReview.setBody(newReviewBody.getText());

        if(count > 500) {
            Notifications.exception(new Exception("Le recensioni possono avere al massimo 500 parole."));
            return;
        }

        ThreadPool.getInstance().execute(() -> {
            try{
                CurrentSession.getDHT().exec(note.getTitle(), new UpdateReviewOperation(newReview));
                NoteMetaData noteMetaData = CurrentSession.getDHT().get(note.getTitle());
                catalogController.updateData(noteMetaData);
                catalogController.getPopup().hide();
                Platform.runLater(() -> Notifications.confirmation("Recensione aggiunta con successo."));
            } catch (DHTException ex){
                Platform.runLater(() -> Notifications.exception(ex));
            }
        });

    }

    private void initNewReviewRating(){
        Button[] stars = {newReviewOneStar, newReviewTwoStar, newReviewThreeStar, newReviewFourStar, newReviewFiveStar};
        int rating = 0;

        if(newReview != null)
            rating = newReview.getRating();

        for(int i = 0; i < stars.length; i++){
            if(i < rating) stars[i].styleProperty().setValue("-fx-background-color: #5264AE;");
            else stars[i].styleProperty().setValue("-fx-background-color: rgba(82, 100, 174, 0.4);");
        }
    }

    private void initWordsCount() {
        newReviewBody.textProperty().addListener(((observableValue, oldValue, newValue) -> {
            count = Arrays.stream(newValue.split("[ \t\n]")).count();
            wordsCount.setText(Long.toString(count));
        }));
    }
}
