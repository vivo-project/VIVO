package org.vivoweb.vivo.selenium;

import com.sun.tools.internal.xjc.Driver;
import org.apache.commons.lang33.StringUtils;
import org.junit.Assert;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WebAppTester {
    private static WebAppTester webAppTester = new WebAppTester();

    public static void assertConfirmation(String text) {
        WebDriverWait wait = new WebDriverWait(driver(), 2);
        wait.until(ExpectedConditions.alertIsPresent());
        Alert alert = driver().switchTo().alert();
        if (!StringUtils.isEmpty(text)) {
            Assert.assertTrue(text.equalsIgnoreCase(alert.getText()));
        }
        alert.accept();
        driver().switchTo().defaultContent();
    }

    public static void assertTitle(String title) {
        Assert.assertEquals(title, driver().getTitle());
    }

    public static void clickAndWait(By by) {
        driver().findElement(by).click();
    }

    public static void deleteAllVisibleCookies() {
        driver().manage().deleteAllCookies();
    }

    public static void open(String urlPart) {
        SeleniumUtils.navigate(driver(), urlPart);
    }

    public static void pause(long timeout) {
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
        }
    }

    public static void selectByLabel(By by, String label) {
        Select select = new Select(driver().findElement(by));
        select.selectByVisibleText(label);

    }

    public static void type(By by, String text) {
        WebElement element = driver().findElement(by);
        element.sendKeys(text);
    }

    public static void typeTinyMCE(String text) {
//        <td> tinyMCE.activeEditor.setContent('The Primate College of America is a privately-funded college for the study of primates.')</td>

        driver().switchTo().frame("literal_ifr");
        WebElement element = driver().findElement(By.cssSelector("body"));
        element.click();
        element.sendKeys(text);
        driver().switchTo().defaultContent();
    }

    public static void verifyElementPresent(By by) {
        Assert.assertNotNull(driver().findElement(by));
    }

    public static void verifyTextPresent(String... text) {
        if (text != null) {
            String bodyText = driver().findElement(By.xpath("//body")).getText();
            for (String str : text) {
                Assert.assertTrue(bodyText.contains(str));
            }
        }
//        Assert.assertNotNull(driver().findElement(xpathForTextPresent(text)));
    }

    public static boolean waitForElementPresent(By by) {
        return waitForElementPresent(by, 30);
    }

    public static boolean waitForElementPresent(By by, int timeout) {
        WebDriverWait wait = new WebDriverWait(driver(), timeout);
        return wait.until(ExpectedConditions.presenceOfElementLocated(by)) != null;
    }

    public static boolean waitForTextPresent(String text) {
        return waitForTextPresent(text, 30);
    }

    public static boolean waitForTextPresent(String text, int timeout) {
        WebDriverWait wait = new WebDriverWait(driver(), timeout);
        return wait.until(ExpectedConditions.presenceOfElementLocated(xpathForTextPresent(text))) != null;
    }

    protected static By xpathForTextPresent(String text) {
        return By.xpath("//*[text()[contains(.,'" + text + "')]]");
    }

    protected static WebDriver driver() {
        return DriverFactory.getDriver();
    }

    public static void startTests() {
        Class token = getCallingClass(WebAppTester.class);

        if (token != null) {
            DriverFactory.setCloseToken(token);
        }
    }

    public static void endTests() {
        Class token = getCallingClass(WebAppTester.class);

        if (token != null) {
            DriverFactory.setCloseToken(token);
        }
    }

    protected static Class getCallingClass(Class thisClass) {
        boolean foundThisClass = false;
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        int idx = 0;
        while (idx < elements.length) {
            if (foundThisClass) {
                if (!thisClass.getCanonicalName().equals(elements[idx].getClassName())) {
                    try {
                        return Class.forName(elements[idx].getClassName());
                    } catch (ClassNotFoundException e) {
                    }
                }
            } else {

            }
            if (thisClass.getCanonicalName().equals(elements[idx].getClassName())) {
                foundThisClass = true;
            }
            idx++;
        }

        return null;
    }
}
