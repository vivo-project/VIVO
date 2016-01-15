package org.vivoweb.vivo.selenium.tests;

import org.junit.Test;
import org.openqa.selenium.By;

public class DeleteLocations extends AbstractVIVOSeleniumTest {
    @Test
    public void deleteLocations() {
        deleteAllVisibleCookies();

        open("/");
        assertTitle("VIVO");

        vivoLogIn("testAdmin@cornell.edu", "Password");

/* from CreateCourses */
        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        vivoDeleteIndividual("Building", "Primate Memorial Building");
/* */
/* from CreateEquipment */
        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        vivoDeleteIndividual("Facility", "Primate Research Lab Room 123");
/* */
/* from CreateEvent */
        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        vivoDeleteIndividual("Facility", "State Fair Park");
/* */
/* from CreateLocation */
        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        vivoDeleteIndividual("Building", "Jane Memorial Building");

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        vivoDeleteIndividual("Facility", "Lab Admin Office");

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        vivoDeleteIndividual("Geographic Location", "Primate Quad");
/* */
        vivoLogOut();
    }
}
/*
Primate Quad
 */