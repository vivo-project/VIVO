package org.vivoweb.vivo.selenium.tests;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import static org.vivoweb.vivo.selenium.VIVOAppTester.*;

public class RebuildSearchIndex {
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
    public void rebuildSearchIndexTest() {
        clickAndWait(By.linkText("Site Admin"));                // clickAndWait,link=Site Admin
        assertTitle("VIVO Site Administration");                // assertTitle,VIVO Site Administration

        clickAndWait(By.linkText("Rebuild search index"));      // clickAndWait,link=Rebuild search index
        assertTitle("Rebuild Search Index");                    // assertTitle,Rebuild Search Index

        waitForTextPresent("Reset the search index and re-populate it.");       // waitForTextPresent,Reset the search index and re-populate it.

        clickAndWait(By.name("rebuild"));                       // clickAndWait,name=rebuild
        assertTitle("Rebuild Search Index");                    // assertTitle, Rebuild Search Index

        waitForTextPresent("Reset the search index and re-populate it.");       // waitForTextPresent,Reset the search index and re-populate it.
    }
}
