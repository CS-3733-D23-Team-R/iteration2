package edu.wpi.teamR.requestdb;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter(AccessLevel.PACKAGE)
public class ItemRequest {

    private int requestID;
    private RequestType requestType;
    private RequestStatus requestStatus;
    private String longname, staffUsername, itemType, requesterName, additionalNotes;
    private Timestamp requestDate;

    public ItemRequest(int requestID, RequestType requestType, RequestStatus requestStatus, String longname, String staffUsername, String itemType, String requesterName, String additionalNotes, Timestamp requestDate){
        this.requestID=requestID;
        this.requestType = requestType;
        this.requestStatus = requestStatus;
        this.longname = longname;
        this.staffUsername = staffUsername;
        this.itemType = itemType;
        this.requesterName = requesterName;
        this.additionalNotes = additionalNotes;
        this.requestDate = requestDate;
    }

    public void setRequestStatus(RequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

    public void setStaffMember(String staffMember) {
        this.staffUsername = staffMember;
    }
}
