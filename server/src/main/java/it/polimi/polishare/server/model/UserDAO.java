package it.polimi.polishare.server.model;

import it.polimi.polishare.common.AddFailedException;
import it.polimi.polishare.server.utils.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    public void create(String username, String password, String mail) throws AddFailedException {
        Connection c = null;
        try{
            String fetchUsernameStatement = "SELECT * FROM users WHERE username = ?";
            String fetchMailStatement = "SELECT * FROM users WHERE mail = ?";
            String addUserQuery = "INSERT INTO users (username, password, mail) VALUES (?, ?, ?)";
            c = DB.getConnection();

            PreparedStatement fetchUsername = c.prepareStatement(fetchUsernameStatement);
            fetchUsername.setString(1, username);
            ResultSet rsUsername = fetchUsername.executeQuery();
            if(rsUsername.next()){
                DB.closeConnection(c);
                throw new AddFailedException("Nome utente non disponibile.");
            }

            PreparedStatement fetchMail = c.prepareStatement(fetchMailStatement);
            fetchMail.setString(1, mail);
            ResultSet rsMail = fetchMail.executeQuery();
            if(rsMail.next()) {
                DB.closeConnection(c);
                throw new AddFailedException("Indirizzo email già utilizzato da un altro account.");
            }


            PreparedStatement addUser = c.prepareStatement(addUserQuery);
            addUser.setString(1, username);
            addUser.setString(2, password);
            addUser.setString(3, mail);
            addUser.executeUpdate();

            fetchUsername.close();
            fetchMail.close();
            addUser.close();
            rsUsername.close();
            rsMail.close();
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

    public void delete(String username) {
        Connection c = null;
        try{
            c = DB.getConnection();
            PreparedStatement deleteUser = c.prepareStatement("DELETE FROM users WHERE username = ?");
            deleteUser.setString(1, username);
            deleteUser.executeUpdate();
            deleteUser.close();
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

    public boolean checkLogin(String username, String password) {
        Connection c = null;
        boolean present = false;
        try {
            c = DB.getConnection();

            PreparedStatement check = c.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");

            check.setString(1, username);
            check.setString(2, password);
            ResultSet rs = check.executeQuery();
            if (rs.next()) present = true;

            check.close();
            rs.close();
            DB.closeConnection(c);

            return present;
        } catch (SQLException e){
            System.err.println("Impossibile verificare le credenziali.");
            e.printStackTrace();
            try{
                if(c != null) DB.closeConnection(c);
            } catch (SQLException ex){
                ex.printStackTrace();
            }
            return present;
        }
    }
}
