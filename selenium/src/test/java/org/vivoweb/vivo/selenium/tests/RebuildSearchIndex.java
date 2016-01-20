package org.vivoweb.vivo.selenium.tests;

import org.junit.Test;
import org.openqa.selenium.By;

public class RebuildSearchIndex extends AbstractVIVOSeleniumTest {
    @Test
    public void rebuildSearchIndexTest() {
        deleteAllVisibleCookies();

        open("/");
        assertTitle("VIVO");                                    // assertTitle,VIVO

        vivoLogIn("testAdmin@cornell.edu", "Password");

        clickAndWait(By.linkText("Site Admin"));                // clickAndWait,link=Site Admin
        assertTitle("VIVO Site Administration");                // assertTitle,VIVO Site Administration

        clickAndWait(By.linkText("Rebuild search index"));      // clickAndWait,link=Rebuild search index
        assertTitle("Rebuild Search Index");                    // assertTitle,Rebuild Search Index

        waitForTextPresent("Reset the search index and re-populate it.");       // waitForTextPresent,Reset the search index and re-populate it.

        clickAndWait(By.name("rebuild"));                       // clickAndWait,name=rebuild
        assertTitle("Rebuild Search Index");                    // assertTitle, Rebuild Search Index

        waitForTextPresent("Reset the search index and re-populate it.");       // waitForTextPresent,Reset the search index and re-populate it.

        vivoLogOut();                                               // clickAndWait,Log out
    }
}
