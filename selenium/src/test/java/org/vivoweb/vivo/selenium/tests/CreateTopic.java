package org.vivoweb.vivo.selenium.tests;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import static org.vivoweb.vivo.selenium.VIVOAppTester.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CreateTopic {
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
    public void createTopic() {
        clickAndWait(By.linkText("Site Admin"));
        assertTitle("VIVO Site Administration");

        verifyTextPresent("Data Input");

        selectByLabel(By.id("VClassURI"), "Concept (skos)");

        clickAndWait(By.xpath("//input[@value='Add individual of this class']"));
        assertTitle("Edit");

        verifyTextPresent("Create a new Concept");

        clickAndWait(By.linkText("Cancel"));
        assertTitle("VIVO Site Administration");

        selectByLabel(By.id("VClassURI"), "Concept (skos)");

        clickAndWait(By.xpath("//input[@value='Add individual of this class']"));
        assertTitle("Edit");

        verifyTextPresent("Create a new Concept");

        clickAndWait(By.id("submit"));
        assertTitle("Edit");

        verifyTextPresent("Please enter a value in the Name field.");

        type(By.id("label"), "Primate Health");

        clickAndWait(By.id("submit"));
        assertTitle("Primate Health");

        clickAndWait(By.cssSelector("li.nonSelectedGroupTab.clickable"));

        clickAndWait(By.xpath("//h3[@id='broader']/a/img"));
        assertTitle("Edit");

        verifyTextPresent("If you don't find the appropriate entry on the selection list above:");
        selectByLabel(By.id("objectVar"), "Animal Health");

        clickAndWait(By.id("submit"));
        assertTitle("Primate Health");

        clickAndWait(By.xpath("//h3[@id='narrower']/a/img"));
        assertTitle("Edit");

        verifyTextPresent("If you don't find the appropriate entry on the selection list above:");

        clickAndWait(By.id("offerCreate"));
        assertTitle("Edit");

        verifyTextPresent("Create \"narrower concept\" entry for Primate Health");
        type(By.id("label"), "Primate Diet");

        clickAndWait(By.id("submit"));
        assertTitle("Primate Health");

        clickAndWait(By.xpath("//h3[@id='related']/a/img"));
        assertTitle("Edit");

        verifyTextPresent("If you don't find the appropriate entry on the selection list above:");

        clickAndWait(By.id("offerCreate"));
        assertTitle("Edit");

        verifyTextPresent("Create \"related concept\" entry for Primate Health");
        type(By.id("label"), "Ape Health");

        clickAndWait(By.id("submit"));
        assertTitle("Primate Health");

        verifyElementPresent(By.linkText("Animal Health"));
        verifyElementPresent(By.linkText("Primate Diet"));
        verifyElementPresent(By.linkText("Ape Health"));
    }
}
