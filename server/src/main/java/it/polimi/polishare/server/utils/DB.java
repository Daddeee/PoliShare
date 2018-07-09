package it.polimi.polishare.server.utils;

import it.polimi.polishare.server.App;

import java.sql.*;

public class DB {
    private static String dbName = "server.db";

    public static void setUp() throws SQLException {
        String createNotesTable =
                "CREATE TABLE IF NOT EXISTS users (" +
                "username      VARCHAR(40) PRIMARY KEY NOT NULL," +
                "password      VARCHAR(40) NOT NULL," +
                "mail          VARCHAR(40) UNIQUE NOT NULL" +
                ");";

        //String addUserQuery = "INSERT INTO users (username, password, mail) VALUES (?, ?, ?)";
        Connection c = getConnection();

        Statement stmnt = c.createStatement();
        stmnt.executeUpdate(createNotesTable);
        stmnt.close();

        /*PreparedStatement addUser = c.prepareStatement(addUserQuery);
        addUser.setString(1, App.DHT_NAME);
        addUser.setString(2, new RandomString(20).nextString());
        addUser.setString(3, "polishare@outlook.com");
        addUser.executeUpdate();*/

        c.close();
    }

    public static void setDbName(String db){
        dbName = db;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + dbName);
    }

    public static void closeConnection(Connection c) throws SQLException {
        c.close();
    }
}

