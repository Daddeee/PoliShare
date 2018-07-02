package it.polimi.polishare.common;

import it.polimi.polishare.common.chord.Operation;

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
