package it.polimi.polishare.peer.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DB {
    private static String dbName =  "polishare.db";

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

    public static void setDbName(String dbName){
        DB.dbName = dbName;
    }

    public static Connection getConnection() throws SQLException {
        Connection c = DriverManager.getConnection("jdbc:sqlite:" + dbName);
        return c;
    }

    public static void closeConnection(Connection c) throws SQLException {
        c.close();
    }
}
