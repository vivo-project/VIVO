package org.vivoweb.vivo.selenium.tests;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import static org.vivoweb.vivo.selenium.VIVOAppTester.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CheckPublicView {
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
    public void checkPublicView() {
        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Project"));
        assertTitle("Project");

        clickAndWait(By.linkText("Human and Ape Brain Comparison"));
        assertTitle("Human and Ape Brain Comparison");

        verifyElementPresent(By.linkText("Primate College of America"));

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Research Project"));
        assertTitle("Research Project");

        clickAndWait(By.linkText("Human and Ape Brain Comparison"));
        assertTitle("Human and Ape Brain Comparison");

        verifyElementPresent(By.linkText("Primate College of America"));

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Service"));
        assertTitle("Service");

        clickAndWait(By.linkText("Primate Heart Health"));
        assertTitle("Primate Heart Health");

        verifyElementPresent(By.linkText("Primate College of America"));

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Service"));
        assertTitle("Service");

        clickAndWait(By.linkText("Gorilla Moving Company"));
        assertTitle("Gorilla Moving Company");

        verifyElementPresent(By.linkText("Primate College of America"));

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Transport Service"));
        assertTitle("Transport Service");

        clickAndWait(By.linkText("Gorilla Moving Company"));
        assertTitle("Gorilla Moving Company");

        verifyElementPresent(By.linkText("Primate College of America"));

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Course"));
        assertTitle("Course");

        clickAndWait(By.linkText("Introduction to Primate Health"));
        assertTitle("Introduction to Primate Health");

        verifyElementPresent(By.linkText("Primate College of America"));

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Course"));
        assertTitle("Course");

        clickAndWait(By.linkText("Introduction to Primates"));
        assertTitle("Introduction to Primates");

        verifyElementPresent(By.linkText("Primate College of America"));

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Conference"));
        assertTitle("Conference");

        clickAndWait(By.linkText("Primate Health Conference"));
        assertTitle("Primate Health Conference");

        verifyElementPresent(By.linkText("Primate Health and Fitness"));
    	verifyElementPresent(By.linkText("Animal Health"));
	    verifyElementPresent(By.linkText("PHC Proceedings"));

        verifyTextPresent(
                "has subject area",
                "Animal Health",
                "description"
        );


        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Invited Talk"));
        assertTitle("Invited Talk");

        clickAndWait(By.linkText("Primate Health and Fitness"));
        assertTitle("Primate Health and Fitness");

        verifyElementPresent(By.linkText("Introduction to Primate Health"));

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Performance"));
        assertTitle("Performance");

        clickAndWait(By.linkText("Primates in the Wild"));
        assertTitle("Primates in the Wild");

        verifyElementPresent(By.linkText("Primate College of America"));

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Presentation"));
        assertTitle("Presentation");

        clickAndWait(By.linkText("Primate Health and Fitness"));
        assertTitle("Primate Health and Fitness");

        verifyElementPresent(By.linkText("Introduction to Primate Health"));

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Seminar Series"));
        assertTitle("Seminar Series");

        clickAndWait(By.linkText("Primate Health Talks"));
        assertTitle("Primate Health Talks");

        verifyElementPresent(By.linkText("Introduction to Primate Health"));

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Workshop"));
        assertTitle("Workshop");

        clickAndWait(By.linkText("New Primate Students"));
        assertTitle("New Primate Students");

        verifyElementPresent(By.linkText("Primate College of America"));

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("College"));
        assertTitle("College");

        clickAndWait(By.linkText("Primate College of America"));
        assertTitle("Primate College of America");

        verifyElementPresent(By.linkText("B.S. Bachelor of Science"));

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("College"));
        assertTitle("College");

        clickAndWait(By.linkText("Primate College of New York"));
        assertTitle("Primate College of New York");

        verifyElementPresent(By.linkText("Primate College of America"));

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Company"));
        assertTitle("Company");

        clickAndWait(By.linkText("Primates-r-us"));
        assertTitle("Primates-r-us");

        verifyElementPresent(By.linkText("Primate College of America"));

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Consortium"));
        assertTitle("Consortium");

        clickAndWait(By.linkText("Primate Colleges of the World"));
        assertTitle("Primate Colleges of the World");

        verifyElementPresent(By.linkText("Primate College of America"));

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Laboratory"));
        assertTitle("Laboratory");

        clickAndWait(By.linkText("Primate Research Laboratory"));
        assertTitle("Primate Research Laboratory");

        verifyElementPresent(By.linkText("Primate College of America"));

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Library"));
        assertTitle("Library");

        clickAndWait(By.linkText("Primate History Library"));
        assertTitle("Primate History Library");

        verifyElementPresent(By.linkText("Primate College of America"));

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("University"));
        assertTitle("University");

        clickAndWait(By.linkText("Primate University of America"));
        assertTitle("Primate University of America");

        verifyElementPresent(By.linkText("Jane Memorial Building"));

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Organization"));
        assertTitle("Organization");

        verifyElementPresent(By.linkText("Primate College of America"));
        verifyElementPresent(By.linkText("Primate College of New York"));
        verifyElementPresent(By.linkText("Primate Colleges of the World"));
        verifyElementPresent(By.linkText("Primate History Library"));
        verifyElementPresent(By.linkText("Primate Research Laboratory"));
        verifyElementPresent(By.linkText("Primate University of America"));
        verifyElementPresent(By.linkText("Primates-r-us"));

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Equipment"));
        assertTitle("Equipment");

        clickAndWait(By.linkText("Portable Primate Habitat"));
        assertTitle("Portable Primate Habitat");

        verifyElementPresent(By.linkText("Primate College of America"));

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Equipment"));
        assertTitle("Equipment");

        clickAndWait(By.linkText("Primate Feeder"));
        assertTitle("Primate Feeder");

        verifyElementPresent(By.linkText("Primate Research Laboratory"));

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Article"));
        assertTitle("Article");

        clickAndWait(By.linkText("Primate Happenings"));
        assertTitle("Primate Happenings");

        verifyElementPresent(By.linkText("Primate College of America"));

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Award or Honor"));
        assertTitle("Award or Honor");

        clickAndWait(By.linkText("Best Primate College"));
        assertTitle("Best Primate College");

        verifyElementPresent(By.linkText("Best Primate College (Primate College of America)"));

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Award or Honor"));
        assertTitle("Award or Honor");

        clickAndWait(By.linkText("Primate Student of the Year"));
        assertTitle("Primate Student of the Year");

        verifyElementPresent(By.linkText("Primate College of America"));

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Blog Posting"));
        assertTitle("Blog Posting");

        clickAndWait(By.linkText("Primate Happenings"));
        assertTitle("Primate Happenings");

        verifyElementPresent(By.linkText("Primate College of America"));

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Book"));
        assertTitle("Book");

        verifyElementPresent(By.linkText("PHC Proceedings"));

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Concept"));
        assertTitle("Concept");

        verifyElementPresent(By.linkText("Animal Health"));
        verifyElementPresent(By.linkText("Ape Health"));
        verifyElementPresent(By.linkText("Best Primate College"));
        verifyElementPresent(By.linkText("Elderly Care"));
        verifyElementPresent(By.linkText("Primate Diet"));
        verifyElementPresent(By.linkText("Primate Health"));
        verifyElementPresent(By.linkText("Primate Student of the Year"));

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Database"));
        assertTitle("Database");

        clickAndWait(By.linkText("Primate Info"));
        assertTitle("Primate Info");

        verifyElementPresent(By.linkText("Primate College of America"));

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Grant"));
        assertTitle("Grant");

        clickAndWait(By.linkText("Primate Elderly Care"));
        assertTitle("Primate Elderly Care");

        verifyElementPresent(By.linkText("Elderly Care"));

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Grant"));
        assertTitle("Grant");

        clickAndWait(By.linkText("Primate Habitat Research Grant"));
        assertTitle("Primate Habitat Research Grant");

        verifyElementPresent(By.linkText("Primate College of America"));

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Grant"));
        assertTitle("Grant");

        clickAndWait(By.linkText("Primate Survival Planning Grant"));
        assertTitle("Primate Survival Planning Grant");

        verifyElementPresent(By.linkText("Primate College of America"));

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Human Study"));
        assertTitle("Human Study");

        clickAndWait(By.linkText("Human and Ape Brain Comparison"));
        assertTitle("Human and Ape Brain Comparison");

        verifyElementPresent(By.linkText("Primate College of America"));

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Patent"));
        assertTitle("Patent");

        clickAndWait(By.linkText("USA222333444555"));
        assertTitle("USA222333444555");

        verifyElementPresent(By.linkText("Primate College of America"));

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Proceedings"));
        assertTitle("Proceedings");

        clickAndWait(By.linkText("PHC Proceedings"));
        assertTitle("PHC Proceedings");

        verifyElementPresent(By.linkText("Primate Health Conference"));

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Webpage"));
        assertTitle("Webpage");

        clickAndWait(By.linkText("http://primatehealthintro.cornell.edu"));
        assertTitle("http://primatehealthintro.cornell.edu");

        verifyElementPresent(By.linkText("Introduction to Primate Health"));

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Building"));
        assertTitle("Building");

        clickAndWait(By.linkText("Jane Memorial Building"));
        assertTitle("Jane Memorial Building");

        verifyElementPresent(By.linkText("Portable Primate Habitat"));

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Room"));
        assertTitle("Room");

        clickAndWait(By.linkText("Lab Admin Office"));
        assertTitle("Lab Admin Office");

        verifyElementPresent(By.linkText("Jane Memorial Building"));

        clickAndWait(By.linkText("Home"));
        assertTitle("VIVO");
    }
}
