package com.homtest.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/** Validation-only runner: checks every step is defined without a device. */
@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"com.homtest.steps"},
        dryRun = true,
        plugin = {"pretty"},
        monochrome = true
)
public class DryRunTestRunner {
}
