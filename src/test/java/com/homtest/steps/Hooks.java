package com.homtest.steps;

import com.homtest.support.DriverManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

/** Starts a fresh driver per scenario and attaches a screenshot on failure. */
public class Hooks {

    @Before
    public void setUp() {
        DriverManager.startDriver();
    }

    @After
    public void tearDown(Scenario scenario) {
        try {
            if (scenario.isFailed() && DriverManager.getDriver() != null) {
                byte[] shot = ((TakesScreenshot) DriverManager.getDriver())
                        .getScreenshotAs(OutputType.BYTES);
                scenario.attach(shot, "image/png", scenario.getName());
            }
        } catch (Exception ignored) {
            // Never let screenshot capture break the teardown.
        } finally {
            DriverManager.quitDriver();
        }
    }
}
