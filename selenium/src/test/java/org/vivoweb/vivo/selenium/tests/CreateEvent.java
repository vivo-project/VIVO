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
public class CreateEvent {
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
    public void createEvent() {
        clickAndWait(By.linkText("Site Admin"));
        assertTitle("VIVO Site Administration");

        verifyTextPresent("Data Input");

        selectByLabel(By.id("VClassURI"), "Conference (bibo)");

        clickAndWait(By.xpath("//input[@value='Add individual of this class']"));
        assertTitle("Edit");

        verifyTextPresent("Create a new Conference");

        clickAndWait(By.linkText("Cancel"));
        assertTitle("VIVO Site Administration");

        selectByLabel(By.id("VClassURI"), "Conference (bibo)");

        clickAndWait(By.xpath("//input[@value='Add individual of this class']"));
        assertTitle("Edit");

        verifyTextPresent("Create a new Conference");

        clickAndWait(By.id("submit"));
        assertTitle("Edit");

        verifyTextPresent("Please enter a value in the Name field.");

        type(By.id("label"), "Primate Health Conference");

        clickAndWait(By.id("submit"));
        assertTitle("Primate Health Conference");

        clickAndWait(By.xpath("//div[@id='wrapper-content']/ul/li[8]"));
        clickAndWait(By.xpath("//h3[@id='description']/a/img"));
        assertTitle("Edit");

        verifyTextPresent("Add new entry for: description");
        typeTinyMCE("First annual conference for those interested in the general health of primates.");

        clickAndWait(By.id("submit"));
        assertTitle("Primate Health Conference");

        clickAndWait(By.xpath("//h3[@id='hasProceedings']/a/img"));
        assertTitle("Edit");

        clickAndWait(By.id("offerCreate"));
        assertTitle("Edit");

        type(By.id("label"), "PHC Proceedings");

        clickAndWait(By.id("submit"));
        assertTitle("Primate Health Conference");

        clickAndWait(By.xpath("//h3[@id='contactInformation']/a/img"));
        assertTitle("Edit");

        verifyTextPresent("Add new entry for: contact information");
        typeTinyMCE("info@primateconf.org");

        clickAndWait(By.id("submit"));
        assertTitle("Primate Health Conference");

        clickAndWait(By.xpath("//h3[@id='geographicFocus']/a/img"));
        assertTitle("Edit");

        verifyTextPresent("If you don't find the appropriate entry on the selection list above:");

        vivoAutoCompleteSelect(By.id("object"), "Kenya", Keys.ARROW_DOWN);

        clickAndWait(By.id("submit"));
        assertTitle("Primate Health Conference");

        clickAndWait(By.xpath("(//h3[@id='RO_0001025']/a)[2]"));
        assertTitle("Edit");

        verifyTextPresent("If you don't find the appropriate entry on the selection list above:");
        verifyTextPresent("Geographic Location Name");

        vivoAutoCompleteSelect(By.id("object"), "Cong", Keys.ARROW_DOWN);

        clickAndWait(By.id("submit"));
        assertTitle("Primate Health Conference");

        clickAndWait(By.cssSelector("a.add-RO_0001025 > img.add-individual"));
        assertTitle("Edit");

        verifyTextPresent("If you don't find the appropriate entry on the selection list above:");
        selectByLabel(By.id("typeOfNew"), "Facility (vivo)");

        clickAndWait(By.id("offerCreate"));
        assertTitle("Edit");

        type(By.id("label"), "State Fair Park");

        clickAndWait(By.id("submit"));
        assertTitle("Primate Health Conference");

        clickAndWait(By.xpath("//h3[@id='dateTimeInterval']/a/img"));
        assertTitle("Edit");

        type(By.id("startField-year"), "2011");
        type(By.id("startField-month"), "1");
        type(By.id("startField-day"), "5");

        type(By.id("endField-year"), "2011");
        type(By.id("endField-month"), "1");
        type(By.id("endField-day"), "9");

        clickAndWait(By.id("submit"));
        assertTitle("Primate Health Conference");

        clickAndWait(By.xpath("//h3[@id='presents']/a/img"));
        assertTitle("Edit");

        clickAndWait(By.id("submit"));
        assertTitle("Primate Health Conference");

        clickAndWait(By.cssSelector("a.add-BFO_0000051 > img.add-individual"));
        assertTitle("Edit");

        verifyTextPresent("If you don't find the appropriate entry on the selection list above:");
        selectByLabel(By.id("objectVar"), "Primate Health Check (Event)");

        clickAndWait(By.id("submit"));
        assertTitle("Primate Health Conference");

        clickAndWait(By.cssSelector("a.add-hasSubjectArea > img.add-individual"));
        assertTitle("Edit");

        clickAndWait(By.id("showAddFormButton"));
        clickAndWait(By.linkText("Select or create a VIVO-defined concept. "));

        assertTitle("Edit");
        vivoAutoCompleteSelect(By.id("relatedIndLabel"), "Anim", Keys.ARROW_DOWN);

        clickAndWait(By.id("submit"));
        assertTitle("Edit");

        clickAndWait(By.linkText("Return to Profile Page"));
        assertTitle("Primate Health Conference");

        clickAndWait(By.xpath("//h3[@id='abbreviation']/a/img"));
        assertTitle("Edit");

        typeTinyMCE("PrimHConf");

        clickAndWait(By.id("submit"));
        assertTitle("Primate Health Conference");

        clickAndWait(By.cssSelector("a.add-BFO_0000050 > img.add-individual"));
        assertTitle("Edit");

        selectByLabel(By.id("objectVar"), "Primate Health and Fitness (Invited Talk)");

        clickAndWait(By.id("submit"));
        assertTitle("Primate Health Conference");

        clickAndWait(By.xpath("(//img[@alt='add'])[6]"));
        assertTitle("Edit");

        clickAndWait(By.id("submit"));
        assertTitle("Primate Health Conference");

        clickAndWait(By.cssSelector("a.add-RO_0002234 > img.add-individual"));
        assertTitle("Edit");

        selectByLabel(By.id("objectVar"), "Primate Happenings (Blog Posting)");

        clickAndWait(By.id("submit"));
        assertTitle("Primate Health Conference");

        verifyTextPresent(
                "Primate Health Conference",
                "Conference",
                "First annual conference for those interested in the general health of primates.",
                "PrimHConf",
                "January 5, 2011 - January 9, 2011",
                "info@primateconf.org"
        );

        verifyElementPresent(By.linkText("Primate Health and Fitness"));
        verifyElementPresent(By.linkText("Primate Health Check"));
        verifyElementPresent(By.linkText("Primate Health Talks"));
        verifyElementPresent(By.linkText("Animal Health"));
        verifyElementPresent(By.linkText("PHC Proceedings"));
        verifyElementPresent(By.linkText("Kenya"));
        verifyElementPresent(By.linkText("Primate Happenings"));
        verifyElementPresent(By.linkText("http://primatehealthintro.cornell.edu"));
        verifyElementPresent(By.linkText("State Fair Park"));
        verifyElementPresent(By.linkText("Congo"));
    }
}
