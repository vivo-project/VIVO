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
public class CreateActivity {
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
    public void createActivity() {
        clickAndWait(By.linkText("Site Admin"));
        assertTitle("VIVO Site Administration");

        verifyTextPresent("Data Input");

        selectByLabel(By.id("VClassURI"), "Grant (vivo)");

        clickAndWait(By.xpath("//input[@value='Add individual of this class']"));
        assertTitle("Edit");
        verifyTextPresent("Create a new Grant");

        clickAndWait(By.linkText("Cancel"));
        assertTitle("VIVO Site Administration");

        selectByLabel(By.id("VClassURI"), "Grant (vivo)");

        clickAndWait(By.xpath("//input[@value='Add individual of this class']"));
        assertTitle("Edit");
        verifyTextPresent("Create a new Grant");

        clickAndWait(By.id("submit"));
        assertTitle("Edit");
        verifyTextPresent("Please enter a value in the Name field.");

        type(By.id("label"), "Primate Elderly Care");

        clickAndWait(By.id("submit"));
        assertTitle("Primate Elderly Care");

        clickAndWait(By.xpath("//div[@id='wrapper-content']/ul/li[@groupname='viewAll']"));
        assertTitle("Primate Elderly Care");

        clickAndWait(By.cssSelector("a.add-relates > img.add-individual"));
        assertTitle("Edit");

        vivoAutoCompleteSelect(By.id("organization"), "primate colleges of the wor", Keys.ARROW_DOWN);

        clickAndWait(By.cssSelector("input.submit"));
        assertTitle("Primate Elderly Care");

        clickAndWait(By.cssSelector("a.add-assignedBy > img.add-individual"));
        assertTitle("Edit");

        verifyTextPresent("If you don't find the appropriate entry on the selection list above:");
        selectByLabel(By.id("objectVar"), "Primate Research Laboratory (Laboratory)");

        clickAndWait(By.id("submit"));
        assertTitle("Primate Elderly Care");

        clickAndWait(By.xpath("//h3[@id='abstract']/a/img"));
        assertTitle("Edit");

        verifyTextPresent("Add new entry for: abstract");
        typeTinyMCE("Purpose of grant is to determine the appropriate environment, physical activity, and diet for primates as they age.");

        clickAndWait(By.id("submit"));
        assertTitle("Primate Elderly Care");

        clickAndWait(By.xpath("//h3[@id='grantSubcontractedThrough']/a"));
        assertTitle("Edit");

        selectByLabel(By.id("objectVar"), "Primate Colleges of the World (Consortium)");

        clickAndWait(By.id("submit"));
        assertTitle("Primate Elderly Care");

        clickAndWait(By.xpath("//h3[@id='totalAwardAmount']/a/img"));
        assertTitle("Edit");

        verifyTextPresent("Add new entry for: total award amount");
        typeTinyMCE("$1,234,567");

        clickAndWait(By.id("submit"));
        assertTitle("Primate Elderly Care");

        clickAndWait(By.xpath("//h3[@id='grantDirectCosts']/a/img"));
        assertTitle("Edit");

        typeTinyMCE("$999,999");

        clickAndWait(By.id("submit"));
        assertTitle("Primate Elderly Care");

        clickAndWait(By.xpath("//h3[@id='sponsorAwardId']/a/img"));
        assertTitle("Edit");

        typeTinyMCE("1234-5678");

        clickAndWait(By.id("submit"));
        assertTitle("Primate Elderly Care");

        clickAndWait(By.xpath("//h3[@id='geographicFocus']/a/img"));
        assertTitle("Edit");

        vivoAutoCompleteSelect(By.id("object"), "Afri", Keys.ARROW_DOWN);

        clickAndWait(By.id("submit"));
        assertTitle("Primate Elderly Care");

        clickAndWait(By.xpath("//h3[@id='dateTimeInterval']/a/img"));
        assertTitle("Edit");

        type(By.id("startField-year"), "2010");
        type(By.id("startField-month"), "9");
        type(By.id("startField-day"), "1");

        type(By.id("endField-year"), "2012");
        type(By.id("endField-month"), "8");
        type(By.id("endField-day"), "31");

        clickAndWait(By.id("submit"));
        assertTitle("Primate Elderly Care");

        clickAndWait(By.xpath("//h3[@id='localAwardId']/a/img"));
        assertTitle("Edit");

        verifyTextPresent("Add new entry for: local award ID");
        typeTinyMCE("P999-1234");

        clickAndWait(By.id("submit"));
        assertTitle("Primate Elderly Care");

        clickAndWait(By.cssSelector("a.add-fundingVehicleFor > img.add-individual"));
        assertTitle("Edit");

        vivoAutoCompleteSelect(By.id("object"), "primate health chec", Keys.ARROW_DOWN);

        clickAndWait(By.id("submit"));
        assertTitle("Primate Elderly Care");

        clickAndWait(By.xpath("//h3[@id='hasSubjectArea']/a/img"));
        assertTitle("Edit");

        verifyTextPresent("Manage Concepts");

        clickAndWait(By.id("showAddFormButton"));
        clickAndWait(By.linkText("Select or create a VIVO-defined concept. "));

        assertTitle("Edit");

        type(By.id("relatedIndLabel"), "Elderly Care");

        clickAndWait(By.id("submit"));
        assertTitle("Edit");

        clickAndWait(By.linkText("Return to Profile Page"));
        assertTitle("Primate Elderly Care");

        clickAndWait(By.cssSelector("a.add-BFO_0000051 > img.add-individual"));
        assertTitle("Edit");

        selectByLabel(By.id("objectVar"), "Primate Habitat Research Grant");

        clickAndWait(By.id("submit"));
        assertTitle("Primate Elderly Care");

        clickAndWait(By.cssSelector("a.add-BFO_0000050 > img.add-individual"));
        assertTitle("Edit");

        selectByLabel(By.id("objectVar"), "Primate Survival Planning Grant");

        clickAndWait(By.id("submit"));
        assertTitle("Primate Elderly Care");

        clickAndWait(By.cssSelector("a.add-supportedInformationResource"));
        assertTitle("Edit");

        selectByLabel(By.id("objectVar"), "Primate Info (Database)");

        clickAndWait(By.id("submit"));
        assertTitle("Primate Elderly Care");

        verifyTextPresent(
                "Primate Elderly Care",
                "Grant",
                "Purpose of grant is to determine the appropriate environment, physical activity, and diet for primates as they age.",
                "September 1, 2010 - August 31, 2012",
                "$1,234,567",
                "$999,999",
                "1234-5678",
                "P999-1234"

        );

        verifyElementPresent(By.linkText("Elderly Care"));
        verifyElementPresent(By.linkText("Primate Research Laboratory"));
        verifyElementPresent(By.linkText("Primate Colleges of the World"));
        verifyElementPresent(By.linkText("Africa"));
        verifyElementPresent(By.linkText("Primate Habitat Research Grant"));
        verifyElementPresent(By.linkText("Primate Survival Planning Grant"));
        verifyElementPresent(By.linkText("Primate Info"));
    }
}
