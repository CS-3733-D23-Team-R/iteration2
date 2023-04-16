package edu.wpi.teamR.requestdb;

import edu.wpi.teamR.Configuration;
import edu.wpi.teamR.ItemNotFoundException;
import edu.wpi.teamR.mapdb.Node;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;

public class ItemRequestDAO {
    public  ItemRequestDAO(){}
    ItemRequest addItemRequest(RequestType requestType, RequestStatus requestStatus, String longname, String staffUsername, String itemType, String requesterName, String additionalNotes, Timestamp requestDate) throws SQLException, ClassNotFoundException {
        Connection connection = Configuration.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO "+ Configuration.getServiceRequestSchemaNameTableName()+"(requestType,requestStatus,longname,staffUsername,itemType,requesterName,additionalNotes,requestDate) VALUES (?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1, requestType.toString());
        preparedStatement.setString(2, requestStatus.toString());
        preparedStatement.setString(3, longname);
        preparedStatement.setString(4, staffUsername);
        preparedStatement.setString(5, itemType);
        preparedStatement.setString(6, requesterName);
        preparedStatement.setString(7, additionalNotes);
        preparedStatement.setTimestamp(8, requestDate);
        preparedStatement.executeUpdate();
        ResultSet resultSet = preparedStatement.getGeneratedKeys();
        int requestID = 0;
        if (resultSet.next())
            requestID = resultSet.getInt("nodeID");
        return new ItemRequest(requestID, requestType, requestStatus, longname, staffUsername, itemType, requesterName, additionalNotes, requestDate);
    }

    void deleteItemRequest(int requestID) throws SQLException, ClassNotFoundException, ItemNotFoundException {
        Connection connection = Configuration.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM "+Configuration.getServiceRequestSchemaNameTableName()+" WHERE requestID=?;");
        preparedStatement.setInt(1, requestID);
        int numRows = preparedStatement.executeUpdate();
        if (numRows==0)
            throw new ItemNotFoundException();
    }
    void deleteAllItemRequests() throws SQLException, ClassNotFoundException {
        Connection connection = Configuration.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM "+Configuration.getServiceRequestSchemaNameTableName()+";");
        preparedStatement.executeUpdate();
    }
    ArrayList<ItemRequest> getItemRequests() throws SQLException, ClassNotFoundException {
        Connection connection = Configuration.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM "+Configuration.getServiceRequestSchemaNameTableName()+";");
        return getItemRequests(preparedStatement);
    }
    ArrayList<ItemRequest> getItemRequestByAttributes(SearchList searchList) throws SQLException, ClassNotFoundException {
        //separate the query items into the filter and order requirements because the
        // filter requirements need to be applied first
        ArrayList<Triple<RequestAttribute, Operation, Object>> fullRequirements = searchList.getSearchRequirements();
        ArrayList<Triple<RequestAttribute, Operation, Object>> filterRequirements = new ArrayList<>();
        ArrayList<Triple<RequestAttribute, Operation, Object>> orderRequirements = new ArrayList<>();

        //Split different operation into different lists
        for (Triple<RequestAttribute, Operation, Object> triple : fullRequirements){
            Operation operation = triple.getMiddle();
            boolean isFilter = operation==Operation.lessThan || operation==Operation.equalTo || operation==Operation.greaterThan;
            if (isFilter)
                filterRequirements.add(triple);
            else
                orderRequirements.add(triple);
        }

        StringBuilder stringBuilder = new StringBuilder("SELECT * FROM "+Configuration.getServiceRequestSchemaNameTableName()+" WHERE 1=1 ");

        //Add filter segment of statement
        for (Triple<RequestAttribute, Operation, Object> triple : filterRequirements){
            String operationString = "";

            switch (triple.getMiddle()){
                case lessThan -> operationString = "<";
                case equalTo -> operationString = "=";
                case greaterThan -> operationString = ">";
            }

            stringBuilder.append("AND "+triple.getLeft().toString()+operationString+"? ");
        }

        //Add order by segment of statement
        if (orderRequirements.size() > 0)
            stringBuilder.append("ORDER BY "); //if there is any order by clause

        for (Triple<RequestAttribute, Operation, Object> triple : orderRequirements){
            // Object will be null for these
            String orderDirectionString = "";

            switch (triple.getMiddle()){
                case orderByDesc -> orderDirectionString = " desc";
                case orderByAsc -> orderDirectionString = " asc";
            }

            stringBuilder.append(triple.getLeft().toString()+orderDirectionString+",");
        }
        stringBuilder.deleteCharAt(stringBuilder.length()-1); //remove the last ,
        stringBuilder.append(";");

        PreparedStatement preparedStatement = Configuration.getConnection().prepareStatement(stringBuilder.toString());

        //Insert the values into the statement
        for (int j = 0; j < filterRequirements.size(); j++){
            Object compareValue = filterRequirements.get(j).getRight();

            if (compareValue instanceof Integer)
                preparedStatement.setInt(j, (Integer) compareValue);
            if (compareValue instanceof RequestType)
                preparedStatement.setString(j, ((RequestType) compareValue).toString());
            if (compareValue instanceof RequestStatus)
                preparedStatement.setString(j, ((RequestStatus) compareValue).toString());
            if (compareValue instanceof String)
                preparedStatement.setString(j, (String) compareValue);
            if (compareValue instanceof Timestamp)
                preparedStatement.setTimestamp(j, (Timestamp) compareValue);
        }

        return getItemRequests(preparedStatement);
    }

    @NotNull
    private ArrayList<ItemRequest> getItemRequests(PreparedStatement preparedStatement) throws SQLException {
        ResultSet resultSet = preparedStatement.executeQuery();
        ArrayList<ItemRequest> itemRequests = new ArrayList<>();
        while (resultSet.next()){
            int requestID = resultSet.getInt("requestID");
            RequestType requestType = RequestType.valueOf(resultSet.getString("requestType"));
            RequestStatus requestStatus = RequestStatus.valueOf(resultSet.getString("requestStatus"));
            String longname = resultSet.getString("longname");
            String staffUsername = resultSet.getString("staffUsername");
            String itemType = resultSet.getString("itemType");
            String requesterName = resultSet.getString("requesterName");
            String additionalNotes = resultSet.getString("additionalNotes");
            Timestamp requestDate = resultSet.getTimestamp("requestDate");

            itemRequests.add(new ItemRequest(requestID, requestType, requestStatus, longname, staffUsername, itemType, requesterName, additionalNotes, requestDate));
        }
        return itemRequests;
    }
}
