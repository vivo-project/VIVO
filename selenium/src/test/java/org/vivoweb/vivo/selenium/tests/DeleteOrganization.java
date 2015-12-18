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

        clickAndWait(By.linkText("Organization"));
        assertTitle("Organization");

        clickAndWait(By.linkText("Primate College of America"));
        assertTitle("Primate College of America");

        clickAndWait(By.linkText("Edit this individual"));
        assertTitle("Individual Control Panel");

        clickAndWait(By.xpath("//input[@value='Edit This Individual']"));
        assertTitle("Individual Editing Form");

        clickAndWait(By.name("_delete"));

        try { Thread.sleep(50000); } catch (Exception e) { }

        logOut();
    }
}
/*
<tr>
	<td>assertConfirmation</td>
	<td>Are you SURE you want to delete this individual? If in doubt, CANCEL.</td>
	<td></td>
</tr>
<tr>
	<td>waitForPageToLoad</td>
	<td>5000</td>
	<td></td>
</tr>
<tr>
	<td>assertTitle</td>
	<td>VIVO Site Administration</td>
	<td></td>
</tr>
<tr>
	<td>clickAndWait</td>
	<td>link=Index</td>
	<td></td>
</tr>
<tr>
	<td>assertTitle</td>
	<td>Index of Contents</td>
	<td></td>
</tr>
<tr>
	<td>clickAndWait</td>
	<td>link=Organization</td>
	<td></td>
</tr>
<tr>
	<td>assertTitle</td>
	<td>Organization</td>
	<td></td>
</tr>
<tr>
	<td>clickAndWait</td>
	<td>link=Primate College of New York</td>
	<td></td>
</tr>
<tr>
	<td>assertTitle</td>
	<td>Primate College of New York</td>
	<td></td>
</tr>
<tr>
	<td>clickAndWait</td>
	<td>link=Edit this individual</td>
	<td></td>
</tr>
<tr>
	<td>assertTitle</td>
	<td>Individual Control Panel</td>
	<td></td>
</tr>
<tr>
	<td>clickAndWait</td>
	<td>//input[@value='Edit This Individual']</td>
	<td></td>
</tr>
<tr>
	<td>assertTitle</td>
	<td>Individual Editing Form</td>
	<td></td>
</tr>
<tr>
	<td>click</td>
	<td>name=_delete</td>
	<td></td>
</tr>
<tr>
	<td>assertConfirmation</td>
	<td>Are you SURE you want to delete this individual? If in doubt, CANCEL.</td>
	<td></td>
</tr>
<tr>
	<td>waitForPageToLoad</td>
	<td>5000</td>
	<td></td>
</tr>
<tr>
	<td>assertTitle</td>
	<td>VIVO Site Administration</td>
	<td></td>
</tr>
<tr>
	<td>clickAndWait</td>
	<td>link=Index</td>
	<td></td>
</tr>
<tr>
	<td>assertTitle</td>
	<td>Index of Contents</td>
	<td></td>
</tr>
<tr>
	<td>clickAndWait</td>
	<td>link=Organization</td>
	<td></td>
</tr>
<tr>
	<td>assertTitle</td>
	<td>Organization</td>
	<td></td>
</tr>
<tr>
	<td>clickAndWait</td>
	<td>link=Primate Colleges of the World</td>
	<td></td>
</tr>
<tr>
	<td>assertTitle</td>
	<td>Primate Colleges of the World</td>
	<td></td>
</tr>
<tr>
	<td>clickAndWait</td>
	<td>link=Edit this individual</td>
	<td></td>
</tr>
<tr>
	<td>assertTitle</td>
	<td>Individual Control Panel</td>
	<td></td>
</tr>
<tr>
	<td>clickAndWait</td>
	<td>//input[@value='Edit This Individual']</td>
	<td></td>
</tr>
<tr>
	<td>assertTitle</td>
	<td>Individual Editing Form</td>
	<td></td>
</tr>
<tr>
	<td>click</td>
	<td>name=_delete</td>
	<td></td>
</tr>
<tr>
	<td>assertConfirmation</td>
	<td>Are you SURE you want to delete this individual? If in doubt, CANCEL.</td>
	<td></td>
</tr>
<tr>
	<td>waitForPageToLoad</td>
	<td>5000</td>
	<td></td>
</tr>
<tr>
	<td>assertTitle</td>
	<td>VIVO Site Administration</td>
	<td></td>
</tr>
<tr>
	<td>clickAndWait</td>
	<td>link=Index</td>
	<td></td>
</tr>
<tr>
	<td>assertTitle</td>
	<td>Index of Contents</td>
	<td></td>
</tr>
<tr>
	<td>clickAndWait</td>
	<td>link=Organization</td>
	<td></td>
</tr>
<tr>
	<td>assertTitle</td>
	<td>Organization</td>
	<td></td>
</tr>
<tr>
	<td>clickAndWait</td>
	<td>link=Primate History Library</td>
	<td></td>
</tr>
<tr>
	<td>assertTitle</td>
	<td>Primate History Library</td>
	<td></td>
</tr>
<tr>
	<td>clickAndWait</td>
	<td>link=Edit this individual</td>
	<td></td>
</tr>
<tr>
	<td>assertTitle</td>
	<td>Individual Control Panel</td>
	<td></td>
</tr>
<tr>
	<td>clickAndWait</td>
	<td>//input[@value='Edit This Individual']</td>
	<td></td>
</tr>
<tr>
	<td>assertTitle</td>
	<td>Individual Editing Form</td>
	<td></td>
</tr>
<tr>
	<td>click</td>
	<td>name=_delete</td>
	<td></td>
</tr>
<tr>
	<td>assertConfirmation</td>
	<td>Are you SURE you want to delete this individual? If in doubt, CANCEL.</td>
	<td></td>
</tr>
<tr>
	<td>waitForPageToLoad</td>
	<td>5000</td>
	<td></td>
</tr>
<tr>
	<td>assertTitle</td>
	<td>VIVO Site Administration</td>
	<td></td>
</tr>
<tr>
	<td>clickAndWait</td>
	<td>link=Index</td>
	<td></td>
</tr>
<tr>
	<td>assertTitle</td>
	<td>Index of Contents</td>
	<td></td>
</tr>
<tr>
	<td>clickAndWait</td>
	<td>link=Organization</td>
	<td></td>
</tr>
<tr>
	<td>assertTitle</td>
	<td>Organization</td>
	<td></td>
</tr>
<tr>
	<td>clickAndWait</td>
	<td>link=Primate Research Laboratory</td>
	<td></td>
</tr>
<tr>
	<td>assertTitle</td>
	<td>Primate Research Laboratory</td>
	<td></td>
</tr>
<tr>
	<td>clickAndWait</td>
	<td>link=Edit this individual</td>
	<td></td>
</tr>
<tr>
	<td>assertTitle</td>
	<td>Individual Control Panel</td>
	<td></td>
</tr>
<tr>
	<td>clickAndWait</td>
	<td>//input[@value='Edit This Individual']</td>
	<td></td>
</tr>
<tr>
	<td>assertTitle</td>
	<td>Individual Editing Form</td>
	<td></td>
</tr>
<tr>
	<td>click</td>
	<td>name=_delete</td>
	<td></td>
</tr>
<tr>
	<td>assertConfirmation</td>
	<td>Are you SURE you want to delete this individual? If in doubt, CANCEL.</td>
	<td></td>
</tr>
<tr>
	<td>waitForPageToLoad</td>
	<td>5000</td>
	<td></td>
</tr>
<tr>
	<td>assertTitle</td>
	<td>VIVO Site Administration</td>
	<td></td>
</tr>
<tr>
	<td>clickAndWait</td>
	<td>link=Index</td>
	<td></td>
</tr>
<tr>
	<td>assertTitle</td>
	<td>Index of Contents</td>
	<td></td>
</tr>
<tr>
	<td>clickAndWait</td>
	<td>link=Organization</td>
	<td></td>
</tr>
<tr>
	<td>assertTitle</td>
	<td>Organization</td>
	<td></td>
</tr>
<tr>
	<td>clickAndWait</td>
	<td>link=Primate University of America</td>
	<td></td>
</tr>
<tr>
	<td>assertTitle</td>
	<td>Primate University of America</td>
	<td></td>
</tr>
<tr>
	<td>clickAndWait</td>
	<td>link=Edit this individual</td>
	<td></td>
</tr>
<tr>
	<td>assertTitle</td>
	<td>Individual Control Panel</td>
	<td></td>
</tr>
<tr>
	<td>clickAndWait</td>
	<td>//input[@value='Edit This Individual']</td>
	<td></td>
</tr>
<tr>
	<td>assertTitle</td>
	<td>Individual Editing Form</td>
	<td></td>
</tr>
<tr>
	<td>click</td>
	<td>name=_delete</td>
	<td></td>
</tr>
<tr>
	<td>assertConfirmation</td>
	<td>Are you SURE you want to delete this individual? If in doubt, CANCEL.</td>
	<td></td>
</tr>
<tr>
	<td>waitForPageToLoad</td>
	<td>5000</td>
	<td></td>
</tr>
<tr>
	<td>assertTitle</td>
	<td>VIVO Site Administration</td>
	<td></td>
</tr>
<tr>
	<td>clickAndWait</td>
	<td>link=Index</td>
	<td></td>
</tr>
<tr>
	<td>assertTitle</td>
	<td>Index of Contents</td>
	<td></td>
</tr>
<tr>
	<td>clickAndWait</td>
	<td>link=Organization</td>
	<td></td>
</tr>
<tr>
	<td>assertTitle</td>
	<td>Organization</td>
	<td></td>
</tr>
<tr>
	<td>clickAndWait</td>
	<td>link=Primates-r-us</td>
	<td></td>
</tr>
<tr>
	<td>assertTitle</td>
	<td>Primates-r-us</td>
	<td></td>
</tr>
<tr>
	<td>clickAndWait</td>
	<td>link=Edit this individual</td>
	<td></td>
</tr>
<tr>
	<td>assertTitle</td>
	<td>Individual Control Panel</td>
	<td></td>
</tr>
<tr>
	<td>clickAndWait</td>
	<td>//input[@value='Edit This Individual']</td>
	<td></td>
</tr>
<tr>
	<td>assertTitle</td>
	<td>Individual Editing Form</td>
	<td></td>
</tr>
<tr>
	<td>click</td>
	<td>name=_delete</td>
	<td></td>
</tr>
<tr>
	<td>assertConfirmation</td>
	<td>Are you SURE you want to delete this individual? If in doubt, CANCEL.</td>
	<td></td>
</tr>
<tr>
	<td>waitForPageToLoad</td>
	<td>5000</td>
	<td></td>
</tr>
<tr>
	<td>assertTitle</td>
	<td>VIVO Site Administration</td>
	<td></td>
</tr>
<tr>
	<td>clickAndWait</td>
	<td>link=Index</td>
	<td></td>
</tr>
<tr>
	<td>assertTitle</td>
	<td>Index of Contents</td>
	<td></td>
</tr>

 */