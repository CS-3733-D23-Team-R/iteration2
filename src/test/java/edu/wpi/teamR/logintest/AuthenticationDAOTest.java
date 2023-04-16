package edu.wpi.teamR.logintest;

import edu.wpi.teamR.Configuration;
import edu.wpi.teamR.login.AccessLevel;
import edu.wpi.teamR.login.AuthenticationDAO;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.*;
import static org.junit.jupiter.api.Assertions.*;

public class AuthenticationDAOTest {
    static AuthenticationDAO authDao;
    @BeforeAll
    static void startup() throws SQLException, ClassNotFoundException {
        Configuration.changeSchemaName("iteration1test");
        authDao = AuthenticationDAO.getInstance();
        authDao.deleteALLUsers();
    }
    @AfterAll
    static void end() throws SQLException, ClassNotFoundException {
        authDao.deleteALLUsers();
    }
    @Test
    void addDeleteUserTest() throws SQLException, ClassNotFoundException {
        authDao.addUser("140", "test", AccessLevel.Staff);
        Connection connection = Configuration.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultset = statement.executeQuery("SELECT * FROM "+Configuration.getUserTableSchemaNameTableName()+" WHERE username = '140' AND password = 'test';");
        resultset.next();
        assertEquals("140",resultset.getString(1));
        assertEquals("test", resultset.getString(2));
        assertEquals("Staff", resultset.getString(3));
        authDao.removeUserByID("140");
    }

    @Test
    void modifyUserAccessByIDTest() throws SQLException, ClassNotFoundException {
        authDao.addUser("777", "seven", AccessLevel.Admin);
        authDao.modifyUserAccessByID("777", AccessLevel.Staff);
        Connection connection = Configuration.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultset = statement.executeQuery("SELECT * FROM "+Configuration.getUserTableSchemaNameTableName()+" WHERE username = '777' AND password = 'seven';");
        resultset.next();
        assertEquals("777", resultset.getString(1));
        assertEquals("seven", resultset.getString(2));
        assertEquals("Staff", resultset.getString(3));
        authDao.removeUserByID("777");
    }
}
