package edu.wpi.teamR.mapdb;
import edu.wpi.teamR.Configuration;
import edu.wpi.teamR.ItemNotFoundException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DirectionArrowDAO {

    private Connection connection;
    DirectionArrowDAO(Connection connection){
        this.connection = connection;
    }

    DirectionArrow addDirectionArrow(String longname, int kioskID, Direction direction) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO "+ Configuration.getDirectionArrowSchemaNameTableName()+"(longname, kioskID, direction) VALUES(?, ?, ?);");
        preparedStatement.setString(1, longname);
        preparedStatement.setInt(2, kioskID);
        preparedStatement.setString(3, direction.toString());
        preparedStatement.executeUpdate();
        return new DirectionArrow(longname, kioskID, direction);
    }

    void deleteDirectionArrowByLongname(String longname) throws SQLException, ItemNotFoundException {
        PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM "+Configuration.getDirectionArrowSchemaNameTableName()+" WHERE longname=?;");
        preparedStatement.setString(1, longname);
        int rows = preparedStatement.executeUpdate();
        if (rows == 0)
            throw new ItemNotFoundException();
    }

    void deleteDirectionArrowsByKiosk(int kioskID) throws SQLException, ItemNotFoundException {
        PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM "+Configuration.getDirectionArrowSchemaNameTableName()+" WHERE kioskID=?;");
        preparedStatement.setInt(1, kioskID);
        int rows = preparedStatement.executeUpdate();
        if (rows == 0)
            throw new ItemNotFoundException();
    }

    void deleteAllDirectionArrows() throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM "+Configuration.getDirectionArrowSchemaNameTableName()+";");
        preparedStatement.executeUpdate();
    }

    ArrayList<DirectionArrow> getDirectionArrows() throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM "+Configuration.getDirectionArrowSchemaNameTableName()+";");
        ResultSet resultSet = preparedStatement.executeQuery();
        ArrayList<DirectionArrow> directionArrows = new ArrayList<>();
        while (resultSet.next()){
            String longname = resultSet.getString("longname");
            int kioskID = resultSet.getInt("kioskID");
            Direction direction = Direction.valueOf(resultSet.getString("direction"));
            directionArrows.add(new DirectionArrow(longname, kioskID, direction));
        }
        return directionArrows;
    }

    ArrayList<DirectionArrow> getDirectionArrowsByKiosk(int kioskID) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM "+Configuration.getDirectionArrowSchemaNameTableName()+" WHERE kioskID=?;");
        preparedStatement.setInt(1, kioskID);
        ResultSet resultSet = preparedStatement.executeQuery();
        ArrayList<DirectionArrow> directionArrows = new ArrayList<>();
        while (resultSet.next()){
            String longname = resultSet.getString("longname");
            Direction direction = Direction.valueOf(resultSet.getString("direction"));
            directionArrows.add(new DirectionArrow(longname, kioskID, direction));
        }
        return directionArrows;
    }
}
