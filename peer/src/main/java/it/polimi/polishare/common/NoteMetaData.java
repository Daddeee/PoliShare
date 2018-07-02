package it.polimi.polishare.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NoteMetaData implements Serializable {
    private String title;
    private String author;
    private String subject;
    private String teacher;
    private int year;
    private List<ReviewMetaData> reviewsMetaData;
    private List<Downloader> owners;

    public NoteMetaData(String title, String author, String subject, String teacher, int year) {
        this.title = title;
        this.author = author;
        this.subject = subject;
        this.teacher = teacher;
        this.year = year;
        this.reviewsMetaData = new ArrayList<>();
        this.owners = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public List<ReviewMetaData> getReviewsMetaData() {
        return reviewsMetaData;
    }

    public void setReviewsMetaData(List<ReviewMetaData> reviewsMetaData) {
        this.reviewsMetaData = reviewsMetaData;
    }

    public List<Downloader> getOwners() {
        return owners;
    }

    public void addOwner(Downloader owner) {
        this.owners.add(owner);
    }

    public void removeOwner(Downloader owner) {
        this.owners.remove(owner);
    }

    public double averageRating() {
        return reviewsMetaData.stream().mapToInt(ReviewMetaData::getRating).average().orElse(0.0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NoteMetaData that = (NoteMetaData) o;
        return Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title);
    }
}
