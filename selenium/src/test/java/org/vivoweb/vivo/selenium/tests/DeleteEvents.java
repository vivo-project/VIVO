package org.vivoweb.vivo.selenium.tests;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import static org.vivoweb.vivo.selenium.VIVOAppTester.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DeleteEvents {
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
    public void deleteEventNewPrimateStudents() {
        vivoDeleteIndividual("Event", "New Primate Students");
    }

    @Test
    public void deleteEventPrimateHealthAndFitness() {
        vivoDeleteIndividual("Event", "Primate Health and Fitness");
    }

    @Test
    public void deleteEventPrimateHealthCheck() {
        vivoDeleteIndividual("Event", "Primate Health Check");
    }

    @Test
    public void deleteEventPrimateHealthConference() {
        // From CreateEvent
        vivoDeleteIndividual("Event", "Primate Health Conference");
    }

    @Test
    public void deleteEventPrimatesInTheWild() {
        vivoDeleteIndividual("Event", "Primates in the Wild");
    }

    @Test
    public void deleteSeminarPrimateHealthTalks() {
        // From CreateCourses
        vivoDeleteIndividual("Seminar Series", "Primate Health Talks");
    }

    // Where is Introduction to Primates??
}
