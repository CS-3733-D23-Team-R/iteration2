package edu.wpi.teamR.mapdb;

import edu.wpi.teamR.csv.CSVReadable;
import edu.wpi.teamR.csv.CSVWritable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PACKAGE)
public class Node implements MapData, CSVReadable, CSVWritable {
    private int nodeID;
    private int xCoord;
    private int yCoord;
    private String floorNum, building;

    public Node(int nodeID, int xCoord, int yCoord, String floorNum, String building){
        this.nodeID = nodeID;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.floorNum = floorNum;
        this.building = building;
    }

    private Node(String[] args) throws IndexOutOfBoundsException {
        this(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]), args[3], args[4]);
    }

    @Override
    public String toCSVEntry() {
        return nodeID + "," + xCoord + "," + yCoord + "," + floorNum + "," + building;
    }

    @Override
    public String getCSVColumns() {
        return "nodeID,xcoord,ycoord,floor,building";
    }

    @Override
    public MapDataType getDataType() {
        return MapDataType.NODE;
    }
}
