package org.vivoweb.vivo.selenium.tests;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import static org.vivoweb.vivo.selenium.VIVOAppTester.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DeleteOrganization {
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
    public void deletePrimateCollegeOfAmerica() {
        vivoDeleteIndividual("Organization", "Primate College of America");
    }

    @Test
    public void deletePrimateCollegeOfNewYork() {
        vivoDeleteIndividual("Organization", "Primate College of New York");
    }

    @Test
    public void deletePrimateCollegesOfTheWorld() {
        vivoDeleteIndividual("Organization", "Primate Colleges of the World");
    }

    @Test
    public void deletePrimateHistoryLibrary() {
        vivoDeleteIndividual("Organization", "Primate History Library");
    }

    @Test
    public void deletePrimateResearchLaboratory() {
        vivoDeleteIndividual("Organization", "Primate Research Laboratory");
    }

    @Test
    public void deletePrimateUniversityOfAmerica() {
        vivoDeleteIndividual("Organization", "Primate University of America");
    }

    @Test
    public void deletePrimatesRUs() {
        vivoDeleteIndividual("Organization", "Primates-r-us");
    }

    @Test
    public void deletePollyPerson() {
        vivoDeleteIndividual("Person", "Person, Polly");
    }
}
