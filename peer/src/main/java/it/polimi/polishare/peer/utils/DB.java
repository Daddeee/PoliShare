package it.polimi.polishare.peer.utils;

import it.polimi.polishare.peer.App;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DB {
    private static String dbName = App.USERNAME + ".db";

    public static void setUp() throws SQLException {
        String createNotesTable = "CREATE TABLE IF NOT EXISTS notes (" +
                "title      VARCHAR(40) PRIMARY KEY NOT NULL," +
                "path       TEXT" +
                ");";

        Connection c = getConnection();

        Statement stmnt = c.createStatement();

        stmnt.executeUpdate(createNotesTable);

        stmnt.close();

        c.close();
    }

    public static void setDbName(String db){
        dbName = db;
    }

    public static Connection getConnection() throws SQLException {
        Connection c = DriverManager.getConnection("jdbc:sqlite:" + dbName);
        return c;
    }

    public static void closeConnection(Connection c) throws SQLException {
        c.close();
    }
}
