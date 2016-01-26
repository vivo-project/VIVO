package org.vivoweb.vivo.selenium.tests;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import static org.vivoweb.vivo.selenium.VIVOAppTester.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CheckIndexView {
    @BeforeClass
    public static void setUp() {
        startTests();
        vivoLogOut();
    }

    @AfterClass
    public static void tearDown() {
        endTests();
    }

    @Test
    public void checkIndexView() {
        clickAndWait(By.linkText("Index"));

        verifyTextPresent(
                "people",
                "activities",
                "courses",
                "events",
                "organizations",
                "equipment",
                "research",
                "locations",
                "Person (1)",
                "Project (1)",
                "Research Project (1)",
                "Service (2)",
                "Transport Service (1)",
                "Course (2)",
                "Conference (1)",
                "Event (7)",
                "Invited Talk (1)",
                "Performance (1)",
                "Presentation (1)",
                "Seminar Series (1)",
                "Workshop (1)",
                "College (2)",
                "Company (1)",
                "Consortium (1)",
                "Laboratory (1)",
                "Library (1)",
                "Organization (7)",
                "University (1)",
                "Equipment (2)",
                "Article (1)",
                "Award or Honor (2)",
                "Blog Posting (1)",
                "Book (1)",
                "Concept (7)",
                "Database (1)",
                "Grant (3)",
                "Human Study (1)",
                "Patent (1)",
                "Proceedings (1)",
                "Webpage (1)",
                "Building (2)",
                "Continent (7)",
                "Facility (5)",
                "Room (1)"
        );

        verifyElementPresent(By.linkText("Person"));
        verifyElementPresent(By.linkText("Project"));
        verifyElementPresent(By.linkText("Research Project"));
        verifyElementPresent(By.linkText("Service"));
        verifyElementPresent(By.linkText("Transport Service"));
        verifyElementPresent(By.linkText("Course"));
        verifyElementPresent(By.linkText("Conference"));
        verifyElementPresent(By.linkText("Event"));
        verifyElementPresent(By.linkText("Invited Talk"));
        verifyElementPresent(By.linkText("Performance"));
        verifyElementPresent(By.linkText("Presentation"));
        verifyElementPresent(By.linkText("Seminar Series"));
        verifyElementPresent(By.linkText("Workshop"));
        verifyElementPresent(By.linkText("College"));
        verifyElementPresent(By.linkText("Company"));
        verifyElementPresent(By.linkText("Consortium"));
        verifyElementPresent(By.linkText("Laboratory"));
        verifyElementPresent(By.linkText("Library"));
        verifyElementPresent(By.linkText("Organization"));
        verifyElementPresent(By.linkText("University"));
        verifyElementPresent(By.linkText("Equipment"));
        verifyElementPresent(By.linkText("Article"));
        verifyElementPresent(By.linkText("Award or Honor"));
        verifyElementPresent(By.linkText("Blog Posting"));
        verifyElementPresent(By.linkText("Book"));
        verifyElementPresent(By.linkText("Concept"));
        verifyElementPresent(By.linkText("Database"));
        verifyElementPresent(By.linkText("Grant"));
        verifyElementPresent(By.linkText("Human Study"));
        verifyElementPresent(By.linkText("Patent"));
        verifyElementPresent(By.linkText("Proceedings"));
        verifyElementPresent(By.linkText("Webpage"));
        verifyElementPresent(By.linkText("Building"));
        verifyElementPresent(By.linkText("Facility"));
        verifyElementPresent(By.linkText("Room"));

        clickAndWait(By.linkText("Home"));
        assertTitle("VIVO");
    }
}
