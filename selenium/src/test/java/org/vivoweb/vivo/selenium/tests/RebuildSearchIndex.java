package org.vivoweb.vivo.selenium.tests;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.vivoweb.vivo.selenium.DriverFactory;
import org.vivoweb.vivo.selenium.SeleniumUtils;

public class RebuildSearchIndex extends AbstractSeleniumTest {
    @Test
    public void rebuildSearchIndexTest() {
        deleteAllVisibleCookies();

        open("/");
        assertTitle("VIVO");                                    // assertTitle,VIVO

        logIn("testAdmin@cornell.edu", "Password");

        clickAndWait(By.linkText("Site Admin"));                // clickAndWait,link=Site Admin
        assertTitle("VIVO Site Administration");                // assertTitle,VIVO Site Administration

        clickAndWait(By.linkText("Rebuild search index"));      // clickAndWait,link=Rebuild search index
        assertTitle("Rebuild Search Index");                    // assertTitle,Rebuild Search Index

        clickAndWait(By.name("rebuild"));                       // clickAndWait,name=rebuild
        assertTitle("Rebuild Search Index");                    // assertTitle, Rebuild Search Index

        waitForTextPresent("Reset the search index and re-populate it.");       // waitForTextPresent,Reset the search index and re-populate it.

        logOut();                                               // clickAndWait,Log out
    }
}
