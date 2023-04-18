package edu.wpi.teamR.pathfinding;

import edu.wpi.teamR.ItemNotFoundException;
import edu.wpi.teamR.mapdb.MapDatabase;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class PathfinderTest {

    @Test
    void setAlgorithm() throws ItemNotFoundException, SQLException, ClassNotFoundException {
        Pathfinder makePather = new Pathfinder(new MapDatabase());
        makePather.setAlgorithm(Algorithm.BFS);
        assertEquals(makePather.getCurrentAlgorithm(), Algorithm.BFS);
    }
}