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
public class CreateLocation {
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
    public void createLocation() {
        clickAndWait(By.linkText("Site Admin"));
        assertTitle("VIVO Site Administration");

        verifyTextPresent("Data Input");

        selectByLabel(By.id("VClassURI"), "Building (vivo)");

        clickAndWait(By.xpath("//input[@value='Add individual of this class']"));
        assertTitle("Edit");

        verifyTextPresent("Create a new Building");

        clickAndWait(By.linkText("Cancel"));
        assertTitle("VIVO Site Administration");

        selectByLabel(By.id("VClassURI"), "Building (vivo)");

        clickAndWait(By.xpath("//input[@value='Add individual of this class']"));
        assertTitle("Edit");

        verifyTextPresent("Create a new Building");

        clickAndWait(By.id("submit"));
        assertTitle("Edit");

        verifyTextPresent("Please enter a value in the Name field.");

        type(By.id("label"), "Jane Memorial Building");

        clickAndWait(By.id("submit"));
        assertTitle("Jane Memorial Building");

        clickAndWait(By.cssSelector("a.add-BFO_0000051 > img.add-individual"));
        assertTitle("Edit");

        verifyTextPresent("There are no entries in the system from which to select.");

        clickAndWait(By.id("offerCreate"));
        assertTitle("Edit");

        verifyTextPresent("Create \"rooms\" entry for Jane Memorial Building");
        type(By.id("label"), "Lab Admin Office");

        clickAndWait(By.id("submit"));
        assertTitle("Jane Memorial Building");

        clickAndWait(By.cssSelector("a.add-BFO_0000050 > img.add-individual"));
        assertTitle("Edit");

        verifyTextPresent("If you don't find the appropriate entry on the selection list above:");
        selectByLabel(By.id("typeOfNew"), "Geographic Location (vivo)");

        clickAndWait(By.id("offerCreate"));
        assertTitle("Edit");

        type(By.id("label"), "Primate Quad");

        clickAndWait(By.id("submit"));
        assertTitle("Jane Memorial Building");

        clickAndWait(By.cssSelector("a.add-RO_0001015 > img.add-individual"));
        assertTitle("Edit");

        verifyTextPresent("If you don't find the appropriate entry on the selection list above:");

        clickAndWait(By.id("submit"));
        assertTitle("Jane Memorial Building");

        clickAndWait(By.xpath("(//img[@alt='add'])[3]"));
        assertTitle("Edit");

        selectByLabel(By.id("objectVar"), "Primate Heart Health (Service)");

        clickAndWait(By.id("submit"));
        assertTitle("Jane Memorial Building");

        clickAndWait(By.xpath("(//h3[@id='RO_0001015']/a)[3]"));
        assertTitle("Edit");

        selectByLabel(By.id("objectVar"), "Primate University of America (University)");

        clickAndWait(By.id("submit"));
        assertTitle("Jane Memorial Building");

        clickAndWait(By.xpath("(//img[@alt='add'])[5]"));
        assertTitle("Edit");

        selectByLabel(By.id("objectVar"), "Primate Health Check (Event)");

        clickAndWait(By.id("submit"));
        assertTitle("Jane Memorial Building");

        verifyElementPresent(By.linkText("Portable Primate Habitat"));
        verifyElementPresent(By.linkText("Primate Heart Health"));
        verifyElementPresent(By.linkText("Primate University of America"));
        verifyElementPresent(By.linkText("Primate Health Check"));
        verifyElementPresent(By.linkText("Lab Admin Office"));
        verifyElementPresent(By.linkText("Primate Quad"));
    }
}
