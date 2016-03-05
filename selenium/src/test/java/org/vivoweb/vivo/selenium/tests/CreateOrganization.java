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
public class CreateOrganization {
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
    public void createOrganization() {
        clickAndWait(By.linkText("Site Admin"));
        assertTitle("VIVO Site Administration");

        verifyTextPresent("Data Input");

        selectByLabel(By.id("VClassURI"), "College (vivo)");

        clickAndWait(By.xpath("//input[@value='Add individual of this class']"));
        assertTitle("Edit");
        verifyTextPresent("Create a new College");
        verifyTextPresent("Name");

        clickAndWait(By.linkText("Cancel"));

        assertTitle("VIVO Site Administration");

        selectByLabel(By.id("VClassURI"), "College (vivo)");

        clickAndWait(By.xpath("//input[@value='Add individual of this class']"));
        assertTitle("Edit");
        verifyTextPresent("Create a new College");
        verifyTextPresent("Name");

        clickAndWait(By.id("submit"));
        assertTitle("Edit");
        verifyTextPresent("Please enter a value in the Name field.");

        type(By.id("label"), "Primate College of America");

        clickAndWait(By.id("submit"));
        assertTitle("Primate College of America");

        clickAndWait(By.xpath("//h2[@id='overview']/a/img"));
        assertTitle("Edit");

        typeTinyMCE("The Primate College of America is a privately-funded college for the study of primates.");

        clickAndWait(By.id("submit"));
        assertTitle("Primate College of America");

        clickAndWait(By.cssSelector("a.add-offers > img.add-individual"));
        assertTitle("Edit");

        selectByLabel(By.id("objectVar"), "B.S. Bachelor of Science (Academic Degree)");

        clickAndWait(By.id("submit"));
        assertTitle("Primate College of America");

        clickAndWait(By.xpath("//div[@id='wrapper-content']/ul/li[12]"));
        clickAndWait(By.cssSelector("a.add-hasPredecessorOrganization > img.add-individual"));
        assertTitle("Edit");

        selectByLabel(By.id("typeOfNew"), "College (vivo)");
        clickAndWait(By.id("offerCreate"));

        assertTitle("Edit");
        type(By.id("label"), "Primate College of New York");

        clickAndWait(By.id("submit"));
        assertTitle("Primate College of America");

        clickAndWait(By.xpath("//div[@id='wrapper-content']/ul/li[8]"));
        clickAndWait(By.cssSelector("a.add-assigns > img.add-individual"));

        assertTitle("Edit");
        clickAndWait(By.id("offerCreate"));

        assertTitle("Edit");
        type(By.id("label"), "Primate Habitat Research Grant");

        clickAndWait(By.id("submit"));
        assertTitle("Primate College of America");

        clickAndWait(By.cssSelector("li.nonSelectedGroupTab.clickable"));
        clickAndWait(By.cssSelector("a.add-sponsors > img.add-individual"));

        assertTitle("Edit");
        clickAndWait(By.id("offerCreate"));

        assertTitle("Edit");
        type(By.id("label"), "Primate Student of the Year");

        clickAndWait(By.id("submit"));
        assertTitle("Primate College of America");

        clickAndWait(By.cssSelector("a.add-relatedBy > img.add-individual"));

        assertTitle("Edit");
        type(By.id("award"), "Best Primate College");
        clickAndWait(By.xpath("//input[@value='Create Entry']"));

        assertTitle("Primate College of America");
        clickAndWait(By.xpath("//div[@id='wrapper-content']/ul/li[14]"));

        clickAndWait(By.cssSelector("a.add-hasEquipment > img.add-individual"));

        assertTitle("Edit");
        clickAndWait(By.id("offerCreate"));

        assertTitle("Edit");
        type(By.id("label"), "Portable Primate Habitat");

        clickAndWait(By.id("submit"));
        assertTitle("Primate College of America");

        clickAndWait(By.xpath("//div[@id='wrapper-content']/ul/li[8]"));
        clickAndWait(By.cssSelector("a.add-subcontractsGrant > img.add-individual"));

        assertTitle("Edit");
        clickAndWait(By.id("offerCreate"));

        assertTitle("Edit");
        type(By.id("label"), "Primate Survival Planning Grant");

        clickAndWait(By.id("submit"));
        assertTitle("Primate College of America");

        clickAndWait(By.xpath("//div[@id='wrapper-content']/ul/li[4]"));
        clickAndWait(By.cssSelector("a.add-BFO_0000051 > img.add-individual"));

        assertTitle("Edit");
        selectByLabel(By.id("typeOfNew"), "Laboratory (vivo)");

        clickAndWait(By.id("offerCreate"));

        assertTitle("Edit");
        type(By.id("label"), "Primate Research Laboratory");

        clickAndWait(By.id("submit"));
        assertTitle("Primate College of America");

        clickAndWait(By.cssSelector("a.add-BFO_0000051 > img.add-individual"));

        assertTitle("Edit");
        selectByLabel(By.id("typeOfNew"), "Library (vivo)");

        clickAndWait(By.id("offerCreate"));

        assertTitle("Edit");
        type(By.id("label"), "Primate History Library");

        clickAndWait(By.id("submit"));
        assertTitle("Primate College of America");

        clickAndWait(By.cssSelector("#relatedBy-Position > a.add-relatedBy > img.add-individual"));

        assertTitle("Edit");
        type(By.id("positionTitle"), "Dr.");

        selectByLabel(By.id("positionType"), "Faculty Administrative Position");

        type(By.id("person"), "Person");
        type(By.id("firstName"), "Polly");
        type(By.id("startField-year"), "1999");

        clickAndWait(By.id("submit"));
        assertTitle("Primate College of America");

        clickAndWait(By.cssSelector("a.add-RO_0000053 > img.add-individual"));

        assertTitle("Edit");
        type(By.id("typeSelector"), "Company");
        type(By.id("activity"), "Primates-r-us");
        type(By.id("roleLabel"), "Founder");
        type(By.id("startField-year"), "2010");

        clickAndWait(By.id("submit"));
        assertTitle("Primate College of America");

        clickAndWait(By.cssSelector("a.add-affiliatedOrganization > img.add-individual"));

        assertTitle("Edit");
        selectByLabel(By.id("objectVar"), "Primates-r-us (Company)");

        clickAndWait(By.id("submit"));
        assertTitle("Primate College of America");

        clickAndWait(By.xpath("(//img[@alt='add'])[13]"));
        assertTitle("Edit");

        selectByLabel(By.id("typeSelector"), "Consortium");
        type(By.id("activity"), "Primate Colleges of the World");
        type(By.id("roleLabel"), "Member");
        type(By.id("startField-year"), "2009");

        clickAndWait(By.id("submit"));
        assertTitle("Primate College of America");

        clickAndWait(By.cssSelector("a.add-hasCollaborator > img.add-individual"));
        assertTitle("Edit");

        vivoAutoCompleteSelect(By.id("object"), "Primate His", Keys.ARROW_DOWN);

        clickAndWait(By.id("submit"));
        assertTitle("Primate College of America");

        clickAndWait(By.xpath("(//img[@alt='add'])[14]"));
        assertTitle("Edit");

        type(By.id("typeSelector"), "Service");
        type(By.id("activity"), "Primate Heart Health");
        type(By.id("roleLabel"), "Founder");
        type(By.id("startField-year"), "2010");

        clickAndWait(By.id("submit"));
        assertTitle("Primate College of America");

        clickAndWait(By.xpath("//div[@id='wrapper-content']/ul/li[6]"));
        clickAndWait(By.cssSelector("a.add-publisherOf > img.add-individual"));
        assertTitle("Edit");

//        type(By.id("typeOfNew"), "Database (vivo)");
        selectByLabel(By.id("typeOfNew"), "Database (vivo)");
        clickAndWait(By.id("offerCreate"));
        assertTitle("Edit");

        type(By.id("label"), "Primate Info");

        clickAndWait(By.id("submit"));
        assertTitle("Primate College of America");

        clickAndWait(By.cssSelector("#publicationsGroup > article.property > #RO_0000053 > a.add-RO_0000053 > img.add-individual"));
        assertTitle("Edit");

        type(By.id("typeSelector"), "Invited Talk");
        type(By.id("presentation"), "Primate Health and Fitness");
        type(By.id("roleLabel"), "Organizer");
        type(By.id("startField-year"), "2008");

        clickAndWait(By.cssSelector("input.submit"));
        assertTitle("Primate College of America");

        clickAndWait(By.xpath("//div[@id='wrapper-content']/ul/li[10]"));
        clickAndWait(By.cssSelector("#serviceGroup > article.property > #RO_0000053 > a.add-RO_0000053 > img.add-individual"));
        assertTitle("Edit");

        type(By.id("typeSelector"), "Event");
        type(By.id("activity"), "Primate Health Check");
        type(By.id("roleLabel"), "Sponsor");
        type(By.id("startField-year"), "2008");
        type(By.id("endField-year"), "2010");

        clickAndWait(By.id("submit"));
        assertTitle("Primate College of America");

        clickAndWait(By.xpath("//div[@id='wrapper-content']/ul/li[12]"));
        clickAndWait(By.cssSelector("a.add-RO_0001025 > img.add-individual"));
        assertTitle("Edit");

        vivoAutoCompleteSelect(By.id("object"), "northern Afr", Keys.ARROW_DOWN);

        clickAndWait(By.id("submit"));
        assertTitle("Primate College of America");

        clickAndWait(By.xpath("(//img[@alt='add'])[34]"));
        assertTitle("Edit");

        type(By.id("emailAddress"), "info@primates.edu");

        clickAndWait(By.id("submit"));
        assertTitle("Primate College of America");

        clickAndWait(By.cssSelector("article.property > #ARG_2000028 > a.add-ARG_2000028 > img.add-individual"));
        assertTitle("Edit");

        type(By.id("telephoneNumber"), "555-555-5555");

        clickAndWait(By.id("submit"));
        assertTitle("Primate College of America");

        clickAndWait(By.xpath("(//h3[@id='ARG_2000028']/a)[2]"));
        assertTitle("Edit");

        type(By.id("telephoneNumber"), "555-555-5554");

        clickAndWait(By.id("submit"));
        assertTitle("Primate College of America");

        clickAndWait(By.xpath("(//img[@alt='add'])[35]"));
        assertTitle("Edit");

        type(By.id("streetAddressOne"), "1234 Northern African Nation");
        type(By.id("city"), "Morocco City");
        type(By.id("postalCode"), "1234567890");
        type(By.id("countryEditMode"), "Morocco");

        clickAndWait(By.id("submit"));
        assertTitle("Primate College of America");

        clickAndWait(By.cssSelector("li.nonSelectedGroupTab.clickable"));
        clickAndWait(By.cssSelector("a.add-dateTimeInterval > img.add-individual"));
        assertTitle("Edit");

        type(By.id("startField-year"), "1959");

        clickAndWait(By.id("submit"));
        assertTitle("Primate College of America");

        clickAndWait(By.xpath("//h3[@id='abbreviation']/a/img"));
        assertTitle("Edit");

        typeTinyMCE("PCoA");

        clickAndWait(By.id("submit"));
        assertTitle("Primate College of America");

        clickAndWait(By.xpath("//div[@id='wrapper-content']/ul/li[8]"));
        clickAndWait(By.xpath("//h3[@id='freetextKeyword']/a/img"));
        assertTitle("Edit");

        typeTinyMCE("Gorillas");

        clickAndWait(By.id("submit"));
        assertTitle("Primate College of America");

        clickAndWait(By.xpath("//div[@id='wrapper-content']/ul/li[14]"));
        clickAndWait(By.xpath("(//img[@alt='add'])[14]"));
        assertTitle("Edit");

        type(By.id("typeSelector"), "Workshop");
        type(By.id("activity"), "New Primate Students ");
        type(By.id("startField-year"), "2003");
        type(By.id("endField-year"), "2006");

        clickAndWait(By.id("submit"));
        assertTitle("Primate College of America");

        clickAndWait(By.xpath("(//img[@alt='add'])[15]"));
        assertTitle("Edit");

        type(By.id("typeSelector"), "Performance");
        type(By.id("activity"), "Primates in the Wild");
        type(By.id("startField-year"), "1997");

        clickAndWait(By.id("submit"));
        assertTitle("Primate College of America");

        clickAndWait(By.xpath("//div[@id='wrapper-content']/ul/li[6]"));
        clickAndWait(By.cssSelector("a.add-featuredIn > img.add-individual"));
        assertTitle("Edit");

        selectByLabel(By.id("typeOfNew"), "Blog Posting (vivo)");

        clickAndWait(By.id("offerCreate"));
        assertTitle("Edit");

        type(By.id("label"), "Primate Happenings");

        clickAndWait(By.id("submit"));
        assertTitle("Primate College of America");

        clickAndWait(By.cssSelector("a.add-assigneeFor > img.add-individual"));
        assertTitle("Edit");

        clickAndWait(By.id("offerCreate"));
        assertTitle("Edit");

        type(By.id("label"), "USA222333444555");

        clickAndWait(By.id("submit"));
        assertTitle("Primate College of America");

        clickAndWait(By.cssSelector("a.add-translatorOf > img.add-individual"));
        assertTitle("Edit");

        selectByLabel(By.id("objectVar"), "Primate Happenings (Blog Posting)");

        clickAndWait(By.id("submit"));
        assertTitle("Primate College of America");

        clickAndWait(By.xpath("//div[@id='wrapper-content']/ul/li[8]"));
        clickAndWait(By.cssSelector("#researchGroup > article.property > #RO_0000053 > a.add-RO_0000053 > img.add-individual"));
        assertTitle("Edit");

        vivoAutoCompleteSelect(By.id("grant"), "primate hab", Keys.ARROW_DOWN);

        clickAndWait(By.cssSelector("input.submit"));
        assertTitle("Primate College of America");

        clickAndWait(By.cssSelector("a.add-ERO_0001520 > img.add-individual"));
        assertTitle("Edit");

        clickAndWait(By.id("offerCreate"));
        assertTitle("Edit");

        type(By.id("label"), "Human and Ape Brain Comparison");

        clickAndWait(By.id("submit"));
        assertTitle("Primate College of America");

        clickAndWait(By.xpath("//div[@id='wrapper-content']/ul/li[10]"));
        clickAndWait(By.cssSelector("a.add-ERO_0000037 > img.add-individual"));
        assertTitle("Edit");

        selectByLabel(By.id("typeOfNew"), "Transport Service (obo)");

        clickAndWait(By.id("offerCreate"));
        assertTitle("Edit");

        type(By.id("label"), "Gorilla Moving Company");

        clickAndWait(By.id("submit"));
        assertTitle("Primate College of America");

        clickAndWait(By.cssSelector("#serviceGroup > article.property > #offers > a.add-offers > img.add-individual"));
        assertTitle("Edit");

        clickAndWait(By.id("offerCreate"));
        assertTitle("Edit");

        type(By.id("label"), "Introduction to Primates");

        clickAndWait(By.id("submit"));
        assertTitle("Primate College of America");

        clickAndWait(By.xpath("//div[@id='wrapper-content']/ul/li[12]"));
        clickAndWait(By.cssSelector("a.add-hasSuccessorOrganization > img.add-individual"));
        assertTitle("Edit");

        selectByLabel(By.id("typeOfNew"), "University (vivo)");

        clickAndWait(By.id("offerCreate"));
        assertTitle("Edit");

        type(By.id("label"), "Primate University of America");

        clickAndWait(By.id("submit"));
        assertTitle("Primate College of America");

        clickAndWait(By.cssSelector("a.add-governingAuthorityFor > img.add-individual"));
        assertTitle("Edit");

        vivoAutoCompleteSelect(By.id("object"), "primate colleges of the wor", Keys.ARROW_DOWN);

        clickAndWait(By.id("submit"));
        assertTitle("Primate College of America");

        // Verify everything entered is displaying properly

        clickAndWait(By.xpath("//div[@id='wrapper-content']/ul/li[2]"));
        verifyTextPresent(
                "PCoA",
                "1959 -"
        );
        verifyElementPresent(By.linkText("B.S. Bachelor of Science"));
        verifyElementPresent(By.linkText("Primate Student of the Year"));
        verifyElementPresent(By.linkText("Best Primate College"));

        clickAndWait(By.xpath("//div[@id='wrapper-content']/ul/li[4]"));
        verifyTextPresent(
                "faculty administrative position",
                "Person, Polly, Dr. 1999 -",
                "Primates-r-us Founder 2010 -",
                "Primate Colleges of the World Member 2009 -",
                "Primate Heart Health Founder 2010 -",
                "New Primate Students 2003 - 2006",
                "Primates in the Wild 1997 -"
        );
        verifyElementPresent(By.linkText("Person, Polly"));
        verifyElementPresent(By.linkText("Primate History Library"));
        verifyElementPresent(By.linkText("Primate Research Laboratory"));
        verifyElementPresent(By.linkText("Primates-r-us"));
        verifyElementPresent(By.linkText("Primate Colleges of the World"));
        verifyElementPresent(By.linkText("Primate Heart Health"));
        verifyElementPresent(By.linkText("New Primate Students"));
        verifyElementPresent(By.linkText("Primates in the Wild"));

        clickAndWait(By.xpath("//div[@id='wrapper-content']/ul/li[6]"));
        verifyTextPresent("invited talk", "Primate Health and Fitness, Organizer 2008");
        verifyElementPresent(By.linkText("Primate Info"));
        verifyElementPresent(By.linkText("Primate Health and Fitness"));
        verifyElementPresent(By.linkText("Primate Happenings"));
        verifyElementPresent(By.linkText("USA222333444555"));

        clickAndWait(By.xpath("//div[@id='wrapper-content']/ul/li[8]"));
        verifyElementPresent(By.linkText("Primate Habitat Research Grant"));
        verifyElementPresent(By.linkText("Primate Survival Planning Grant"));
        verifyElementPresent(By.linkText("Human and Ape Brain Comparison"));
        verifyTextPresent("Gorillas");

        clickAndWait(By.xpath("//div[@id='wrapper-content']/ul/li[10]"));
        verifyTextPresent("Primate Health Check Sponsor 2008 - 2010");
        verifyElementPresent(By.linkText("Gorilla Moving Company"));
        verifyElementPresent(By.linkText("Primate Health Check"));
        verifyElementPresent(By.linkText("Portable Primate Habitat"));
        verifyElementPresent(By.linkText("Introduction to Primates"));

        clickAndWait(By.xpath("//div[@id='wrapper-content']/ul/li[12]"));
        verifyTextPresent(
                "555-555-5555",
                "555-555-5554",
                "info@primates.edu",
                "1234 Northern African Nation",
                "Morocco City",
                "1234567890",
                "Morocco");
        verifyElementPresent(By.linkText("northern Africa"));
        verifyElementPresent(By.linkText("Primate College of New York"));
        verifyElementPresent(By.linkText("Primate University of America"));
        verifyElementPresent(By.linkText("Primate Colleges of the World"));

        clickAndWait(By.xpath("//div[@id='wrapper-content']/ul/li[14]"));
        verifyTextPresent(
                "PCoA",
                "1959 -",
                "faculty administrative position",
                "Person, Polly, Dr. 1999 -",
                "Primates-r-us Founder 2010 -",
                "Primate Colleges of the World Member 2009 -",
                "Primate Heart Health Founder 2010 -",
                "New Primate Students 2003 - 2006",
                "Primates in the Wild 1997 -",
                "invited talk",
                "Primate Health and Fitness, Organizer 2008",
                "Gorillas",
                "Primate Health Check Sponsor 2008 - 2010",
                "555-555-5555",
                "555-555-5554",
                "info@primates.edu",
                "1234 Northern African Nation",
                "Morocco City",
                "1234567890",
                "Morocco"
        );
        verifyElementPresent(By.linkText("B.S. Bachelor of Science"));
        verifyElementPresent(By.linkText("Primate Student of the Year"));
        verifyElementPresent(By.linkText("Best Primate College"));
        verifyElementPresent(By.linkText("Person, Polly"));
        verifyElementPresent(By.linkText("Primate History Library"));
        verifyElementPresent(By.linkText("Primate Research Laboratory"));
        verifyElementPresent(By.linkText("Primates-r-us"));
        verifyElementPresent(By.linkText("Primate Colleges of the World"));
        verifyElementPresent(By.linkText("Primate Heart Health"));
        verifyElementPresent(By.linkText("New Primate Students"));
        verifyElementPresent(By.linkText("Primates in the Wild"));
        verifyElementPresent(By.linkText("Primate Info"));
        verifyElementPresent(By.linkText("Primate Health and Fitness"));
        verifyElementPresent(By.linkText("Primate Happenings"));
        verifyElementPresent(By.linkText("USA222333444555"));
        verifyElementPresent(By.linkText("Primate Habitat Research Grant"));
        verifyElementPresent(By.linkText("Primate Survival Planning Grant"));
        verifyElementPresent(By.linkText("Human and Ape Brain Comparison"));
        verifyElementPresent(By.linkText("Gorilla Moving Company"));
        verifyElementPresent(By.linkText("Primate Health Check"));
        verifyElementPresent(By.linkText("Portable Primate Habitat"));
        verifyElementPresent(By.linkText("Introduction to Primates"));
        verifyElementPresent(By.linkText("northern Africa"));
        verifyElementPresent(By.linkText("Primate College of New York"));
        verifyElementPresent(By.linkText("Primate University of America"));
        verifyElementPresent(By.linkText("Primate Colleges of the World"));
    }
}
