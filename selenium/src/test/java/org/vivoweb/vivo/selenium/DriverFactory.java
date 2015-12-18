package org.vivoweb.vivo.selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

public final class DriverFactory {
    private static WebDriver driver = null;
    private static Object closeToken = null;

    public static WebDriver getDriver() {
        if (driver == null) {
            driver = new FirefoxDriver();
        }

        return driver;
    }

    public static void close() {
        if (closeToken == null && driver != null) {
            driver.quit();
            driver = null;
        }
    }

    public static void close(Object token) {
        if (closeToken == token || (closeToken != null && closeToken.equals(token))) {
            if (driver != null) {
                driver.quit();
                driver = null;
            }
        }
    }

    public static void setCloseToken(Object token) {
        closeToken = token;
    }
}
