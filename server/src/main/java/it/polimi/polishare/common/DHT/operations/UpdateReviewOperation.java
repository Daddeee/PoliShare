package it.polimi.polishare.common.DHT.operations;

import it.polimi.polishare.common.DHT.chord.Operation;
import it.polimi.polishare.common.DHT.model.NoteMetaData;
import it.polimi.polishare.common.DHT.model.ReviewMetaData;

import java.io.Serializable;

public class UpdateReviewOperation implements Operation, Serializable {
    private ReviewMetaData reviewMetaData;

    public UpdateReviewOperation(ReviewMetaData reviewMetaData) {
        this.reviewMetaData = reviewMetaData;
    }

    @Override
    public void execute(Object o) {
        ((NoteMetaData) o).addReviewMetaData(reviewMetaData);
    }
}
