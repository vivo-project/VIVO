package org.vivoweb.vivo.selenium.tests;

import org.junit.Test;
import org.openqa.selenium.By;

public class DeleteOrganization extends AbstractSeleniumTest {
    @Test
    public void deleteOrganization() {
        deleteAllVisibleCookies();

        open("/");
        assertTitle("VIVO");

        logIn("testAdmin@cornell.edu", "Password");

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Organization"));
        assertTitle("Organization");

        clickAndWait(By.linkText("Primate College of America"));
        assertTitle("Primate College of America");

        clickAndWait(By.linkText("Edit this individual"));
        assertTitle("Individual Control Panel");

        clickAndWait(By.xpath("//input[@value='Edit This Individual']"));
        assertTitle("Individual Editing Form");

        clickAndWait(By.name("_delete"));
        assertConfirmation("Are you SURE you want to delete this individual? If in doubt, CANCEL.");

        assertTitle("VIVO Site Administration");

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Organization"));
        assertTitle("Organization");

        clickAndWait(By.linkText("Primate College of New York"));
        assertTitle("Primate College of New York");

        clickAndWait(By.linkText("Edit this individual"));
        assertTitle("Individual Control Panel");

        clickAndWait(By.xpath("//input[@value='Edit This Individual']"));
        assertTitle("Individual Editing Form");

        clickAndWait(By.name("_delete"));
        assertConfirmation("Are you SURE you want to delete this individual? If in doubt, CANCEL.");

        assertTitle("VIVO Site Administration");

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Organization"));
        assertTitle("Organization");

        clickAndWait(By.linkText("Primate Colleges of the World"));
        assertTitle("Primate Colleges of the World");

        clickAndWait(By.linkText("Edit this individual"));
        assertTitle("Individual Control Panel");

        clickAndWait(By.xpath("//input[@value='Edit This Individual']"));
        assertTitle("Individual Editing Form");

        clickAndWait(By.name("_delete"));
        assertConfirmation("Are you SURE you want to delete this individual? If in doubt, CANCEL.");

        assertTitle("VIVO Site Administration");

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Organization"));
        assertTitle("Organization");

        clickAndWait(By.linkText("Primate History Library"));
        assertTitle("Primate History Library");

        clickAndWait(By.linkText("Edit this individual"));
        assertTitle("Individual Control Panel");

        clickAndWait(By.xpath("//input[@value='Edit This Individual']"));
        assertTitle("Individual Editing Form");

        clickAndWait(By.name("_delete"));
        assertConfirmation("Are you SURE you want to delete this individual? If in doubt, CANCEL.");

        assertTitle("VIVO Site Administration");

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Organization"));
        assertTitle("Organization");

        clickAndWait(By.linkText("Primate Research Laboratory"));
        assertTitle("Primate Research Laboratory");

        clickAndWait(By.linkText("Edit this individual"));
        assertTitle("Individual Control Panel");

        clickAndWait(By.xpath("//input[@value='Edit This Individual']"));
        assertTitle("Individual Editing Form");

        clickAndWait(By.name("_delete"));
        assertConfirmation("Are you SURE you want to delete this individual? If in doubt, CANCEL.");

        assertTitle("VIVO Site Administration");

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Organization"));
        assertTitle("Organization");

        clickAndWait(By.linkText("Primate University of America"));
        assertTitle("Primate University of America");

        clickAndWait(By.linkText("Edit this individual"));
        assertTitle("Individual Control Panel");

        clickAndWait(By.xpath("//input[@value='Edit This Individual']"));
        assertTitle("Individual Editing Form");

        clickAndWait(By.name("_delete"));
        assertConfirmation("Are you SURE you want to delete this individual? If in doubt, CANCEL.");

        assertTitle("VIVO Site Administration");

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText("Organization"));
        assertTitle("Organization");

        clickAndWait(By.linkText("Primates-r-us"));
        assertTitle("Primates-r-us");

        clickAndWait(By.linkText("Edit this individual"));
        assertTitle("Individual Control Panel");

        clickAndWait(By.xpath("//input[@value='Edit This Individual']"));
        assertTitle("Individual Editing Form");

        clickAndWait(By.name("_delete"));
        assertConfirmation("Are you SURE you want to delete this individual? If in doubt, CANCEL.");

        assertTitle("VIVO Site Administration");

        logOut();
    }
}
