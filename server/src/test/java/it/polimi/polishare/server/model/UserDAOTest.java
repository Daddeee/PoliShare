package it.polimi.polishare.server.model;


import it.polimi.polishare.common.AddFailedException;
import it.polimi.polishare.server.utils.DB;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;

public class UserDAOTest {
    private UserDAO userDAO = new UserDAO();

    @BeforeEach
    public void setUp() throws SQLException {
        DB.setDbName("test.db");
        DB.setUp();

        this.userDAO = userDAO;
    }

    @Test
    public void create() throws AddFailedException {
        userDAO.create("username", "password", "email");
        assert (userDAO.checkLogin("username", "password"));
    }

    @Test
    public void createFailsIfUsernameIsAlreadyTaken() throws AddFailedException {
        userDAO.create("username", "password", "email");
        try {
            userDAO.create("username", "password", "email2");
            Assertions.fail("AddFailedException non sollevata");
        } catch (AddFailedException e) {}
    }

    @Test
    public void createFailsIfMailIsAlreadyTaken() throws AddFailedException {
        userDAO.create("username", "password", "email");
        try {
            userDAO.create("username2", "password", "email");
            Assertions.fail("AddFailedException non sollevata");
        } catch (AddFailedException e) {}
    }

    @Test
    public void delete() throws AddFailedException {
        userDAO.create("username", "password", "email");
        userDAO.delete("username");

        assert (!userDAO.checkLogin("username", "password"));
    }

    @AfterEach
    public void tearDown() throws IOException {
        Files.delete(Paths.get("test.db"));
    }
}