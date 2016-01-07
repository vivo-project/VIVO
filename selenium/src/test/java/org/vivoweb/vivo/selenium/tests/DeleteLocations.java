package org.vivoweb.vivo.selenium.tests;

import org.junit.Test;
import org.openqa.selenium.By;

public class DeleteLocations extends AbstractVIVOSeleniumTest {
    @Test
    public void deleteLocations() {
        deleteAllVisibleCookies();

        open("/");
        assertTitle("VIVO");

        vivoLogIn("testAdmin@cornell.edu", "Password");

        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        vivoLogOut();
    }
}
/*
<!--Delete Locations-->
<tr>
	<td>clickAndWait</td>
	<td>link=Building</td>
	<td></td>
</tr>
<tr>
	<td>assertTitle</td>
	<td>Building</td>
	<td></td>
</tr>
<tr>
	<td>clickAndWait</td>
	<td>link=Jane Memorial Building</td>
	<td></td>
</tr>
<tr>
	<td>assertTitle</td>
	<td>Jane Memorial Building</td>
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
	<td>//input[@value=&quot;Edit This Individual&quot;]</td>
	<td></td>
</tr>
<tr>
	<td>assertTitle</td>
	<td>Individual Editing Form</td>
	<td></td>
</tr>
<tr>
	<td>clickAndWait</td>
	<td>_delete</td>
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
	<td>clickAndWait</td>
	<td>link=Building</td>
	<td></td>
</tr>
<tr>
	<td>assertTitle</td>
	<td>Building</td>
	<td></td>
</tr>
<tr>
	<td>clickAndWait</td>
	<td>link=Primate Memorial Building</td>
	<td></td>
</tr>
<tr>
	<td>assertTitle</td>
	<td>Primate Memorial Building</td>
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
	<td>link=Facility</td>
	<td></td>
</tr>
<tr>
	<td>assertTitle</td>
	<td>Facility</td>
	<td></td>
</tr>
<tr>
	<td>clickAndWait</td>
	<td>link=Lab Admin Office</td>
	<td></td>
</tr>
<tr>
	<td>assertTitle</td>
	<td>Lab Admin Office</td>
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
	<td>link=Facility</td>
	<td></td>
</tr>
<tr>
	<td>assertTitle</td>
	<td>Facility</td>
	<td></td>
</tr>
<tr>
	<td>clickAndWait</td>
	<td>link=Primate Research Lab Room 123</td>
	<td></td>
</tr>
<tr>
	<td>assertTitle</td>
	<td>Primate Research Lab Room 123</td>
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
	<td>link=Facility</td>
	<td></td>
</tr>
<tr>
	<td>assertTitle</td>
	<td>Facility</td>
	<td></td>
</tr>
<tr>
	<td>clickAndWait</td>
	<td>link=State Fair Park</td>
	<td></td>
</tr>
<tr>
	<td>assertTitle</td>
	<td>State Fair Park</td>
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