package it.polimi.polishare.peer.model;

import it.polimi.polishare.common.NoteMetaData;

public class Note {
    private NoteMetaData noteMetaData;
    private String title;
    private String path;

    public Note(String title, String path) {
        this.title = title;
        this.path = path;
        this.noteMetaData = null;
    }

    public String getTitle() {
        return title;
    }

    public String getPath() {
        return path;
    }

    public NoteMetaData getNoteMetaData() {
        return noteMetaData;
    }

    public void setNoteMetaData(NoteMetaData noteMetaData) {
        this.noteMetaData = noteMetaData;
    }
}
