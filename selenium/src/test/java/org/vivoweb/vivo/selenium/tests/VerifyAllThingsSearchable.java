package org.vivoweb.vivo.selenium.tests;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import static org.vivoweb.vivo.selenium.VIVOAppTester.*;

public class VerifyAllThingsSearchable {
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
    public void verifyAllThingsSearchable() {
        type(By.name("querytext"), "primates");

        clickAndWait(By.xpath("//input[@value='Search']"));

        assertTitle("primates - VIVO Search Results");

        verifyElementPresent(By.linkText("people"));
        verifyElementPresent(By.linkText("activities"));
        verifyElementPresent(By.linkText("courses"));
        verifyElementPresent(By.linkText("events"));
        verifyElementPresent(By.linkText("organizations"));
        verifyElementPresent(By.linkText("equipment"));
        verifyElementPresent(By.linkText("research"));
        verifyElementPresent(By.linkText("locations"));
        verifyElementPresent(By.linkText("Primates in the Wild"));
        verifyElementPresent(By.linkText("Introduction to Primates"));
        verifyElementPresent(By.linkText("Primates-r-us"));
        verifyElementPresent(By.linkText("Primate Happenings"));
        verifyElementPresent(By.linkText("Primate Info"));
        verifyElementPresent(By.linkText("Primate Health"));
        verifyElementPresent(By.linkText("Primate Quad"));
        verifyElementPresent(By.linkText("Primate Feeder"));
        verifyElementPresent(By.linkText("Primate Diet"));
        verifyElementPresent(By.linkText("Primate College of America"));
        verifyElementPresent(By.linkText("Primate Health Talks"));
        verifyElementPresent(By.linkText("Primate Research Laboratory"));
        verifyElementPresent(By.linkText("Portable Primate Habitat"));
        verifyElementPresent(By.linkText("Primate Elderly Care"));
        verifyElementPresent(By.linkText("Introduction to Primate Health"));
        verifyElementPresent(By.linkText("Primate Health and Fitness"));
        verifyElementPresent(By.linkText("Primate University of America"));
        verifyElementPresent(By.linkText("Primate Colleges of the World"));
        verifyElementPresent(By.linkText("Primate Health Check"));
        verifyElementPresent(By.linkText("Primate Health Conference"));
        verifyElementPresent(By.linkText("Primate Heart Health"));
        verifyElementPresent(By.linkText("New Primate Students"));
        verifyElementPresent(By.linkText("Primate Habitat Research Grant"));
        verifyElementPresent(By.linkText("Best Primate College"));
        verifyElementPresent(By.linkText("Primate History Library"));

        verifyTextPresent(
                "Primates in the Wild Performance",
                "Introduction to Primates Course",
                "Primates-r-us Company",
                "Primate Happenings Blog Posting",
                "Primate Info Database",
                "Primate Health Concept",
                "Primate Quad Geographic Location",
                "Primate Feeder Equipment",
                "Primate Diet Concept",
                "Primate College of America College",
                "Primate Health Talks Seminar Series",
                "Primate Research Laboratory Laboratory",
                "Portable Primate Habitat Equipment",
                "Primate Elderly Care Grant",
                "Introduction to Primate Health Course",
                "Primate Health and Fitness Invited Talk",
                "Primate University of America University",
                "Primate Colleges of the World Consortium",
                "Primate Health Check Event",
                "Primate Health Conference Conference",
                "Primate Heart Health Service",
                "New Primate Students Workshop",
                "Primate Habitat Research Grant Grant",
                "Best Primate College Award or Honor",
                "Primate History Library Library"
        );

        clickAndWait(By.linkText("2"));
        assertTitle("primates - VIVO Search Results");

        verifyElementPresent(By.linkText("Primate Memorial Building"));
        verifyElementPresent(By.linkText("Primate Student of the Year"));
        verifyElementPresent(By.linkText("Primate Survival Planning Grant"));
        verifyElementPresent(By.linkText("Primate College of New York"));
        verifyElementPresent(By.linkText("Primate Research Lab Room 123"));
        verifyElementPresent(By.linkText("Animal Health"));
        verifyElementPresent(By.linkText("Ape Health"));
        verifyElementPresent(By.linkText("Elderly Care"));
        verifyElementPresent(By.linkText("Jane Memorial Building"));
        verifyElementPresent(By.linkText("http://primatehealthintro.cornell.edu"));
        verifyElementPresent(By.linkText("Human and Ape Brain Comparison"));
        verifyElementPresent(By.linkText("Person, Polly"));
        verifyElementPresent(By.linkText("Gorilla Moving Company"));
        verifyElementPresent(By.linkText("PHC Proceedings"));
        verifyElementPresent(By.linkText("State Fair Park"));
        verifyElementPresent(By.linkText("USA222333444555"));
        verifyElementPresent(By.linkText("Africa"));
        verifyElementPresent(By.linkText("Kenya"));

        verifyTextPresent(
                "Primate Memorial Building Building",
                "Primate Student of the Year Award or Honor",
                "Primate Survival Planning Grant Grant",
                "Primate College of New York College",
                "Primate Research Lab Room 123 Facility",
                "Animal Health Concept",
                "Ape Health Concept",
                "Elderly Care Concept",
                "http://primatehealthintro.cornell.edu Webpage",
                "Human and Ape Brain Comparison Human Study",
                "Gorilla Moving Company Transport Service",
                "PHC Proceedings Proceedings",
                "USA222333444555 Patent",
                "Africa Continent Transnational Region",
                "Kenya Country"
        );

        clickAndWait(By.linkText("Home"));
        assertTitle("VIVO");

/*
<tr>
	<td>verifyText</td>
	<td>//div[@id='wrapper-content']/div/ul/li[9]/span</td>
	<td>Building</td>
</tr>
<tr>
	<td>verifyText</td>
	<td>//div[@id='wrapper-content']/div/ul/li[12]/span</td>
	<td>Person</td>
</tr>
<tr>
	<td>verifyText</td>
	<td>//div[@id='wrapper-content']/div/ul/li[15]/span</td>
	<td>Facility</td>
</tr>
*/
    }
}
