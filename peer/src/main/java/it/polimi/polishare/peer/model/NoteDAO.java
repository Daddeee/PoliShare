package it.polimi.polishare.peer.model;

import it.polimi.polishare.peer.utils.DB;
import it.polimi.polishare.common.AddFailedException;
import it.polimi.polishare.common.UpdateFailedException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NoteDAO {
    public NoteDAO() {}

    public List<Note> getAll() {
        Connection c = null;
        try{
            List<Note> notes = new ArrayList<>();
            c = DB.getConnection();

            PreparedStatement getNotes = c.prepareStatement("SELECT * FROM notes");

            ResultSet rsNotes = getNotes.executeQuery();
            while (rsNotes.next()) notes.add(new Note(rsNotes.getString("title"), rsNotes.getString("path")));

            getNotes.close();
            rsNotes.close();
            DB.closeConnection(c);

            return notes;
        } catch (SQLException e){
            System.err.println("Unable to load notes");
            e.printStackTrace();
            try{
                if(c != null) DB.closeConnection(c);
            } catch (SQLException ex){
                ex.printStackTrace();
            }
            return new ArrayList<>();
        }
    }

    public void create(Note note) throws AddFailedException {
        Connection c = null;
        try{
            String fetchNoteQuery = "SELECT * FROM notes WHERE title = ?";
            String addNoteQuery = "INSERT INTO notes (title, path) VALUES (?, ?)";
            c = DB.getConnection();

            PreparedStatement fetchNote = c.prepareStatement(fetchNoteQuery);
            fetchNote.setString(1, note.getTitle());
            ResultSet rsFetch = fetchNote.executeQuery();
            rsFetch.next();
            if(rsFetch.next()) throw new AddFailedException("A note with the same title is already present.");

            PreparedStatement addNote = c.prepareStatement(addNoteQuery);
            addNote.setString(1, note.getTitle());
            addNote.setString(2, note.getPath());
            addNote.executeUpdate();

            fetchNote.close();
            addNote.close();
            rsFetch.close();
            DB.closeConnection(c);
        } catch (SQLException e){
            e.printStackTrace();
            try{
                if(c != null) DB.closeConnection(c);
            } catch (SQLException ex){
                ex.printStackTrace();
            }
            throw new AddFailedException("SQL error");
        }
    }

    public Note read(String title) {
        Connection c = null;
        try {
            Note note = null;
            c = DB.getConnection();

            PreparedStatement getNote = c.prepareStatement("SELECT * FROM notes WHERE title = ?");

            getNote.setString(1, title);
            ResultSet rsNotes = getNote.executeQuery();
            if (rsNotes.next()) note = new Note(rsNotes.getString("title"),  rsNotes.getString("path"));

            getNote.close();
            rsNotes.close();
            DB.closeConnection(c);

            return note;
        } catch (SQLException e){
            System.err.println("Unable to load notes");
            e.printStackTrace();
            try{
                if(c != null) DB.closeConnection(c);
            } catch (SQLException ex){
                ex.printStackTrace();
            }
            return null;
        }
    }

    public void update(Note note) throws UpdateFailedException {
        Connection c = null;
        try{
            String fetchNoteQuery = "SELECT * FROM notes WHERE title = ?";
            String updateNoteQuery = "UPDATE notes " +
                    "SET path = ? " +
                    "WHERE title = ?";
            c = DB.getConnection();

            PreparedStatement fetchNote = c.prepareStatement(fetchNoteQuery);
            fetchNote.setString(1, note.getTitle());
            ResultSet rsFetch = fetchNote.executeQuery();
            if(!rsFetch.next()) throw new UpdateFailedException("Cannot find the note to update.");

            PreparedStatement updateNote = c.prepareStatement(updateNoteQuery);
            updateNote.setString(2, note.getTitle());
            updateNote.setString(1, note.getPath());
            updateNote.executeUpdate();

            fetchNote.close();
            updateNote.close();
            rsFetch.close();
            DB.closeConnection(c);
        } catch (SQLException e){
            e.printStackTrace();
            try{
                if(c != null) DB.closeConnection(c);
            } catch (SQLException ex){
                ex.printStackTrace();
            }
            throw new UpdateFailedException("SQL error");
        }
    }

    public void delete(String title) {
        Connection c = null;
        try{
            c = DB.getConnection();
            PreparedStatement deleteNote = c.prepareStatement("DELETE FROM notes WHERE title = ?");
            deleteNote.setString(1, title);
            deleteNote.executeUpdate();
            deleteNote.close();
            DB.closeConnection(c);
        } catch (SQLException e){
            try{
                if(c != null) DB.closeConnection(c);
            } catch (SQLException ex){
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }
}
