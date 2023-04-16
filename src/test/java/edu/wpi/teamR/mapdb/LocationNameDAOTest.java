package edu.wpi.teamR.mapdb;

import edu.wpi.teamR.Configuration;
import edu.wpi.teamR.ItemNotFoundException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class LocationNameDAOTest {

    private static LocationNameDAO locationNameDAO;
    private static Connection connection;
    @BeforeAll
    static void starterFunction() throws SQLException, ClassNotFoundException {
        Configuration.changeSchemaName("iteration2test");
        connection = Configuration.getConnection();
        locationNameDAO = new LocationNameDAO(connection);
    }
    @BeforeEach
    void deleteOldData() throws SQLException {
        locationNameDAO.deleteAllLocationNames();
    }

    @AfterAll
    static void clearDataDeleteConnection() throws SQLException {
        locationNameDAO.deleteAllLocationNames();
    }
    @Test
    void getLocations() throws SQLException {
        ArrayList<LocationName> locationNames;
        LocationName locationName;

        locationNames = locationNameDAO.getLocations();
        assertEquals(locationNames.size(), 0);

        locationNameDAO.addLocationName("Location1", "l1", "node");

        locationNames = locationNameDAO.getLocations();
        assertEquals(locationNames.size(), 1);
        locationName = locationNames.get(0);
        assertEquals(locationName.getLongName(), "Location1");
        assertEquals(locationName.getShortName(), "l1");
        assertEquals(locationName.getNodeType(), "node");

        locationNameDAO.addLocationName("Location2", "l2", "node");

        locationNames = locationNameDAO.getLocations();
        assertEquals(locationNames.size(), 2);

        locationNameDAO.addLocationName("Location3", "l2", "node");

        locationNames = locationNameDAO.getLocations();
        assertEquals(locationNames.size(), 3);
    }

    @Test
    void getLocationsByNodeType() throws SQLException {
        ArrayList<LocationName> locationNames;
        LocationName locationName;

        locationNames = locationNameDAO.getLocationsByNodeType("test");
        assertEquals(locationNames.size(), 0);

        locationNameDAO.addLocationName("Location1", "l1", "node");
        locationNameDAO.addLocationName("Location2", "l2", "node");
        locationNameDAO.addLocationName("Location3", "l3", "diff");

        locationNames = locationNameDAO.getLocationsByNodeType("node");
        assertEquals(locationNames.size(), 2);

        locationNames = locationNameDAO.getLocationsByNodeType("diff");
        assertEquals(locationNames.size(), 1);

        locationNames = locationNameDAO.getLocationsByNodeType("test");
        assertEquals(locationNames.size(), 0);
    }

    @Test
    void getLocationByLongName() throws SQLException, ItemNotFoundException {
        LocationName locationName;

        //locationName = locationNameDAO.getLocationByLongName("test"); //TODO: ERROR CASE FOR MISSED SELECT

        locationNameDAO.addLocationName("Location1", "l1", "node");
        locationNameDAO.addLocationName("Location2", "l2", "node");
        locationNameDAO.addLocationName("Location3", "l3", "diff");

        locationName = locationNameDAO.getLocationByLongName("Location1");
        assertEquals(locationName.getLongName(), "Location1");
        assertEquals(locationName.getShortName(), "l1");
        assertEquals(locationName.getNodeType(), "node");

        locationName = locationNameDAO.getLocationByLongName("Location2");
        assertEquals(locationName.getLongName(), "Location2");
        assertEquals(locationName.getShortName(), "l2");
        assertEquals(locationName.getNodeType(), "node");

        locationName = locationNameDAO.getLocationByLongName("Location3");
        assertEquals(locationName.getLongName(), "Location3");
        assertEquals(locationName.getShortName(), "l3");
        assertEquals(locationName.getNodeType(), "diff");

        //locationName = locationNameDAO.getLocationByLongName("test");
    }

    @Test
    void modifyLocationNameType() throws SQLException, ItemNotFoundException {
        LocationName locationName;

        locationNameDAO.addLocationName("Location1", "l1", "node");
        locationNameDAO.addLocationName("Location2", "l2", "node");
        locationNameDAO.addLocationName("Location3", "l3", "diff");

        locationName = locationNameDAO.getLocationByLongName("Location1");
        assertEquals(locationName.getLongName(), "Location1");
        assertEquals(locationName.getShortName(), "l1");
        assertEquals(locationName.getNodeType(), "node");

        locationNameDAO.modifyLocationNameType("Location1", "test");

        locationName = locationNameDAO.getLocationByLongName("Location1");
        assertEquals(locationName.getLongName(), "Location1");
        assertEquals(locationName.getShortName(), "l1");
        assertEquals(locationName.getNodeType(), "test");
    }

    @Test
    void modifyLocationNameShortName() throws SQLException, ItemNotFoundException {
        LocationName locationName;

        locationNameDAO.addLocationName("Location1", "l1", "node");
        locationNameDAO.addLocationName("Location2", "l2", "node");
        locationNameDAO.addLocationName("Location3", "l3", "diff");

        locationName = locationNameDAO.getLocationByLongName("Location1");
        assertEquals(locationName.getLongName(), "Location1");
        assertEquals(locationName.getShortName(), "l1");
        assertEquals(locationName.getNodeType(), "node");

        locationNameDAO.modifyLocationNameShortName("Location1", "testShortName");

        locationName = locationNameDAO.getLocationByLongName("Location1");
        assertEquals(locationName.getLongName(), "Location1");
        assertEquals(locationName.getShortName(), "testShortName");
        assertEquals(locationName.getNodeType(), "node");
    }
}