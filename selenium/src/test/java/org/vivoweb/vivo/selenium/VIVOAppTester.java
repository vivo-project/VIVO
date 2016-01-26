package org.vivoweb.vivo.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public final class VIVOAppTester extends WebAppTester {
    private static String loggedInAs = null;

    private VIVOAppTester() { }

    public static void vivoAutoCompleteSelect(By by, String text, Keys... keys) {
        WebElement element = driver().findElement(by);

        int count = 0;
        WebElement autoComplete = null;
        while (autoComplete == null) {
            element.sendKeys(text);

            int findElementCount = 0;
            while (autoComplete == null && findElementCount < 5) {
                findElementCount++;
                try {
                    Thread.sleep(250);

                    autoComplete = driver().findElement(By.className("ui-autocomplete"));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (NoSuchElementException nse) {
                    System.out.println("Failure number: " + count);
                }

                if (autoComplete != null && !autoComplete.isDisplayed()) {
                    autoComplete = null;
                }
            }

            if (autoComplete == null) {
                element.clear();
                if (count > 3) {
                    throw new NoSuchElementException("Auto complete is not visible");
                }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
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

        WebElement selected = driver().findElement(By.id("ui-active-menuitem"));
        if (selected != null) {
            selected.click();
        }
    }

    public static void vivoDeleteIndividual(String category, String individual) {
        clickAndWait(By.linkText("Index"));
        assertTitle("Index of Contents");

        clickAndWait(By.linkText(category));
        assertTitle(category);

        WebElement individualLink = null;
        int pageCount = 1;
        do {
            try {
                individualLink = driver().findElement(By.linkText(individual));
            } catch (NoSuchElementException nse) {
            }

            if (individualLink == null) {
                pageCount++;
                try {
                    clickAndWait(By.linkText(Integer.toString(pageCount, 10)));
                } catch (NoSuchElementException nse) {
                    break;
                }

            }
        } while (individualLink == null);

        clickAndWait(By.linkText(individual));
        assertTitle(individual);

        vivoDeleteIndividual();
    }

    public static void vivoDeleteIndividual() {
        clickAndWait(By.linkText("Edit this individual"));
        assertTitle("Individual Control Panel");

        clickAndWait(By.xpath("//input[@value='Edit This Individual']"));
        assertTitle("Individual Editing Form");

        clickAndWait(By.name("_delete"));
        assertConfirmation("Are you SURE you want to delete this individual? If in doubt, CANCEL.");

        assertTitle("VIVO Site Administration");
    }

    public static void vivoLogIn(String email, String password) {
        if (loggedInAs != null) {
            vivoLogOut();
        }

        clickAndWait(By.linkText("Log in"));                    // clickAndWait,link=Log in
        assertTitle("Log in to VIVO");                          // aseertTitle,Log in to VIVO

        type(By.id("loginName"), email);                        // type,id=loginName,testAdmin@cornell.edu
        type(By.id("loginPassword"), password);                 // type,id=loginPassword,Password

        clickAndWait(By.name("loginForm"));                     // clickAndWait,name=loginForm
        assertTitle("VIVO");                                    // assertTitle,VIVO

        loggedInAs = email;
    }

    public static void vivoLogOut() {
        if (loggedInAs != null) {
            Actions actions = new Actions(driver());
            actions.moveToElement(driver().findElement(By.id("user-menu"))).perform();
            driver().findElement(By.linkText("Log out")).click();
            loggedInAs = null;
        }
    }

    public static void startTests() {
        Class token = getCallingClass(VIVOAppTester.class);

        if (token != null) {
            if (DriverFactory.setCloseToken(token)) {
                deleteAllVisibleCookies();
            }
        }

        open("/");
        assertTitle("VIVO");
    }

    public static void endTests() {
        Class token = getCallingClass(VIVOAppTester.class);

        if (token != null) {
            DriverFactory.close(token);
        } else {
            DriverFactory.close();
        }
    }

}
