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
public class CreateEquipment {
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
    public void createEquipment() {
        clickAndWait(By.linkText("Site Admin"));
        assertTitle("VIVO Site Administration");

        verifyTextPresent("Data Input");

        selectByLabel(By.id("VClassURI"), "Equipment (vivo)");

        clickAndWait(By.xpath("//input[@value='Add individual of this class']"));
        assertTitle("Edit");

        verifyTextPresent("Create a new Equipment");

        clickAndWait(By.linkText("Cancel"));
        assertTitle("VIVO Site Administration");

        verifyTextPresent("Data Input");

        selectByLabel(By.id("VClassURI"), "Equipment (vivo)");

        clickAndWait(By.xpath("//input[@value='Add individual of this class']"));
        assertTitle("Edit");

        verifyTextPresent("Create a new Equipment");

        clickAndWait(By.id("submit"));
        assertTitle("Edit");

        verifyTextPresent("Please enter a value in the Name field.");

        type(By.id("label"), "Primate Feeder");

        clickAndWait(By.id("submit"));
        assertTitle("Primate Feeder");

        clickAndWait(By.xpath("//div[@id='wrapper-content']/ul/li[8]"));

        clickAndWait(By.xpath("//h3[@id='equipmentFor']/a/img"));
        assertTitle("Edit");

        verifyTextPresent("If you don't find the appropriate entry on the selection list above:");

        selectByLabel(By.id("objectVar"), "Primate Research Laboratory (Laboratory)");

        clickAndWait(By.id("submit"));
        assertTitle("Primate Feeder");

        clickAndWait(By.cssSelector("a.add-RO_0001025 > img.add-individual"));
        assertTitle("Edit");

        verifyTextPresent("If you don't find the appropriate entry on the selection list above:");

        selectByLabel(By.id("typeOfNew"), "Facility (vivo)");

        clickAndWait(By.id("offerCreate"));
        assertTitle("Edit");

        verifyTextPresent("Create \"housed in facility\" entry for Primate Feeder");
        type(By.id("label"), "Primate Research Lab Room 123");

        clickAndWait(By.id("submit"));
        assertTitle("Primate Feeder");

        clickAndWait(By.xpath("//h3[@id='freetextKeyword']/a/img"));
        assertTitle("Edit");

        verifyTextPresent("Add new entry for: keywords");

        typeTinyMCE("Animal Diet");

        clickAndWait(By.id("submit"));

        clickAndWait(By.linkText("Primate Research Lab Room 123"));
        assertTitle("Primate Research Lab Room 123");

        clickAndWait(By.xpath("(//img[@alt='add'])[3]"));
        assertTitle("Edit");

        selectByLabel(By.id("objectVar"), "Primate Heart Health (Service)");

        clickAndWait(By.id("submit"));
        assertTitle("Primate Research Lab Room 123");

        clickAndWait(By.xpath("(//img[@alt='add'])[4]"));
        assertTitle("Edit");

        selectByLabel(By.id("objectVar"), "Primate University of America (University)");

        clickAndWait(By.id("submit"));
        assertTitle("Primate Research Lab Room 123");

        clickAndWait(By.xpath("(//img[@alt='add'])[5]"));
        assertTitle("Edit");

        selectByLabel(By.id("objectVar"), "Primate Health Check (Event)");

        clickAndWait(By.id("submit"));
        assertTitle("Primate Research Lab Room 123");

        clickAndWait(By.cssSelector("a.add-BFO_0000050 > img.add-individual"));
        assertTitle("Edit");

        vivoAutoCompleteSelect(By.id("object"), "United State", Keys.ARROW_DOWN);

        clickAndWait(By.id("submit"));
        assertTitle("Primate Research Lab Room 123");

        verifyElementPresent(By.linkText("Primate Feeder"));
        verifyElementPresent(By.linkText("Primate Heart Health"));
        verifyElementPresent(By.linkText("Primate University of America"));
        verifyElementPresent(By.linkText("Primate Health Check"));
        verifyElementPresent(By.linkText("United States of America"));
    }
}
