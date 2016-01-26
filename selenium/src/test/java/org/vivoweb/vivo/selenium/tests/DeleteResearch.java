package org.vivoweb.vivo.selenium.tests;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import static org.vivoweb.vivo.selenium.VIVOAppTester.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DeleteResearch {
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
    public void deleteArticlePrimateHappenings() {
        vivoDeleteIndividual("Article", "Primate Happenings");
    }

    @Test
    public void deleteAwardBestPrimateCollege() {
        vivoDeleteIndividual("Award or Honor", "Best Primate College");
    }

    @Test
    public void deleteAwardPrimateStudentOfTheYear() {
        vivoDeleteIndividual("Award or Honor", "Primate Student of the Year");
    }

    @Test
    public void deleteBookPHCProceedings() {
        // From CreateEvent

        vivoDeleteIndividual("Book", "PHC Proceedings");
    }

    @Test
    public void deleteConceptApeHealth() {
        // From CreateTopic

        vivoDeleteIndividual("Concept", "Ape Health");
    }

    @Test
    public void deleteConceptPrimateDiet() {
        // From CreateTopic

        vivoDeleteIndividual("Concept", "Primate Diet");
    }

    @Test
    public void deleteConceptPrimateHealth() {
        // From CreateTopic

        vivoDeleteIndividual("Concept", "Primate Health");
    }

    @Test
    public void deleteConceptElderlyCare() {
        // From CreateActivity

        vivoDeleteIndividual("Concept", "Elderly Care");
    }

    @Test
    public void deleteConceptAnimalHealth() {
        // From CreateCourses

        vivoDeleteIndividual("Concept", "Animal Health");
    }

    @Test
    public void deleteDatabasePrimateInfo() {
        vivoDeleteIndividual("Database", "Primate Info");
    }

    @Test
    public void deleteGrantPrimateElderlyCare() {
        // From CreateActivity

        vivoDeleteIndividual("Grant", "Primate Elderly Care");
    }

    @Test
    public void deleteGrantPrimateHabitatResearchGrant() {
        vivoDeleteIndividual("Grant", "Primate Habitat Research Grant");
    }

    @Test
    public void deleteGrantPrimateSurvivalPlanningGrant() {
        vivoDeleteIndividual("Grant", "Primate Survival Planning Grant");
    }

    @Test
    public void deletePatentUSA222333444555() {
        vivoDeleteIndividual("Patent", "USA222333444555");
    }

    @Test
    public void deleteWebPagePrimateHealthIntro() {
        // From CreateCourses

        vivoDeleteIndividual("Webpage", "http://primatehealthintro.cornell.edu");
    }
}
