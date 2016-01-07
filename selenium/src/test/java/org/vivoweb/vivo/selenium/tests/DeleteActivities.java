package org.vivoweb.vivo.selenium.tests;

import org.junit.Test;
import org.openqa.selenium.By;

public class DeleteActivities extends AbstractVIVOSeleniumTest {
    @Test
    public void deleteActivities() {
        deleteAllVisibleCookies();

        open("/");
        assertTitle("VIVO");

        vivoLogIn("testAdmin@cornell.edu", "Password");

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Project"));
        assertTitle("Project");

        clickAndWait(By.linkText("Human and Ape Brain Comparison"));
        assertTitle("Human and Ape Brain Comparison");

        vivoDeleteIndividual();

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Service"));
        assertTitle("Service");

        clickAndWait(By.linkText("Gorilla Moving Company"));
        assertTitle("Gorilla Moving Company");

        vivoDeleteIndividual();

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Service"));
        assertTitle("Service");

        clickAndWait(By.linkText("Primate Heart Health"));
        assertTitle("Primate Heart Health");

        vivoDeleteIndividual();

        vivoLogOut();
    }
}
