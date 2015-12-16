package org.vivoweb.vivo.selenium.tests;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.vivoweb.vivo.selenium.DriverFactory;
import org.vivoweb.vivo.selenium.SeleniumUtils;

public class AbstractSeleniumTest {
    protected WebDriver driver;

    protected void assertTitle(String title) {
        Assert.assertEquals(title, driver.getTitle());
    }

    protected void clickAndWait(By by) {
        driver.findElement(by).click();
    }

    protected void deleteAllVisibleCookies() {
        driver.manage().deleteAllCookies();
    }

    protected void logIn(String email, String password) {
        clickAndWait(By.linkText("Log in"));                    // clickAndWait,link=Log in
        assertTitle("Log in to VIVO");                          // aseertTitle,Log in to VIVO

        type(By.id("loginName"), email);                        // type,id=loginName,testAdmin@cornell.edu
        type(By.id("loginPassword"), password);                 // type,id=loginPassword,Password

        clickAndWait(By.name("loginForm"));                     // clickAndWait,name=loginForm
        assertTitle("VIVO");                                    // assertTitle,VIVO
    }

    protected void logOut() {
        Actions actions = new Actions(driver);
        actions.moveToElement( driver.findElement(By.id("user-menu")) ).perform();
        driver.findElement(By.linkText("Log out")).click();
    }

    protected void open(String urlPart) {
        SeleniumUtils.navigate(driver, urlPart);
    }

    protected void selectByLabel(By by, String label) {
        Select select = new Select(driver.findElement(by));
        select.selectByVisibleText(label);

    }

    protected void type(By by, String text) {
        driver.findElement(by).sendKeys(text);
    }

    protected void typeTinyMCE(String text) {
//        <td> tinyMCE.activeEditor.setContent('The Primate College of America is a privately-funded college for the study of primates.')</td>

        driver.switchTo().frame("literal_ifr");
        WebElement element = driver.findElement(By.cssSelector("body"));
        element.click();
        element.sendKeys(text);
        driver.switchTo().defaultContent();
    }

    protected void verifyTextPresent(String text) {
        Assert.assertNotNull(driver.findElement(xpathForTextPresent(text)));
    }

    protected boolean waitForElementPresent(By by) {
        return waitForElementPresent(by, 30);
    }

    protected boolean waitForElementPresent(By by, int timeout) {
        WebDriverWait wait = new WebDriverWait(driver, timeout);
        return wait.until(ExpectedConditions.presenceOfElementLocated(by)) != null;
    }

    protected boolean waitForTextPresent(String text) {
        return waitForTextPresent(text, 30);
    }

    protected boolean waitForTextPresent(String text, int timeout) {
        WebDriverWait wait = new WebDriverWait(driver, timeout);
        return wait.until(ExpectedConditions.presenceOfElementLocated(xpathForTextPresent(text))) != null;
    }

    protected By xpathForTextPresent(String text) {
        return By.xpath("//*[text()[contains(.,'" + text + "')]]");
    }

    @Before
    public void setup() {
        driver = DriverFactory.getDriver();
    }

    @After
    public void cleanup() {
        DriverFactory.close();
    }
}
