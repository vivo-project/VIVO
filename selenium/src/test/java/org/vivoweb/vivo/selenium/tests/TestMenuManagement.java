package org.vivoweb.vivo.selenium.tests;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import static org.vivoweb.vivo.selenium.VIVOAppTester.*;

public class TestMenuManagement {
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
    public void testMenuManagement() {
        clickAndWait(By.linkText("Site Admin"));
        assertTitle("VIVO Site Administration");

        clickAndWait(By.linkText("Page management"));
        assertTitle("Pages");

        clickAndWait(By.id("submit"));
        assertTitle("Edit");

        type(By.id("pageName"), "Activities");
        type(By.name("prettyUrl"), "/activities");
        selectByLabel(By.id("typeSelect"), "Browse Class Group");
        selectByLabel(By.id("selectClassGroup"), "activities");

//        clickAndWait(By.cssSelector("option[value=\"http://vivoweb.org/ontology#vitroClassGroupactivities\"]"));
        clickAndWait(By.id("doneWithContent"));
        clickAndWait(By.id("menuCheckbox"));

        clickAndWait(By.id("pageSave"));
        assertTitle("Pages");

        verifyTextPresent(
                "Home",
                "People",
                "Organizations",
                "Research",
                "Events",
                "Activities"
        );

        clickAndWait(By.id("submit"));
        assertTitle("Edit");

        type(By.id("pageName"), "Courses");
        type(By.name("prettyUrl"), "/courses");
        selectByLabel(By.id("typeSelect"), "Browse Class Group");
        selectByLabel(By.id("selectClassGroup"), "courses");

//        clickAndWait(By.cssSelector("option[value=\"http://vivoweb.org/ontology#vitroClassGroupactivities\"]"));
        clickAndWait(By.id("doneWithContent"));
        clickAndWait(By.id("menuCheckbox"));

        clickAndWait(By.id("pageSave"));
        assertTitle("Pages");

        verifyTextPresent(
                "Home",
                "People",
                "Organizations",
                "Research",
                "Events",
                "Activities",
                "Courses"
        );

        clickAndWait(By.id("submit"));
        assertTitle("Edit");

        type(By.id("pageName"), "Equipment");
        type(By.name("prettyUrl"), "/equipment");
        selectByLabel(By.id("typeSelect"), "Browse Class Group");
        selectByLabel(By.id("selectClassGroup"), "equipment");

//        clickAndWait(By.cssSelector("option[value=\"http://vivoweb.org/ontology#vitroClassGroupactivities\"]"));
        clickAndWait(By.id("doneWithContent"));
        clickAndWait(By.id("menuCheckbox"));

        clickAndWait(By.id("pageSave"));
        assertTitle("Pages");

        verifyTextPresent(
                "Home",
                "People",
                "Organizations",
                "Research",
                "Events",
                "Activities"
        );

        clickAndWait(By.id("submit"));
        assertTitle("Edit");

        verifyTextPresent(
                "Home",
                "People",
                "Organizations",
                "Research",
                "Events",
                "Activities",
                "Courses",
                "Equipment"
        );

        type(By.id("pageName"), "Locations");
        type(By.name("prettyUrl"), "/locations");
        selectByLabel(By.id("typeSelect"), "Browse Class Group");
        selectByLabel(By.id("selectClassGroup"), "locations");

//        clickAndWait(By.cssSelector("option[value=\"http://vivoweb.org/ontology#vitroClassGroupactivities\"]"));
        clickAndWait(By.id("doneWithContent"));
        clickAndWait(By.id("menuCheckbox"));

        clickAndWait(By.id("pageSave"));
        assertTitle("Pages");

        verifyTextPresent(
                "Home",
                "People",
                "Organizations",
                "Research",
                "Events",
                "Activities",
                "Locations"
        );

        clickAndWait(By.linkText("Activities"));
        assertTitle("Activities");

        clickAndWait(By.linkText("Project (1)"));
        pause(500);

        verifyElementPresent(By.linkText("Human and Ape Brain Comparison"));

        clickAndWait(By.linkText("Research Project (1)"));
        pause(500);

        verifyElementPresent(By.linkText("Human and Ape Brain Comparison"));

        clickAndWait(By.linkText("Service (2)"));
        pause(500);

        verifyElementPresent(By.linkText("Gorilla Moving Company"));
        verifyElementPresent(By.linkText("Primate Heart Health"));

        clickAndWait(By.linkText("Transport Service (1)"));
        pause(500);

        verifyElementPresent(By.linkText("Gorilla Moving Company"));

        clickAndWait(By.linkText("Courses"));
        assertTitle("Courses");

        clickAndWait(By.linkText("Course (2)"));
        pause(500);

        verifyTextPresent("Course");
        verifyElementPresent(By.linkText("Introduction to Primates"));
        verifyElementPresent(By.linkText("Introduction to Primate Health"));

        clickAndWait(By.linkText("Equipment"));
        assertTitle("Equipment");

        clickAndWait(By.linkText("Equipment (2)"));
        pause(500);

        verifyTextPresent("Equipment");
        verifyElementPresent(By.linkText("Portable Primate Habitat"));
        verifyElementPresent(By.linkText("Primate Feeder"));

        clickAndWait(By.linkText("Locations"));
        assertTitle("Locations");

        clickAndWait(By.linkText("Building (2)"));
        pause(500);

        verifyTextPresent("Building");
        verifyElementPresent(By.linkText("Jane Memorial Building"));
        verifyElementPresent(By.linkText("Primate Memorial Building"));

        clickAndWait(By.linkText("Facility (5)"));
        pause(500);

        verifyElementPresent(By.linkText("Jane Memorial Building"));
        verifyElementPresent(By.linkText("Lab Admin Office"));
        verifyElementPresent(By.linkText("Primate Memorial Building"));
        verifyElementPresent(By.linkText("Primate Research Lab Room 123"));
        verifyElementPresent(By.linkText("State Fair Park"));

        clickAndWait(By.linkText("Room (1)"));
        pause(500);

        verifyElementPresent(By.linkText("Lab Admin Office"));

        clickAndWait(By.linkText("Site Admin"));
        assertTitle("VIVO Site Administration");

        clickAndWait(By.linkText("Page management"));
        assertTitle("Pages");

        clickAndWait(By.xpath("(//img[@alt='delete this page'])[2]"));
        assertConfirmation("Are you sure you wish to delete this page:  Activities?");

        assertTitle("Pages");

        clickAndWait(By.linkText("Site Admin"));
        assertTitle("VIVO Site Administration");

        clickAndWait(By.linkText("Page management"));
        assertTitle("Pages");

        clickAndWait(By.xpath("(//img[@alt='delete this page'])[4]"));
        assertConfirmation("Are you sure you wish to delete this page:  Courses?");

        assertTitle("Pages");

        clickAndWait(By.linkText("Site Admin"));
        assertTitle("VIVO Site Administration");

        clickAndWait(By.linkText("Page management"));
        assertTitle("Pages");

        clickAndWait(By.xpath("(//img[@alt='delete this page'])[6]"));
        assertConfirmation("Are you sure you wish to delete this page:  Equipment?");

        assertTitle("Pages");

        clickAndWait(By.linkText("Site Admin"));
        assertTitle("VIVO Site Administration");

        clickAndWait(By.linkText("Page management"));
        assertTitle("Pages");

        clickAndWait(By.xpath("(//img[@alt='delete this page'])[7]"));
        assertConfirmation("Are you sure you wish to delete this page:  Locations?");

        assertTitle("Pages");
    }
}
