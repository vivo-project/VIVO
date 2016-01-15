package org.vivoweb.vivo.selenium.tests;

import org.junit.Test;
import org.openqa.selenium.By;

public class DeleteCourses extends AbstractVIVOSeleniumTest {
    @Test
    public void deleteCourses() {
        deleteAllVisibleCookies();

        open("/");
        assertTitle("VIVO");

        vivoLogIn("testAdmin@cornell.edu", "Password");

    /* From CreateCourses */
        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Course"));
        assertTitle("Course");

        clickAndWait(By.linkText("Introduction to Primate Health"));
        assertTitle("Introduction to Primate Health");

        vivoDeleteIndividual();
    /*  */

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Course"));
        assertTitle("Course");

        clickAndWait(By.linkText("Introduction to Primates"));
        assertTitle("Introduction to Primates");

        vivoDeleteIndividual();

        vivoLogOut();
    }
}
