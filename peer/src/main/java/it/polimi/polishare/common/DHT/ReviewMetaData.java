package it.polimi.polishare.common.DHT;

import java.io.Serializable;
import java.util.Objects;

public class ReviewMetaData implements Serializable {
    private NoteMetaData noteMetaData;
    private String author;
    private String body;
    private int rating;

    public ReviewMetaData(NoteMetaData noteMetaData, String author, String body, int rating) {
        this.noteMetaData = noteMetaData;
        this.author = author;
        this.body = body;
        this.rating = rating;
    }

    public NoteMetaData getNoteMetaData() {
        return noteMetaData;
    }

    public void setNoteMetaData(NoteMetaData noteMetaData) {
        this.noteMetaData = noteMetaData;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReviewMetaData reviewMetaData = (ReviewMetaData) o;
        return Objects.equals(noteMetaData, reviewMetaData.noteMetaData) &&
                Objects.equals(author, reviewMetaData.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(noteMetaData, author);
    }
}
