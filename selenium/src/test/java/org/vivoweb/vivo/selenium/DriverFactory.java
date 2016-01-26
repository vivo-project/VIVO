package org.vivoweb.vivo.selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

public final class DriverFactory {
    private static WebDriver driver = null;
    private static Object closeToken = null;

    static {
        Runtime.getRuntime().addShutdownHook(new ShutdownHook());
    }

    public static WebDriver getDriver() {
        if (driver == null) {
            driver = new FirefoxDriver();
        }

        return driver;
    }

    public static boolean close() {
        if (closeToken == null && driver != null) {
            driver.quit();
            driver = null;
            return true;
        }

        return false;
    }

    public static boolean close(Object token) {
        if (closeToken == token || (closeToken != null && closeToken.equals(token))) {
            if (driver != null) {
                driver.quit();
                driver = null;
                closeToken = null;
                return true;
            }
        }

        return false;
    }

    public static boolean setCloseToken(Object token) {
        if (closeToken == null) {
            closeToken = token;
            return true;
        }

        return false;
    }

    private static class ShutdownHook extends Thread {
        ShutdownHook() { }

        @Override
        public void run() {
            if (driver != null) {
                driver.quit();
                driver = null;
            }
        }
    }
}
