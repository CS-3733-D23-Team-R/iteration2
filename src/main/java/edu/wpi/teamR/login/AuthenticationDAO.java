package edu.wpi.teamR.login;
import edu.wpi.teamR.Configuration;
import edu.wpi.teamR.ItemNotFoundException;

import java.sql.*;

public class AuthenticationDAO {
    private static AuthenticationDAO instance;
    private AuthenticationDAO(){

    }
    public static AuthenticationDAO getInstance() {
        if(AuthenticationDAO.instance == null){
            AuthenticationDAO.instance = new AuthenticationDAO();
        }
        return instance;
    }
    public User addUser(String userID, String password, AccessLevel accessLevel) throws SQLException, ClassNotFoundException {
        Connection connection = Configuration.getConnection();
        Statement statement = connection.createStatement();
        statement.executeUpdate("INSERT INTO "+Configuration.getUserTableSchemaNameTableName()+"(username, password, accesslevel) VALUES ('"+userID+"', '"+password+"', '"+accessLevel.toString()+"');");
        return new User(userID, password, accessLevel);
    }
    public User modifyUserAccessByID(String userID, AccessLevel accessLevel) throws SQLException, ClassNotFoundException {
        Connection connection = Configuration.getConnection();
        Statement modify = connection.createStatement();
        modify.executeUpdate("UPDATE "+Configuration.getUserTableSchemaNameTableName()+" SET accesslevel = '"+accessLevel.toString()+"' WHERE username = '"+userID+"';");
        Statement getUpdated = connection.createStatement();
        ResultSet resultSet = getUpdated.executeQuery("SELECT * FROM "+Configuration.getUserTableSchemaNameTableName()+" WHERE username = '"+userID+"';");
        resultSet.next();
        String aUsername = resultSet.getString("username");
        String aPassword = resultSet.getString("password");
        AccessLevel anAccessLevel = AccessLevel.valueOf(resultSet.getString("accesslevel"));
        User temp = new User(aUsername, aPassword, anAccessLevel);
        return temp;
    }
    public void removeUserByID(String userID) throws SQLException, ClassNotFoundException {
        Connection connection = Configuration.getConnection();
        Statement statement = connection.createStatement();
        statement.executeUpdate("DELETE FROM "+Configuration.getUserTableSchemaNameTableName()+" WHERE username = '"+userID+"';");
    }
    public void deleteALLUsers() throws SQLException, ClassNotFoundException {
        Connection connection = Configuration.getConnection();
        Statement statement = connection.createStatement();
        statement.executeUpdate("DELETE FROM "+Configuration.getUserTableSchemaNameTableName()+";");
    }

    public User getUser(String userID) throws SQLException, ClassNotFoundException, ItemNotFoundException {
        Connection connection = Configuration.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM "+Configuration.getUserTableSchemaNameTableName()+" WHERE staffusername='"+userID+"';");
        if (resultSet.next()){
            String password = resultSet.getString("password");
            AccessLevel accessLevel = AccessLevel.valueOf(resultSet.getString("accesslevel"));

            return new User(userID, password, accessLevel);
        }
        throw new ItemNotFoundException();
    }
}
