package edu.wpi.teamR.requestdb;

import edu.wpi.teamR.Configuration;
import edu.wpi.teamR.ItemNotFoundException;

import java.sql.*;
import java.util.ArrayList;

public class FurnitureRequestDAO {
    private Connection connection;

    FurnitureRequestDAO(Connection connection) {
        this.connection = connection;
    }

    FurnitureRequest addFurnitureRequest(String requesterName, String location, String staffMember, String additionalNotes, Timestamp requestDate, RequestStatus requestStatus, String furnitureType) throws SQLException {
        PreparedStatement sqlInsert = connection.prepareStatement("INSERT INTO "+ Configuration.getFurnitureRequestSchemaNameTableName()
                +"(requesterName,location,staffMember,additionalNotes,requestDate,requestStatus,furnitureType)"
                +" VALUES (?,?,?,?,?,?,?);", Statement.RETURN_GENERATED_KEYS);
        sqlInsert.setString(1, requesterName);
        sqlInsert.setString(2, location);
        sqlInsert.setString(3, staffMember);
        sqlInsert.setString(4, additionalNotes);
        sqlInsert.setTimestamp(5, requestDate);
        sqlInsert.setString(6, requestStatus.toString());
        sqlInsert.setString(7, furnitureType);

        sqlInsert.executeUpdate();
        ResultSet rs = sqlInsert.getGeneratedKeys();
        int requestID = 0;
        if (rs.next()) {
            requestID = rs.getInt("requestID");
        }
        return new FurnitureRequest(requestID, requesterName, location, staffMember, additionalNotes, requestDate, requestStatus, furnitureType);
    }

    void deleteFurnitureRequest(Integer requestID) throws SQLException{
        PreparedStatement sqlDelete = connection.prepareStatement("DELETE FROM "+ Configuration.getFurnitureRequestSchemaNameTableName() + " WHERE requestID = " + requestID +";");
        sqlDelete.executeUpdate();
    }

    ArrayList<FurnitureRequest> getFurnitureRequests() throws SQLException{
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM "+Configuration.getFurnitureRequestSchemaNameTableName()+";");
        ArrayList<FurnitureRequest> furniture = new ArrayList<FurnitureRequest>();
        while (resultSet.next()){
            int requestID = resultSet.getInt("requestID");
            String requesterName = resultSet.getString("requesterName");
            String location = resultSet.getString("location");
            String staffMember = resultSet.getString("staffMember");
            String additionalNotes = resultSet.getString("additionalNotes");
            Timestamp requestDate = resultSet.getTimestamp("requestDate");
            RequestStatus requestStatus = RequestStatus.valueOf(resultSet.getString("requestStatus")) ;
            String furnitureType = resultSet.getString("furnitureType");

            furniture.add(new FurnitureRequest(requestID, requesterName, location, staffMember, additionalNotes, requestDate, requestStatus, furnitureType));
        }
        return furniture;
    }

    FurnitureRequest getFurnitureRequestByID(Integer requestID) throws SQLException, ItemNotFoundException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM "+Configuration.getFurnitureRequestSchemaNameTableName()+" WHERE requestID="+requestID+";");
        if(!resultSet.next()){
            throw new ItemNotFoundException();
        }
        String requesterName = resultSet.getString("requesterName");
        String location = resultSet.getString("location");
        String staffMember = resultSet.getString("staffMember");
        String additionalNotes = resultSet.getString("additionalNotes");
        Timestamp requestDate = resultSet.getTimestamp("requestDate");
        RequestStatus requestStatus = RequestStatus.valueOf(resultSet.getString("requestStatus") );
        String furnitureType = resultSet.getString("furnitureType");

