package org.vivoweb.vivo.selenium.tests;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import static org.vivoweb.vivo.selenium.VIVOAppTester.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DeleteActivities {
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
    public void deleteProjectHumanApeBrainComparison() {
        vivoDeleteIndividual("Project", "Human and Ape Brain Comparison");
    }

    @Test
    public void deleteServiceGorillaMovingCompany() {
        vivoDeleteIndividual("Service", "Gorilla Moving Company");
    }

    @Test
    public void deleteServicePrimateHeartHealth() {
        vivoDeleteIndividual("Service", "Primate Heart Health");
    }
}
