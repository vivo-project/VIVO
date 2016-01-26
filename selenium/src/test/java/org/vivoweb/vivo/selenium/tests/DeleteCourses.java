package org.vivoweb.vivo.selenium.tests;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import static org.vivoweb.vivo.selenium.VIVOAppTester.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DeleteCourses {
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
    public void deleteCourseIntroductionPrimateHealth() {
        // From CreateCourses
        vivoDeleteIndividual("Course", "Introduction to Primate Health");
    }

    @Test
    public void deleteCourseIntroductionPrimates() {
        vivoDeleteIndividual("Course", "Introduction to Primates");
    }
}
