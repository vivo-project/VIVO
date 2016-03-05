package org.vivoweb.vivo.selenium.tests;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import static org.vivoweb.vivo.selenium.VIVOAppTester.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DeleteLocations {
    @BeforeClass
    public static void setUp() {
        startTests();
        vivoLogIn("testAdmin@cornell.edu", "Password");
    }

    @AfterClass
    public static void tearDown() {
        vivoLogOut();
        endTests();
    }

    @Test
    public void deleteBuildingPrimateMemorialBuilding() {
        // from CreateCourses
        vivoDeleteIndividual("Building", "Primate Memorial Building");
    }

    @Test
    public void deleteFacilityPrimateResearchLabRoom123() {
        // from CreateEquipment
        vivoDeleteIndividual("Facility", "Primate Research Lab Room 123");
    }

    @Test
    public void deleteFacilityStateFairPark() {
        // from CreateEvent
        vivoDeleteIndividual("Facility", "State Fair Park");
    }

    @Test
    public void deleteBuildingJaneMemorialBuilding() {
        // from CreateLocation
        vivoDeleteIndividual("Building", "Jane Memorial Building");
    }

    @Test
    public void deleteFacilityLabAdminOffice() {
        // from CreateLocation
        vivoDeleteIndividual("Facility", "Lab Admin Office");
    }

    @Test
    public void deleteGeograpihcPrimateQuad() {
        // from CreateLocation
        vivoDeleteIndividual("Geographic Location", "Primate Quad");
    }
}
