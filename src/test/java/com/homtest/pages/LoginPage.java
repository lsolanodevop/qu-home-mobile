package com.homtest.pages;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/** Login screen. */
public class LoginPage extends BasePage {

    private final By emailField    = AppiumBy.accessibilityId("emailField");
    private final By passwordField = AppiumBy.accessibilityId("passwordField");
    private final By loginButton   = AppiumBy.accessibilityId("Login");
    private final By registerButton = AppiumBy.accessibilityId("registerButton");

    // Unique to the catalog reached after a successful login.
    private final By catalogList = AppiumBy.accessibilityId("itemsList");

    private final By toast     = AppiumBy.xpath("//android.widget.Toast");
    private final By errorText = AppiumBy.xpath(
            "//*[contains(@text,'Invalid') or contains(@text,'invalid') "
          + "or contains(@text,'incorrect') or contains(@text,'required') "
          + "or contains(@text,'Error') or contains(@text,'wrong')]");

    public void enterEmail(String email) {
        if (email != null && !email.isEmpty()) {
            type(emailField, email);
        }
    }

    public void enterPassword(String password) {
        if (password != null && !password.isEmpty()) {
            type(passwordField, password);
        }
    }

    public void tapLogin() {
        tap(loginButton);
    }

    public void login(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        tapLogin();
    }

    public void goToRegistration() {
        tap(registerButton);
    }

    public boolean waitUntilLoaded() {
        return waitForVisible(emailField, 30);
    }

    public boolean isLoaded() {
        return isDisplayed(emailField) && isDisplayed(passwordField);
    }

    /** True when access was blocked: the catalog was never reached. */
    public boolean accessWasBlocked() {
        return !isPresent(catalogList);
    }

    public boolean isErrorDisplayed() {
        if (isPresent(toast) || isPresent(errorText)) {
            return true;
        }
        return accessWasBlocked();
    }
}
