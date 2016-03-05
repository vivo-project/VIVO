package org.vivoweb.vivo.selenium;

import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.List;

public class VIVOSuite extends Suite {
    public VIVOSuite(Class<?> klass, RunnerBuilder builder) throws InitializationError {
        super(klass, builder);
    }

    public VIVOSuite(RunnerBuilder builder, Class<?>[] classes) throws InitializationError {
        super(builder, classes);
    }

    protected VIVOSuite(Class<?> klass, Class<?>[] suiteClasses) throws InitializationError {
        super(klass, suiteClasses);
    }

    protected VIVOSuite(RunnerBuilder builder, Class<?> klass, Class<?>[] suiteClasses) throws InitializationError {
        super(builder, klass, suiteClasses);
    }

    protected VIVOSuite(Class<?> klass, List<Runner> runners) throws InitializationError {
        super(klass, runners);
    }

    @Override
    protected void runChild(Runner runner, RunNotifier notifier) {
        // Set Driver factory, can run multiple times
        super.runChild(runner, notifier);
    }

}
