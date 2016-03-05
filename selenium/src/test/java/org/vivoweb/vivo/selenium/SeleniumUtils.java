package org.vivoweb.vivo.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public final class SeleniumUtils {
    private static String baseUrl = "http://localhost:8080/vivo";

    public static void setBaseUrl(String baseUrl) {
        SeleniumUtils.baseUrl = baseUrl;
    }

    public static String makeUrl(String urlPart) {
        if (urlPart.startsWith("/")) {
            return baseUrl + urlPart;
        } else {
            return baseUrl + "/" + urlPart;
        }
    }

    public static void navigate(WebDriver driver, String urlPart) {
        driver.navigate().to(makeUrl(urlPart));
    }
}
