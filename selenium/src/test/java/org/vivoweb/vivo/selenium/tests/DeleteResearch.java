package org.vivoweb.vivo.selenium.tests;

import org.junit.Test;
import org.openqa.selenium.By;

public class DeleteResearch extends AbstractVIVOSeleniumTest {
    @Test
    public void deleteResearch() {
        deleteAllVisibleCookies();

        open("/");
        assertTitle("VIVO");

        vivoLogIn("testAdmin@cornell.edu", "Password");

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Article"));
        assertTitle("Article");

        clickAndWait(By.linkText("Primate Happenings"));
        assertTitle("Primate Happenings");

        vivoDeleteIndividual();

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Award or Honor"));
        assertTitle("Award or Honor");

        clickAndWait(By.linkText("Best Primate College"));
        assertTitle("Best Primate College");

        vivoDeleteIndividual();

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Award or Honor"));
        assertTitle("Award or Honor");

        clickAndWait(By.linkText("Primate Student of the Year"));
        assertTitle("Primate Student of the Year");

        vivoDeleteIndividual();
/* From CreateEvent */
        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Book"));
        assertTitle("Book");

        clickAndWait(By.linkText("PHC Proceedings"));
        assertTitle("PHC Proceedings");

        vivoDeleteIndividual();
/* */
/* From CreateTopic */
        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Concept"));
        assertTitle("Concept");

        clickAndWait(By.linkText("Ape Health"));
        assertTitle("Ape Health");

        vivoDeleteIndividual();

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Concept"));
        assertTitle("Concept");

        clickAndWait(By.linkText("Primate Diet"));
        assertTitle("Primate Diet");

        vivoDeleteIndividual();

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Concept"));
        assertTitle("Concept");

        clickAndWait(By.linkText("Primate Health"));
        assertTitle("Primate Health");

        vivoDeleteIndividual();
/* */
    /* From CreateActivity */
        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Concept"));
        assertTitle("Concept");

        clickAndWait(By.linkText("Elderly Care"));
        assertTitle("Elderly Care");

        vivoDeleteIndividual();
    /* */

    /* From CreateCourses */
        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Concept"));
        assertTitle("Concept");

        clickAndWait(By.linkText("Animal Health"));
        assertTitle("Animal Health");

        vivoDeleteIndividual();
    /* */
        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Database"));
        assertTitle("Database");

        clickAndWait(By.linkText("Primate Info"));
        assertTitle("Primate Info");

        vivoDeleteIndividual();

    /* Delete grant from createActivity */
        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Grant"));
        assertTitle("Grant");

        clickAndWait(By.linkText("Primate Elderly Care"));
        assertTitle("Primate Elderly Care");

        vivoDeleteIndividual();
    /* */

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Grant"));
        assertTitle("Grant");

        clickAndWait(By.linkText("Primate Habitat Research Grant"));
        assertTitle("Primate Habitat Research Grant");

        vivoDeleteIndividual();

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Grant"));
        assertTitle("Grant");

        clickAndWait(By.linkText("Primate Survival Planning Grant"));
        assertTitle("Primate Survival Planning Grant");

        vivoDeleteIndividual();

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Patent"));
        assertTitle("Patent");

        clickAndWait(By.linkText("USA222333444555"));
        assertTitle("USA222333444555");

        vivoDeleteIndividual();

    /* From CreateCourses */
        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Webpage"));
        assertTitle("Webpage");

        clickAndWait(By.linkText("http://primatehealthintro.cornell.edu"));
        assertTitle("http://primatehealthintro.cornell.edu");

        vivoDeleteIndividual();
    /* */
        vivoLogOut();
    }
}
