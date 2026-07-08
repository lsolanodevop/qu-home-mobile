package com.homtest.support;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

import java.io.File;
import java.net.URL;
import java.time.Duration;

/** Creates and holds one AndroidDriver per test thread. */
public final class DriverManager {

    private static final ThreadLocal<AndroidDriver> DRIVER = new ThreadLocal<>();

    private DriverManager() {
    }

    public static AndroidDriver getDriver() {
        return DRIVER.get();
    }

    public static void startDriver() {
        if (DRIVER.get() != null) {
            return;
        }
        try {
            UiAutomator2Options options = new UiAutomator2Options()
                    .setPlatformName(ConfigReader.get("platformName", "Android"))
                    .setAutomationName(ConfigReader.get("automationName", "UiAutomator2"))
                    .setDeviceName(ConfigReader.get("deviceName", "Android Device"))
                    .setApp(resolveAppPath())
                    .setAutoGrantPermissions(ConfigReader.getBoolean("autoGrantPermissions"))
                    .setNoReset(ConfigReader.getBoolean("noReset"))
                    .setFullReset(ConfigReader.getBoolean("fullReset"))
                    .setNewCommandTimeout(Duration.ofSeconds(
                            Long.parseLong(ConfigReader.get("newCommandTimeout", "120"))));

            String platformVersion = ConfigReader.get("platformVersion");
            if (platformVersion != null && !platformVersion.isBlank()) {
                options.setPlatformVersion(platformVersion);
            }

            URL serverUrl = new URL(ConfigReader.get("appiumServerUrl", "http://127.0.0.1:4723"));
            AndroidDriver driver = new AndroidDriver(serverUrl, options);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
            DRIVER.set(driver);
        } catch (Exception e) {
            throw new RuntimeException("Failed to start AndroidDriver: " + e.getMessage(), e);
        }
    }

    public static void quitDriver() {
        AndroidDriver driver = DRIVER.get();
        if (driver != null) {
            try {
                driver.quit();
            } finally {
                DRIVER.remove();
            }
        }
    }

    private static String resolveAppPath() {
        String configured = ConfigReader.get("appPath", "./apps/app-home-test-mobile.apk");
        File app = new File(configured);
        if (!app.exists()) {
            throw new IllegalStateException(
                    "APK not found at '" + app.getAbsolutePath() + "'. "
                    + "Download app-home-test-mobile.apk from the release page and place it there, "
                    + "or override with -DappPath=/full/path/to.apk");
        }
        return app.getAbsolutePath();
    }
}
