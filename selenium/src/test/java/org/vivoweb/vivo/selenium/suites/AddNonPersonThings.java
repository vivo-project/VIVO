package org.vivoweb.vivo.selenium.suites;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;
import org.vivoweb.vivo.selenium.DriverFactory;
import org.vivoweb.vivo.selenium.VIVOSuite;
import org.vivoweb.vivo.selenium.tests.CreateCourses;
import org.vivoweb.vivo.selenium.tests.CreateOrganization;
import org.vivoweb.vivo.selenium.tests.DeleteActivities;
import org.vivoweb.vivo.selenium.tests.DeleteCourses;
import org.vivoweb.vivo.selenium.tests.DeleteEquipment;
import org.vivoweb.vivo.selenium.tests.DeleteEvents;
import org.vivoweb.vivo.selenium.tests.DeleteOrganization;
import org.vivoweb.vivo.selenium.tests.DeleteResearch;
import org.vivoweb.vivo.selenium.tests.RebuildSearchIndex;

@RunWith(VIVOSuite.class)
@SuiteClasses(
        {
                RebuildSearchIndex.class,
                CreateOrganization.class,
                CreateCourses.class,
                DeleteActivities.class,
                DeleteCourses.class,
                DeleteEvents.class,
                DeleteResearch.class,
                DeleteEquipment.class,
                DeleteOrganization.class
        }
)
public class AddNonPersonThings {
    @BeforeClass
    public static void setup() {
        DriverFactory.setCloseToken(AddNonPersonThings.class);
    }

    @AfterClass
    public static void shutdown() {
        DriverFactory.close(AddNonPersonThings.class);
    }
}
