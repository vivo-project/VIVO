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
public class CreateCourses {
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
    public void createCourses() {
        clickAndWait(By.linkText("Site Admin"));
        assertTitle("VIVO Site Administration");

        verifyTextPresent("Data Input");

        selectByLabel(By.id("VClassURI"), "Course (vivo)");

        clickAndWait(By.id("submit"));
        assertTitle("Edit");
        verifyTextPresent("Create a new Course");

        clickAndWait(By.linkText("Cancel"));
        assertTitle("VIVO Site Administration");

        selectByLabel(By.id("VClassURI"), "Course (vivo)");

        clickAndWait(By.id("submit"));
        assertTitle("Edit");
        verifyTextPresent("Create a new Course");

        clickAndWait(By.id("submit"));
        assertTitle("Edit");
        verifyTextPresent("Please enter a value in the Name field.");

        type(By.id("label"), "Introduction to Primate Health");

        clickAndWait(By.id("submit"));
        assertTitle("Introduction to Primate Health");

        clickAndWait(By.xpath("//h3[@id='description']/a/img"));
        assertTitle("Edit");
        verifyTextPresent("Add new entry for: description");

        typeTinyMCE("Learn the basics about the general health of primates.");

        clickAndWait(By.id("submit"));
        assertTitle("Introduction to Primate Health");

        clickAndWait(By.cssSelector("a.add-offeredBy > img.add-individual"));
        assertTitle("Edit");
        verifyTextPresent("If you don't find the appropriate entry on the selection list above:");

        clickAndWait(By.id("submit"));
        assertTitle("Introduction to Primate Health");

        clickAndWait(By.xpath("//h3[@id='prerequisiteFor']/a/img"));
        assertTitle("Edit");
        verifyTextPresent("If you don't find the appropriate entry on the selection list above:");

        clickAndWait(By.id("submit"));
        assertTitle("Introduction to Primate Health");

        clickAndWait(By.xpath("//h3[@id='geographicFocus']/a/img"));
        assertTitle("Edit");

        vivoAutoCompleteSelect(By.id("object"), "Afri", Keys.ARROW_DOWN);

        clickAndWait(By.id("submit"));
        assertTitle("Introduction to Primate Health");

        clickAndWait(By.xpath("//h3[@id='dateTimeInterval']/a"));
        assertTitle("Edit");

        verifyTextPresent("Create date time value for Introduction to Primate Health");

        type(By.id("startField-year"), "2007");
        type(By.id("startField-month"), "9");
        type(By.id("startField-day"), "1");

        type(By.id("endField-year"), "2007");
        type(By.id("endField-month"), "12");
        type(By.id("endField-day"), "15");

        clickAndWait(By.id("submit"));
        assertTitle("Introduction to Primate Health");

        clickAndWait(By.xpath("//div[@id='wrapper-content']/ul/li[@groupname='viewAll']"));

        clickAndWait(By.cssSelector("a.add-BFO_0000051 > img.add-individual"));
        assertTitle("Edit");

        selectByLabel(By.id("objectVar"), "Primate Health Check (Event)");

        clickAndWait(By.id("submit"));
        assertTitle("Introduction to Primate Health");

        clickAndWait(By.xpath("//h3[@id='hasSubjectArea']/a/img"));
        assertTitle("Edit");

        clickAndWait(By.id("showAddFormButton"));

        clickAndWait(By.linkText("Select or create a VIVO-defined concept. "));
        assertTitle("Edit");

        type(By.id("relatedIndLabel"), "Animal Health");

        clickAndWait(By.id("submit"));
        assertTitle("Edit");

        clickAndWait(By.linkText("Return to Profile Page"));
        assertTitle("Introduction to Primate Health");

        clickAndWait(By.cssSelector("a.add-BFO_0000050 > img.add-individual"));
        assertTitle("Edit");

        selectByLabel(By.id("objectVar"), "Primate Health and Fitness (Invited Talk)");

        clickAndWait(By.id("submit"));
        assertTitle("Introduction to Primate Health");

        clickAndWait(By.xpath("(//img[@alt='add'])[7]"));
        assertTitle("Edit");

        selectByLabel(By.id("typeOfNew"), "Seminar Series (vivo)");

        clickAndWait(By.id("offerCreate"));
        assertTitle("Edit");

        type(By.id("label"), "Primate Health Talks");

        clickAndWait(By.id("submit"));
        assertTitle("Introduction to Primate Health");

        // Test publication tab entry
        clickAndWait(By.xpath("//div[@id='wrapper-content']/ul/li[@groupname='publications']"));

        clickAndWait(By.xpath("//h3[@id='presents']/a/img"));
        assertTitle("Edit");

        selectByLabel(By.id("typeOfNew"), "Webpage (bibo)");
        verifyTextPresent("If you don't find the appropriate entry on the selection list above:");

        clickAndWait(By.id("offerCreate"));
        assertTitle("Edit");

        verifyTextPresent("Create \"related documents\" entry for Introduction to Primate Health");

        type(By.id("label"), "http://primatehealthintro.cornell.edu");

        clickAndWait(By.id("submit"));
        assertTitle("Introduction to Primate Health");

        clickAndWait(By.cssSelector("a.add-RO_0002234 > img.add-individual"));
        assertTitle("Edit");

        selectByLabel(By.id("objectVar"), "Primate Happenings (Blog Posting)");

        clickAndWait(By.id("submit"));
        assertTitle("Introduction to Primate Health");

        clickAndWait(By.xpath("//div[@id='wrapper-content']/ul/li[@groupname='contact']"));
        clickAndWait(By.xpath("//h3[@id='contactInformation']/a/img"));
        assertTitle("Edit");

        verifyTextPresent("Add new entry for: contact information");

        typeTinyMCE("ME Tarzan at metarzan@primates.edu or 555-555-5553");

        clickAndWait(By.id("submit"));
        assertTitle("Introduction to Primate Health");

        clickAndWait(By.xpath("(//h3[@id='RO_0001025']/a)[2]"));
        assertTitle("Edit");

        vivoAutoCompleteSelect(By.id("object"), "lib", Keys.ARROW_DOWN);

        clickAndWait(By.id("submit"));
        assertTitle("Introduction to Primate Health");

        clickAndWait(By.cssSelector("a.add-RO_0001025 > img.add-individual"));
        assertTitle("Edit");

        clickAndWait(By.id("offerCreate"));
        assertTitle("Edit");

        type(By.id("label"), "Primate Memorial Building");

        clickAndWait(By.id("submit"));
        assertTitle("Introduction to Primate Health");

        clickAndWait(By.xpath("//div[@id='wrapper-content']/ul/li[@groupname='viewAll']"));
        clickAndWait(By.xpath("//h3[@id='courseCredits']/a/img"));
        assertTitle("Edit");

        typeTinyMCE("9");

        clickAndWait(By.id("submit"));
        assertTitle("Introduction to Primate Health");

        clickAndWait(By.cssSelector("a.add-hasPrerequisite > img.add-individual"));
        assertTitle("Edit");

        selectByLabel(By.id("objectVar"), "Introduction to Primates");

        clickAndWait(By.id("submit"));
        assertTitle("Introduction to Primate Health");

        clickAndWait(By.xpath("//div[@id='wrapper-content']/ul/li[@groupname='viewAll']"));
        verifyTextPresent(
                "Introduction to Primate Health",
                "Course",
                "Learn the basics about the general health of primates.",
                "Primate College of America",
                "September 1, 2007 - December 15, 2007",
                "ME Tarzan at metarzan@primates.edu or 555-555-5553",
                "9");
        verifyElementPresent(By.linkText("Primate Health and Fitness"));
        verifyElementPresent(By.linkText("Primate Health Check"));
        verifyElementPresent(By.linkText("Primate Health Talks"));
        verifyElementPresent(By.linkText("Animal Health"));
        verifyElementPresent(By.linkText("Introduction to Primate Health"));
        verifyElementPresent(By.linkText("Africa"));
        verifyElementPresent(By.linkText("Primate Happenings"));
        verifyElementPresent(By.linkText("http://primatehealthintro.cornell.edu"));
        verifyElementPresent(By.linkText("Primate Memorial Building"));
        verifyElementPresent(By.linkText("Liberia"));
        verifyElementPresent(By.linkText("Introduction to Primates"));
    }
}
