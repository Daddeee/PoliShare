package it.polimi.polishare.common;

import java.io.Serializable;
import java.util.function.Predicate;

public class NoteMetaDataQueryGenerator implements Serializable {
    public Predicate<NoteMetaData> getPredicate(String titleQuery, String authorQuery, String subjectQuery, String teacherQuery, String yearQuery, int ratingQuery) {
        return (Predicate<NoteMetaData> & Serializable) (note) -> note.getTitle().toLowerCase().contains(titleQuery.toLowerCase()) &&
                note.getAuthor().toLowerCase().contains(authorQuery.toLowerCase()) &&
                note.getSubject().toLowerCase().contains(subjectQuery.toLowerCase()) &&
                note.getTeacher().toLowerCase().contains(teacherQuery.toLowerCase()) &&
                Integer.toString(note.getYear()).contains(yearQuery.toLowerCase()) &&
                note.averageRating() >= ratingQuery;
    }
}