        return new FurnitureRequest(requestID, requesterName, location, staffMember, additionalNotes, requestDate, requestStatus, furnitureType);
    }

    ArrayList<FurnitureRequest> getFurnitureRequestsByRequesterName(String requesterName) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM "+Configuration.getFurnitureRequestSchemaNameTableName()+" WHERE requesterName='"+requesterName+"';");
        ArrayList<FurnitureRequest> furniture = new ArrayList<>();
        while (resultSet.next()) {
            int requestID = resultSet.getInt("requestID");
            String location = resultSet.getString("location");
            String staffMember = resultSet.getString("staffMember");
            String additionalNotes = resultSet.getString("additionalNotes");
            Timestamp requestDate = resultSet.getTimestamp("requestDate");
            RequestStatus requestStatus = RequestStatus.valueOf(resultSet.getString("requestStatus") );
            String furnitureType = resultSet.getString("furnitureType");

            furniture.add(new FurnitureRequest(requestID, requesterName, location, staffMember, additionalNotes, requestDate, requestStatus, furnitureType));
        }
        return furniture;
    }

    ArrayList<FurnitureRequest> getFurnitureRequestsByLocation(String location) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM "+Configuration.getFurnitureRequestSchemaNameTableName()+" WHERE location='"+location+"';");
        ArrayList<FurnitureRequest> furniture = new ArrayList<>();
        while (resultSet.next()) {
            int requestID = resultSet.getInt("requestID");
            String requesterName = resultSet.getString("requesterName");
            String staffMember = resultSet.getString("staffMember");
            String additionalNotes = resultSet.getString("additionalNotes");
            Timestamp requestDate = resultSet.getTimestamp("requestDate");
            RequestStatus requestStatus = RequestStatus.valueOf(resultSet.getString("requestStatus") );
            String furnitureType = resultSet.getString("furnitureType");

            furniture.add(new FurnitureRequest(requestID, requesterName, location, staffMember, additionalNotes, requestDate, requestStatus, furnitureType));
        }
        return furniture;
    }

    ArrayList<FurnitureRequest> getFurnitureRequestsByStaffMember(String staffMember) throws SQLException{
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM "+Configuration.getFurnitureRequestSchemaNameTableName()+" WHERE staffMember='"+staffMember+"';");
        ArrayList<FurnitureRequest> furniture = new ArrayList<>();
        while (resultSet.next()) {
            int requestID = resultSet.getInt("requestID");
            String requesterName = resultSet.getString("requesterName");
            String location = resultSet.getString("location");
            String additionalNotes = resultSet.getString("additionalNotes");
            Timestamp requestDate = resultSet.getTimestamp("requestDate");
            RequestStatus requestStatus = RequestStatus.valueOf(resultSet.getString("requestStatus") );
            String furnitureType = resultSet.getString("furnitureType");

            furniture.add(new FurnitureRequest(requestID, requesterName, location, staffMember, additionalNotes, requestDate, requestStatus, furnitureType));
        }
        return furniture;
    }

    ArrayList<FurnitureRequest> getFurnitureRequestsByFurnitureType(String furnitureType) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM "+Configuration.getFurnitureRequestSchemaNameTableName()+" WHERE furnitureType='"+furnitureType+"';");
        ArrayList<FurnitureRequest> furniture = new ArrayList<>();
        while (resultSet.next()) {
            int requestID = resultSet.getInt("requestID");
            String requesterName = resultSet.getString("requesterName");
            String location = resultSet.getString("location");
            String additionalNotes = resultSet.getString("additionalNotes");
            String staffMember = resultSet.getString("staffMember");
            Timestamp requestDate = resultSet.getTimestamp("requestDate");
            RequestStatus requestStatus = RequestStatus.valueOf(resultSet.getString("requestStatus") );


            furniture.add(new FurnitureRequest(requestID, requesterName, location, staffMember, additionalNotes, requestDate, requestStatus, furnitureType));
        }
        return furniture;
    }

    ArrayList<FurnitureRequest> getFurnitureRequestsByRequestStatus(RequestStatus requestStatus) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM "+Configuration.getFurnitureRequestSchemaNameTableName()+" WHERE requestStatus='"+requestStatus+"';");
        ArrayList<FurnitureRequest> furniture = new ArrayList<>();
        while (resultSet.next()) {
            int requestID = resultSet.getInt("requestID");
            String requesterName = resultSet.getString("requesterName");
            String location = resultSet.getString("location");
            String staffMember = resultSet.getString("staffMember");
            String additionalNotes = resultSet.getString("additionalNotes");
            Timestamp requestDate = resultSet.getTimestamp("requestDate");
            String furnitureType = resultSet.getString("furnitureType");

            furniture.add(new FurnitureRequest(requestID, requesterName, location, staffMember, additionalNotes, requestDate, requestStatus, furnitureType));
        }
        return furniture;
    }

    ArrayList<FurnitureRequest> getFurnitureRequestsAfterTime(Timestamp time) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM "+Configuration.getFurnitureRequestSchemaNameTableName()+" WHERE requestdate>'"+time+"';");
        ArrayList<FurnitureRequest> furniture = new ArrayList<>();
        while(resultSet.next()){
            int requestID = resultSet.getInt("requestID");
            String requesterName = resultSet.getString("requesterName");
            String location = resultSet.getString("location");
            String staffMember = resultSet.getString("staffMember");
            String additionalNotes = resultSet.getString("additionalNotes");
            Timestamp requestDate = resultSet.getTimestamp("requestDate");
            String furnitureType = resultSet.getString("furnitureType");
            RequestStatus requestStatus = RequestStatus.valueOf(resultSet.getString("requestStatus") );

            furniture.add(new FurnitureRequest(requestID, requesterName, location, staffMember, additionalNotes, requestDate, requestStatus, furnitureType));
        }

        return furniture;
    }

    ArrayList<FurnitureRequest> getFurnitureRequestsBeforeTime(Timestamp time) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM "+Configuration.getFurnitureRequestSchemaNameTableName()+" WHERE requestdate<'"+time+"';");
        ArrayList<FurnitureRequest> furniture = new ArrayList<>();
        while(resultSet.next()){
            int requestID = resultSet.getInt("requestID");
            String requesterName = resultSet.getString("requesterName");
            String location = resultSet.getString("location");
            String staffMember = resultSet.getString("staffMember");
            String additionalNotes = resultSet.getString("additionalNotes");
            Timestamp requestDate = resultSet.getTimestamp("requestDate");
            String furnitureType = resultSet.getString("furnitureType");
            RequestStatus requestStatus = RequestStatus.valueOf(resultSet.getString("requestStatus") );

            furniture.add(new FurnitureRequest(requestID, requesterName, location, staffMember, additionalNotes, requestDate, requestStatus, furnitureType));
        }

        return furniture;
    }

    ArrayList<FurnitureRequest> getFurnitureRequestsBetweenTimes(Timestamp firstTime, Timestamp secondTime) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM "+Configuration.getFurnitureRequestSchemaNameTableName()+" WHERE requestdate>'"+ firstTime +"' AND requestdate<'" + secondTime + "';");
        ArrayList<FurnitureRequest> furniture = new ArrayList<>();
        while(resultSet.next()){
            int requestID = resultSet.getInt("requestID");
            String requesterName = resultSet.getString("requesterName");
            String location = resultSet.getString("location");
            String staffMember = resultSet.getString("staffMember");
            String additionalNotes = resultSet.getString("additionalNotes");
            Timestamp requestDate = resultSet.getTimestamp("requestDate");
            String furnitureType = resultSet.getString("furnitureType");
            RequestStatus requestStatus = RequestStatus.valueOf(resultSet.getString("requestStatus") );

            furniture.add(new FurnitureRequest(requestID, requesterName, location, staffMember, additionalNotes, requestDate, requestStatus, furnitureType));
        }

        return furniture;
    }

    FurnitureRequest modifyFurnitureRequest(int requestID, String newRequesterName, String newLocation, String newStaffMember, String newAdditionalNotes, Timestamp newRequestDate, RequestStatus newRequestStatus, String newFurnitureType) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE " + Configuration.getFurnitureRequestSchemaNameTableName() + " SET requestername=?, location=?, staffmember=?, additionalnotes=?, requestdate=?, requeststatus=?, furnituretype=? WHERE requestID=?");
        preparedStatement.setString(1, newRequesterName);
        preparedStatement.setString(2, newLocation);
        preparedStatement.setString(3, newStaffMember);
        preparedStatement.setString(4, newAdditionalNotes);
        preparedStatement.setTimestamp(5, newRequestDate);
        preparedStatement.setString(6, newRequestStatus.toString());
        preparedStatement.setString(7, newFurnitureType);
        preparedStatement.setInt(8, requestID);
        preparedStatement.executeUpdate();
        return new FurnitureRequest(requestID, newRequesterName, newLocation, newStaffMember, newAdditionalNotes, newRequestDate, newRequestStatus, newFurnitureType);
    }
}

