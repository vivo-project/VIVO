package org.vivoweb.vivo.selenium.suites;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;
import org.vivoweb.vivo.selenium.VIVOAppTester;
import org.vivoweb.vivo.selenium.VIVOSuite;
import org.vivoweb.vivo.selenium.tests.CheckBrowseOptions;
import org.vivoweb.vivo.selenium.tests.CheckIndexView;
import org.vivoweb.vivo.selenium.tests.CheckPublicView;
import org.vivoweb.vivo.selenium.tests.CreateActivity;
import org.vivoweb.vivo.selenium.tests.CreateCourses;
import org.vivoweb.vivo.selenium.tests.CreateEquipment;
import org.vivoweb.vivo.selenium.tests.CreateEvent;
import org.vivoweb.vivo.selenium.tests.CreateLocation;
import org.vivoweb.vivo.selenium.tests.CreateOrganization;
import org.vivoweb.vivo.selenium.tests.CreateTopic;
import org.vivoweb.vivo.selenium.tests.DeleteActivities;
import org.vivoweb.vivo.selenium.tests.DeleteCourses;
import org.vivoweb.vivo.selenium.tests.DeleteEquipment;
import org.vivoweb.vivo.selenium.tests.DeleteEvents;
import org.vivoweb.vivo.selenium.tests.DeleteLocations;
import org.vivoweb.vivo.selenium.tests.DeleteOrganization;
import org.vivoweb.vivo.selenium.tests.DeleteResearch;
import org.vivoweb.vivo.selenium.tests.RebuildSearchIndex;
import org.vivoweb.vivo.selenium.tests.TestMenuManagement;
import org.vivoweb.vivo.selenium.tests.VerifyAllThingsSearchable;

@RunWith(VIVOSuite.class)
@SuiteClasses(
        {
                RebuildSearchIndex.class,
                CreateOrganization.class,
                CreateCourses.class,
                CreateActivity.class,
                CreateEvent.class,
                CreateTopic.class,
                CreateEquipment.class,
                CreateLocation.class,
                RebuildSearchIndex.class,
                VerifyAllThingsSearchable.class,
                CheckPublicView.class,
                CheckIndexView.class,
                CheckBrowseOptions.class,
                TestMenuManagement.class,
                DeleteActivities.class,
                DeleteCourses.class,
                DeleteLocations.class,
                DeleteEvents.class,
                DeleteResearch.class,
                DeleteEquipment.class,
                DeleteOrganization.class
        }
)
public class AddNonPersonThings {
    @BeforeClass
    public static void setup() {
        VIVOAppTester.startTests();
    }

    @AfterClass
    public static void shutdown() {
        VIVOAppTester.endTests();
    }
}
