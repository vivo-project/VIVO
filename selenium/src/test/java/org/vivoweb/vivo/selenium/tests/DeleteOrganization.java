package org.vivoweb.vivo.selenium.tests;

import org.junit.Test;
import org.openqa.selenium.By;

public class DeleteOrganization extends AbstractVIVOSeleniumTest {
    @Test
    public void deleteOrganization() {
        deleteAllVisibleCookies();

        open("/");
        assertTitle("VIVO");

        vivoLogIn("testAdmin@cornell.edu", "Password");

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Organization"));
        assertTitle("Organization");

        clickAndWait(By.linkText("Primate College of America"));
        assertTitle("Primate College of America");

        vivoDeleteIndividual();

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Organization"));
        assertTitle("Organization");

        clickAndWait(By.linkText("Primate College of New York"));
        assertTitle("Primate College of New York");

        vivoDeleteIndividual();

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Organization"));
        assertTitle("Organization");

        clickAndWait(By.linkText("Primate Colleges of the World"));
        assertTitle("Primate Colleges of the World");

        vivoDeleteIndividual();

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Organization"));
        assertTitle("Organization");

        clickAndWait(By.linkText("Primate History Library"));
        assertTitle("Primate History Library");

        vivoDeleteIndividual();

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Organization"));
        assertTitle("Organization");

        clickAndWait(By.linkText("Primate Research Laboratory"));
        assertTitle("Primate Research Laboratory");

        vivoDeleteIndividual();

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Organization"));
        assertTitle("Organization");

        clickAndWait(By.linkText("Primate University of America"));
        assertTitle("Primate University of America");

        vivoDeleteIndividual();

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Organization"));
        assertTitle("Organization");

        clickAndWait(By.linkText("Primates-r-us"));
        assertTitle("Primates-r-us");

        vivoDeleteIndividual();

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Person"));
        assertTitle("Person");

        clickAndWait(By.linkText("Person, Polly"));
        assertTitle("Person, Polly");

        vivoDeleteIndividual();

        vivoLogOut();
    }
}
