package org.vivoweb.vivo.selenium.tests;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import static org.vivoweb.vivo.selenium.VIVOAppTester.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CheckBrowseOptions {
    @BeforeClass
    public static void setUp() {
        startTests();
        vivoLogOut();
    }

    @AfterClass
    public static void tearDown() {
        endTests();
    }

    @Test
    public void checkBrowseOptions() {
        verifyElementPresent(By.linkText("Books"));
        verifyElementPresent(By.linkText("Grants"));

        verifyTextPresent(
                "1  Books",
                "3  Grants"
        );

        clickAndWait(By.linkText("View all ..."));
        assertTitle("Research");

        verifyElementPresent(By.linkText("Article (1)"));
        verifyElementPresent(By.linkText("Award or Honor (2)"));
        verifyElementPresent(By.linkText("Blog Posting (1)"));
        verifyElementPresent(By.linkText("Book (1)"));
        verifyElementPresent(By.linkText("Concept (7)"));
        verifyElementPresent(By.linkText("Database (1)"));
        verifyElementPresent(By.linkText("Grant (3)"));
        verifyElementPresent(By.linkText("Human Study (1)"));
        verifyElementPresent(By.linkText("Patent (1)"));
        verifyElementPresent(By.linkText("Proceedings (1)"));
        verifyElementPresent(By.linkText("Webpage (1)"));

        clickAndWait(By.linkText("Home"));
        assertTitle("VIVO");

        verifyTextPresent(
                "No faculty members found.",
                "No academic departments found."
        );

        clickAndWait(By.linkText("People"));
        assertTitle("People");

        verifyElementPresent(By.linkText("Person (1)"));
        verifyElementPresent(By.linkText("Person, Polly"));

        clickAndWait(By.linkText("Organizations"));
        assertTitle("Organizations");

        verifyElementPresent(By.linkText("College (2)"));
        verifyElementPresent(By.linkText("Company (1)"));
        verifyElementPresent(By.linkText("Consortium (1)"));
        verifyElementPresent(By.linkText("Laboratory (1)"));
        verifyElementPresent(By.linkText("Library (1)"));
        verifyElementPresent(By.linkText("Organization (7)"));
        verifyElementPresent(By.linkText("University (1)"));

        clickAndWait(By.xpath("//li[@id='college']/a"));
        pause(500);

        verifyElementPresent(By.linkText("Primate College of New York"));
        verifyElementPresent(By.linkText("Primate College of America"));

        clickAndWait(By.linkText("Company (1)"));
        pause(500);

        verifyElementPresent(By.linkText("Primates-r-us"));

        clickAndWait(By.xpath("//li[@id='consortium']/a"));
        pause(500);

        verifyElementPresent(By.linkText("Primate Colleges of the World"));

        clickAndWait(By.xpath("//li[@id='laboratory']/a"));
        pause(500);

        verifyElementPresent(By.linkText("Primate Research Laboratory"));

        clickAndWait(By.xpath("//li[@id='library']/a"));
        pause(500);

        verifyElementPresent(By.linkText("Primate History Library"));

        clickAndWait(By.xpath("//li[@id='organization']/a"));
        pause(500);

        verifyElementPresent(By.linkText("Primate College of America"));
        verifyElementPresent(By.linkText("Primate College of New York"));
        verifyElementPresent(By.linkText("Primate Colleges of the World"));
        verifyElementPresent(By.linkText("Primate History Library"));
        verifyElementPresent(By.linkText("Primate Research Laboratory"));
        verifyElementPresent(By.linkText("Primate University of America"));
        verifyElementPresent(By.linkText("Primates-r-us"));

        clickAndWait(By.xpath("//li[@id='organization']/a"));
        pause(500);
        clickAndWait(By.linkText("P"));
        pause(500);

        verifyElementPresent(By.linkText("Primate College of America"));
        verifyElementPresent(By.linkText("Primate College of New York"));
        verifyElementPresent(By.linkText("Primate Colleges of the World"));
        verifyElementPresent(By.linkText("Primate History Library"));
        verifyElementPresent(By.linkText("Primate Research Laboratory"));
        verifyElementPresent(By.linkText("Primate University of America"));
        verifyElementPresent(By.linkText("Primates-r-us"));

        clickAndWait(By.linkText("University (1)"));
        pause(500);
        verifyElementPresent(By.linkText("Primate University of America"));

        clickAndWait(By.linkText("Research"));
        assertTitle("Research");

        verifyElementPresent(By.linkText("Article (1)"));
        verifyElementPresent(By.linkText("Award or Honor (2)"));
        verifyElementPresent(By.linkText("Blog Posting (1)"));
        verifyElementPresent(By.linkText("Book (1)"));
        verifyElementPresent(By.linkText("Concept (7)"));
        verifyElementPresent(By.linkText("Database (1)"));
        verifyElementPresent(By.linkText("Grant (3)"));
        verifyElementPresent(By.linkText("Human Study (1)"));
        verifyElementPresent(By.linkText("Patent (1)"));
        verifyElementPresent(By.linkText("Proceedings (1)"));
        verifyElementPresent(By.linkText("Webpage (1)"));

        clickAndWait(By.linkText("Article (1)"));
        pause(500);
        verifyElementPresent(By.linkText("Primate Happenings"));

        clickAndWait(By.linkText("Award or Honor (2)"));
        pause(500);
        verifyElementPresent(By.linkText("Best Primate College"));
        verifyElementPresent(By.linkText("Primate Student of the Year"));

        clickAndWait(By.xpath("//li[@id='book']/a"));
        pause(500);
        verifyElementPresent(By.linkText("PHC Proceedings"));

        clickAndWait(By.xpath("//li[@id='concept']/a"));
        pause(500);

        verifyElementPresent(By.linkText("Animal Health"));
        verifyElementPresent(By.linkText("Ape Health"));
        verifyElementPresent(By.linkText("Best Primate College"));
        verifyElementPresent(By.linkText("Elderly Care"));
        verifyElementPresent(By.linkText("Primate Diet"));
        verifyElementPresent(By.linkText("Primate Health"));
        verifyElementPresent(By.linkText("Primate Student of the Year"));

        clickAndWait(By.xpath("//li[@id='concept']/a"));
        pause(500);
        clickAndWait(By.linkText("P"));
        pause(500);

        verifyElementPresent(By.linkText("Primate Diet"));
        verifyElementPresent(By.linkText("Primate Health"));
        verifyElementPresent(By.linkText("Primate Student of the Year"));

        clickAndWait(By.xpath("//li[@id='database']/a"));
        pause(500);

        verifyElementPresent(By.linkText("Primate Info"));

        clickAndWait(By.xpath("//li[@id='grant']/a"));
        pause(500);

        verifyElementPresent(By.linkText("Primate Elderly Care"));
        verifyElementPresent(By.linkText("Primate Habitat Research Grant"));
        verifyElementPresent(By.linkText("Primate Survival Planning Grant"));

        clickAndWait(By.linkText("Human Study (1)"));
        pause(500);

        verifyElementPresent(By.linkText("Human and Ape Brain Comparison"));

        clickAndWait(By.linkText("Patent (1)"));
        pause(500);

        verifyElementPresent(By.linkText("USA222333444555"));

        clickAndWait(By.xpath("//li[@id='proceedings']/a"));
        pause(500);

        verifyElementPresent(By.linkText("PHC Proceedings"));

        clickAndWait(By.xpath("//li[@id='webpage']/a"));
        pause(500);

        verifyElementPresent(By.linkText("http://primatehealthintro.cornell.edu"));

        clickAndWait(By.linkText("Events"));
        assertTitle("Events");

        verifyElementPresent(By.linkText("Conference (1)"));
        verifyElementPresent(By.linkText("Event (7)"));
        verifyElementPresent(By.linkText("Invited Talk (1)"));
        verifyElementPresent(By.linkText("Performance (1)"));
        verifyElementPresent(By.linkText("Presentation (1)"));
        verifyElementPresent(By.linkText("Seminar Series (1)"));
        verifyElementPresent(By.linkText("Workshop (1)"));

        clickAndWait(By.xpath("//li[@id='conference']/a"));
        pause(500);

        verifyElementPresent(By.linkText("Primate Health Conference"));

        clickAndWait(By.xpath("//li[@id='event']/a"));
        pause(500);

        verifyElementPresent(By.linkText("Introduction to Primate Health"));
        verifyElementPresent(By.linkText("Introduction to Primates"));
        verifyElementPresent(By.linkText("New Primate Students"));
        verifyElementPresent(By.linkText("Primate Health and Fitness"));
        verifyElementPresent(By.linkText("Primate Health Check"));
        verifyElementPresent(By.linkText("Primate Health Conference"));
        verifyElementPresent(By.linkText("Primates in the Wild"));

        clickAndWait(By.xpath("//li[@id='invitedTalk']/a"));
        pause(500);

        verifyElementPresent(By.linkText("Primate Health and Fitness"));

        clickAndWait(By.linkText("Performance (1)"));
        pause(500);

        verifyElementPresent(By.linkText("Primates in the Wild"));

        clickAndWait(By.linkText("Presentation (1)"));
        pause(500);

        verifyElementPresent(By.linkText("Primate Health and Fitness"));

        clickAndWait(By.linkText("Seminar Series (1)"));
        pause(500);

        verifyElementPresent(By.linkText("Primate Health Talks"));

        clickAndWait(By.linkText("Workshop (1)"));
        pause(500);

        verifyElementPresent(By.linkText("New Primate Students"));

        clickAndWait(By.linkText("Home"));
        assertTitle("VIVO");
    }
}
