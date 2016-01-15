package org.vivoweb.vivo.selenium.tests;

import org.junit.Test;
import org.openqa.selenium.By;

public class DeleteEvents extends AbstractVIVOSeleniumTest {
    @Test
    public void deleteEvents() {
        deleteAllVisibleCookies();

        open("/");
        assertTitle("VIVO");

        vivoLogIn("testAdmin@cornell.edu", "Password");

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Event"));
        assertTitle("Event");

        clickAndWait(By.linkText("New Primate Students"));
        assertTitle("New Primate Students");

        vivoDeleteIndividual();

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Event"));
        assertTitle("Event");

        clickAndWait(By.linkText("Primate Health and Fitness"));
        assertTitle("Primate Health and Fitness");

        vivoDeleteIndividual();

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Event"));
        assertTitle("Event");

        clickAndWait(By.linkText("Primate Health Check"));
        assertTitle("Primate Health Check");

        vivoDeleteIndividual();

/* From CreateEvent */
        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Event"));
        assertTitle("Event");

        clickAndWait(By.linkText("Primate Health Conference"));
        assertTitle("Primate Health Conference");

        vivoDeleteIndividual();
/* */
        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Event"));
        assertTitle("Event");

        clickAndWait(By.linkText("Primates in the Wild"));
        assertTitle("Primates in the Wild");

        vivoDeleteIndividual();

    /* From CreateCourses */
        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Seminar Series"));
        assertTitle("Seminar Series");

        clickAndWait(By.linkText("Primate Health Talks"));
        assertTitle("Primate Health Talks");

        vivoDeleteIndividual();
    /* */
        // Where is Introduction to Primates??

        vivoLogOut();
    }
}
