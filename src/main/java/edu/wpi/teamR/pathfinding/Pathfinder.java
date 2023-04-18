package edu.wpi.teamR.pathfinding;

import edu.wpi.teamR.ItemNotFoundException;
import edu.wpi.teamR.mapdb.MapDatabase;

import java.sql.SQLException;

public class Pathfinder {
    private static Algorithm currentAlgorithm = Algorithm.Astar;
    private MapDatabase mapDatabase;
    public Pathfinder(MapDatabase mapDatabase) throws ItemNotFoundException {
        this.mapDatabase = mapDatabase;
    }

    public void setAlgorithm(Algorithm newAlgorithm){
        this.currentAlgorithm = newAlgorithm;
    }

    Algorithm getCurrentAlgorithm(){return currentAlgorithm;}

    public Path findPath(int startID, int endID, boolean accessible) throws ItemNotFoundException, SQLException {
        SearchInterface search;
        switch (currentAlgorithm){
                case Astar:
                    search = new AstarSearch(this.mapDatabase);
                    break;
                case BFS:
                    search = new BFSSearch(mapDatabase);
                    break;
                case DFS:
                    search = new DFSSearch(mapDatabase);
                    break;
                default:
                    throw new ItemNotFoundException();
            }
        return search.getPath(startID, endID, accessible);
    }

}