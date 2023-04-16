package edu.wpi.teamR.requestdb;

import java.sql.Timestamp;
import java.util.ArrayList;

public class SearchList {
    private ArrayList<Triple<RequestAttribute, Operation, Object>> searchRequirements;

    public SearchList(){
        this.searchRequirements = new ArrayList<>();
    }

    //TODO: DOCUMENT
    /**
     * Add a comparison to filter down the results of the selection statement
     *
     * @param requestAttribute any attribute
     * @param operation
     * @param compareValue
     * @throws SearchException
     */
    public void addComparison(RequestAttribute requestAttribute, Operation operation, Object compareValue) throws SearchException {
        //Check for possible mistakes
        boolean passedOperationIsOrder = operation==Operation.orderByAsc || operation==Operation.orderByDesc;
        if (passedOperationIsOrder)
            throw new SearchException("Invalid Operation for Comparison");

        for (Triple<RequestAttribute, Operation, Object> triple : searchRequirements){
            boolean listOperationIsOrder = triple.getMiddle()==Operation.orderByAsc || triple.getMiddle()==Operation.orderByDesc;
            boolean requestAttributesEqual = requestAttribute == triple.getLeft();
            if (requestAttributesEqual && !listOperationIsOrder)
                throw new SearchException("Attribute Already Exists"); //if the attribute already exists in another non-order clause

            RequestAttribute rA = requestAttribute; //make the boolean expressions shorter
            boolean shouldBeInteger = rA==RequestAttribute.requestID;
            boolean shouldBeRequestType = rA==RequestAttribute.requestType;
            boolean shouldBeRequestStatus = rA==RequestAttribute.requestStatus;
            boolean shouldBeString = rA==RequestAttribute.longname || rA==RequestAttribute.staffUsername || rA==RequestAttribute.itemType || rA==RequestAttribute.requesterName || rA==RequestAttribute.additionalNotes;
            boolean shouldBeTimestamp = rA==RequestAttribute.requestDate;
            boolean isInteger = compareValue instanceof Integer;
            boolean isRequestType = compareValue instanceof RequestType;
            boolean isRequestStatus = compareValue instanceof RequestStatus;
            boolean isString = compareValue instanceof String;
            boolean isTimestamp = compareValue instanceof Timestamp;

            if (shouldBeInteger && !isInteger)
                throw new SearchException(requestAttribute.toString()+" needs to be of type Integer but was given as type "+compareValue.getClass().toString());
            if (shouldBeRequestType && !isRequestType)
                throw new SearchException(requestAttribute.toString()+" needs to be of type RequestType but was given as type "+compareValue.getClass().toString());
            if (shouldBeRequestStatus && !isRequestStatus)
                throw new SearchException(requestAttribute.toString()+" needs to be of type RequestStatus but was given as type "+compareValue.getClass().toString());
            if (shouldBeString && !isString)
                throw new SearchException(requestAttribute.toString()+" needs to be of type String but was given as type "+compareValue.getClass().toString());
            if (shouldBeTimestamp && !isTimestamp)
                throw new SearchException(requestAttribute.toString()+" needs to be of type Timestamp but was given as type "+compareValue.getClass().toString());
        }
        //input is cleared to be added
        searchRequirements.add(new Triple<RequestAttribute, Operation, Object>(requestAttribute, operation, compareValue));
    }

    public void addOrdering(RequestAttribute requestAttribute, Operation operation) throws SearchException {
        //Check for possible mistakes
        boolean passedOperationIsOrder = operation==Operation.orderByAsc || operation==Operation.orderByDesc;
        if (!passedOperationIsOrder)
            throw new SearchException("Invalid Operation for Ordering");

        for (Triple<RequestAttribute, Operation, Object> triple : searchRequirements){
            boolean listOperationIsOrder = triple.getMiddle()==Operation.orderByAsc || triple.getMiddle()==Operation.orderByDesc;
            boolean requestAttributesEqual = requestAttribute == triple.getLeft();
            if (requestAttributesEqual && listOperationIsOrder)
                throw new SearchException("Attribute Already Exists"); //if the attribute already exists in another ORDER BY clause
        }

        //input is cleared to be added
        searchRequirements.add(new Triple<RequestAttribute, Operation, Object>(requestAttribute, operation, null)); //will ignore 3rd
    }

    ArrayList<Triple<RequestAttribute, Operation, Object>> getSearchRequirements(){
        return searchRequirements;
    }


}
