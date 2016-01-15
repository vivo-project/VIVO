package org.vivoweb.vivo.selenium.tests;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.vivoweb.vivo.selenium.DriverFactory;
import org.vivoweb.vivo.selenium.SeleniumUtils;

public class AbstractVIVOSeleniumTest extends AbstractSeleniumTest {
    protected void vivoAutoCompleteSelect(By by, String text, Keys... keys) {
        WebElement element = driver.findElement(by);

        int count = 0;
        WebElement autoComplete = null;
        while (autoComplete == null) {
            element.sendKeys(text);

            try {
                Thread.sleep(1000);
                autoComplete = driver.findElement(By.className("ui-autocomplete"));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (NoSuchElementException nse) {
                System.out.println("Failure number: " + count);
            }

            if (autoComplete != null && !autoComplete.isDisplayed()) {
                autoComplete = null;
            }

            if (autoComplete == null) {
                element.clear();
                if (count > 5) {
                    throw new NoSuchElementException("Auto complete is not visible");
                }

                count++;
            }
        }

        if (keys != null && keys.length > 0) {
            for (Keys key : keys) {
                element.sendKeys(key);
            }
        } else {
            // If no key presses specified, use default action to select the first entry in the autocomplete
            element.sendKeys(Keys.ARROW_DOWN);
        }

        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        WebElement selected = driver.findElement(By.id("ui-active-menuitem"));
        if (selected != null) {
            selected.click();
        }
    }

    protected void vivoDeleteIndividual(String category, String individual) {
        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText(category));
        assertTitle(category);

        clickAndWait(By.linkText(individual));
        assertTitle(individual);

        vivoDeleteIndividual();
    }

    protected void vivoDeleteIndividual() {
        clickAndWait(By.linkText("Edit this individual"));
        assertTitle("Individual Control Panel");

        clickAndWait(By.xpath("//input[@value='Edit This Individual']"));
        assertTitle("Individual Editing Form");

        clickAndWait(By.name("_delete"));
        assertConfirmation("Are you SURE you want to delete this individual? If in doubt, CANCEL.");

        assertTitle("VIVO Site Administration");
    }

    protected void vivoLogIn(String email, String password) {
        clickAndWait(By.linkText("Log in"));                    // clickAndWait,link=Log in
        assertTitle("Log in to VIVO");                          // aseertTitle,Log in to VIVO

        type(By.id("loginName"), email);                        // type,id=loginName,testAdmin@cornell.edu
        type(By.id("loginPassword"), password);                 // type,id=loginPassword,Password

        clickAndWait(By.name("loginForm"));                     // clickAndWait,name=loginForm
        assertTitle("VIVO");                                    // assertTitle,VIVO
    }

    protected void vivoLogOut() {
        Actions actions = new Actions(driver);
        actions.moveToElement( driver.findElement(By.id("user-menu")) ).perform();
        driver.findElement(By.linkText("Log out")).click();
    }


}
