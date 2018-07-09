package it.polimi.polishare.peer.model;

import it.polimi.polishare.common.AddFailedException;
import it.polimi.polishare.common.UpdateFailedException;
import it.polimi.polishare.peer.utils.DB;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

public class NoteDAOTest {
    private NoteDAO noteDAO;

    @BeforeEach
    public void setUp() throws SQLException {
        DB.setDbName("test.db");
        DB.setUp();
        this.noteDAO = new NoteDAO();
    }

    @Test
    public void getAll() throws AddFailedException {
        List<Note> newNotes = new ArrayList<>();
        for(int i = 0; i < 20; i++) {
            Note n = new Note("getAll" + i, "path");
            noteDAO.create(n);
            newNotes.add(n);
        }

        List<Note> result = noteDAO.getAll();

        assert (result.size() == newNotes.size() && result.containsAll(newNotes));
    }

    @Test
    public void getAllReturnsEmptyCollection() {
        List<Note> result = noteDAO.getAll();
        assert(result.isEmpty());
    }

    @Test
    public void create() throws AddFailedException {
        Note n = new Note("toCreate", "path");
        noteDAO.create(n);
        assertEquals(n, noteDAO.read("toCreate"));
    }

    @Test
    public void readReturnsNullIfNothingIsFound() {
        assertNull(noteDAO.read("dummy"));
    }

    @Test
    public void createFailsIfTitleIsAlreadyPresent() throws AddFailedException {
        Note n = new Note("toCreate", "path");
        noteDAO.create(n);

        Note n2 = new Note("toCreate", "otherPath");
        try{
            noteDAO.create(n2);
            fail("AddFailedException not thrown");
        } catch (AddFailedException e) {}
    }

    @Test
    public void update() throws AddFailedException, UpdateFailedException {
        Note n = new Note("updatable", "firstPath");
        Note nu = new Note("updatable", "secondPath");

        noteDAO.create(n);
        noteDAO.update(nu);

        assertEquals(nu.getPath(), noteDAO.read("updatable").getPath());
    }

    @Test
    public void updateFailsIfNoElementIsFound() {
        Note n = new Note("updatable", "firstPath");

        try {
            noteDAO.update(n);
            fail("UpdateFailedException not thrown");
        } catch (UpdateFailedException e) {}
    }

    @Test
    public void delete() throws AddFailedException {
        Note n = new Note("deletable", "path");
        noteDAO.create(n);

        noteDAO.delete("deletable");
        assertNull(noteDAO.read("deletable"));
    }

    @AfterEach
    public void deleteDB() throws IOException {
        Files.deleteIfExists(Paths.get("test.db"));
    }
}