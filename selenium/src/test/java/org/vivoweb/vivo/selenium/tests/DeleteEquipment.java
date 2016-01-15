package org.vivoweb.vivo.selenium.tests;

import org.junit.Test;
import org.openqa.selenium.By;

public class DeleteEquipment extends AbstractVIVOSeleniumTest {
    @Test
    public void deleteEquipment() {
        deleteAllVisibleCookies();

        open("/");
        assertTitle("VIVO");

        vivoLogIn("testAdmin@cornell.edu", "Password");

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Equipment"));
        assertTitle("Equipment");

        clickAndWait(By.linkText("Portable Primate Habitat"));
        assertTitle("Portable Primate Habitat");

        vivoDeleteIndividual();
/* From CreateEquipment */
        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Equipment"));
        assertTitle("Equipment");

        clickAndWait(By.linkText("Primate Feeder"));
        assertTitle("Primate Feeder");

        vivoDeleteIndividual();
/* */

        vivoLogOut();
    }
}
