package it.polimi.polishare.peer.controller;
import com.jfoenix.controls.JFXProgressBar;
import it.polimi.polishare.common.DHT.DHTException;
import it.polimi.polishare.common.DHT.model.ReviewMetaData;
import it.polimi.polishare.peer.model.Note;
import it.polimi.polishare.peer.CurrentSession;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class ReviewsController {
    @FXML
    private VBox main;
    @FXML
    private VBox reviewsBox;

    @FXML
    private JFXProgressBar fiveStarsBar;
    @FXML
    private JFXProgressBar fourStarsBar;
    @FXML
    private JFXProgressBar threeStarsBar;
    @FXML
    private JFXProgressBar twoStarsBar;
    @FXML
    private JFXProgressBar oneStarsBar;

    @FXML
    private Label fiveStarsPercentLabel;
    @FXML
    private Label fourStarsPercentLabel;
    @FXML
    private Label threeStarsPercentLabel;
    @FXML
    private Label twoStarsPercentLabel;
    @FXML
    private Label oneStarsPercentLabel;

    private Note note;

    public void initData(Note note, double height, double width){
        this.note = note;

        try {
            this.note.setNoteMetaData(CurrentSession.getDHT().get(note.getTitle()));
        } catch (DHTException e) {}


        int[] starsCount = new int[6];
        double total = 0;

        main.setPrefHeight(height);
        main.setMaxHeight(height);
        main.setMinHeight(height);

        main.setPrefWidth(width);
        main.setMaxWidth(width);
        main.setMinWidth(width);

        for(ReviewMetaData r : note.getNoteMetaData().getReviewsMetaData()){
            starsCount[r.getRating()] += 1;
            total++;
            addReview(r);
        }

        double fiveOnTotal = (total == 0) ? 0 : starsCount[5]/total;
        double fivePercent = fiveOnTotal * 100;
        fiveStarsBar.progressProperty().setValue(fiveOnTotal);
        fiveStarsPercentLabel.setText(Integer.toString((int) fivePercent) + "%");

        double fourOnTotal = (total == 0) ? 0 : starsCount[4]/total;
        double fourPercent = fourOnTotal * 100;
        fourStarsBar.progressProperty().setValue(fourOnTotal);
        fourStarsPercentLabel.setText(Integer.toString((int) fourPercent) + "%");

        double threeOnTotal = (total == 0) ? 0 : starsCount[3]/total;
        double threePercent = threeOnTotal * 100;
        threeStarsBar.progressProperty().setValue(threeOnTotal);
        threeStarsPercentLabel.setText(Integer.toString((int) threePercent) + "%");

        double twoOnTotal = (total == 0) ? 0 : starsCount[2]/total;
        double twoPercent = twoOnTotal * 100;
        twoStarsBar.progressProperty().setValue(twoOnTotal);
        twoStarsPercentLabel.setText(Integer.toString((int) twoPercent) + "%");

        double oneOnTotal = (total == 0) ? 0 : starsCount[1]/total;
        double onePercent = oneOnTotal * 100;
        oneStarsBar.progressProperty().setValue(oneOnTotal);
        oneStarsPercentLabel.setText(Integer.toString((int) onePercent) + "%");
    }

    private void addReview(ReviewMetaData r){
        VBox reviewVBox = new VBox();
        reviewVBox.setSpacing(15.0);

        HBox reviewHeader = getReviewHeader(r);
        VBox.setMargin(reviewHeader, new Insets(5.0, 10.0, 5.0, 10.0));
        Label text = new Label(r.getBody());
        text.setWrapText(true);
        VBox.setMargin(text, new Insets(5., 10.0, 5.0, 10.0));

        reviewVBox.getChildren().add(new Separator());
        reviewVBox.getChildren().add(reviewHeader);
        reviewVBox.getChildren().add(text);

        reviewsBox.getChildren().add(reviewVBox);
    }

    private HBox getReviewHeader(ReviewMetaData r) {
        HBox reviewHeader = new HBox();
        reviewHeader.setAlignment(Pos.CENTER);

        Region padding = new Region();

        HBox rating = new HBox();
        rating.setSpacing(2.0);
        for(int i = 1; i < 6; i++){
            Button btn = new Button();
            btn.getStyleClass().add("star-button");
            if(i <= r.getRating()) btn.styleProperty().setValue("-icon-paint: #5264AE;");
            rating.getChildren().add(btn);
        }

        reviewHeader.getChildren().add(new Label(r.getAuthor()));
        reviewHeader.getChildren().add(padding);
        reviewHeader.getChildren().add(rating);
        HBox.setHgrow(padding, Priority.ALWAYS);
        return reviewHeader;
    }
}
