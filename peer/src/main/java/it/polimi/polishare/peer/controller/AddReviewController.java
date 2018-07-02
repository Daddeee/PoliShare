package it.polimi.polishare.peer.controller;

import it.polimi.polishare.common.DHT.DHTException;
import it.polimi.polishare.common.ReviewMetaData;
import it.polimi.polishare.peer.App;
import it.polimi.polishare.peer.model.Note;
import it.polimi.polishare.common.UpdateReviewOperation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

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
    private TextArea newReviewBody;

    private Note note;
    private static ReviewMetaData newReview = null;

    public void initData(Note note, double height, double width){
        this.note = note;

        if(newReview == null || !newReview.getNoteMetaData().equals(note.getNoteMetaData())) {
            ReviewMetaData myReview = null;

            for (ReviewMetaData r : note.getNoteMetaData().getReviewsMetaData()) {
                if (r.getAuthor().equals(App.USERNAME)) {
                    myReview = r;
                    break;
                }
            }

            if(myReview != null)
                newReview = myReview;
            else
                newReview = new ReviewMetaData(note.getNoteMetaData(), App.USERNAME, "", 0);
        }

        initNewReviewRating();
        newReviewBody.setWrapText(true);
        newReviewBody.setText(newReview.getBody());

        main.setPrefHeight(height);
        main.setMaxHeight(height);
        main.setMinHeight(height);

        main.setPrefWidth(width);
        main.setMaxWidth(width);
        main.setMinWidth(width);
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

        try{
            App.dht.exec(note.getTitle(), new UpdateReviewOperation(newReview));
        } catch (DHTException ex){
            //TODO GESTIONE ERRORI
            ex.printStackTrace();
        }
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
}
