package org.vivoweb.vivo.selenium.tests;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import static org.vivoweb.vivo.selenium.VIVOAppTester.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DeleteEquipment {
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
    public void deleteEquipmentPortablePrimateHabitat() {
        vivoDeleteIndividual("Equipment", "Portable Primate Habitat");
    }

    @Test
    public void deleteEquipmentPrimateFeeder() {
        // From CreateEquipment
        vivoDeleteIndividual("Equipment", "Primate Feeder");
    }
}
