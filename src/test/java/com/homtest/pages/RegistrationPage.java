package com.homtest.pages;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/** Two-step registration flow: account details, then personal information. */
public class RegistrationPage extends BasePage {

    // Step 1 - account details
    private final By emailField     = AppiumBy.accessibilityId("emailField");
    private final By firstNameField = AppiumBy.accessibilityId("firstNameField");
    private final By lastNameField  = AppiumBy.accessibilityId("lastNameField");
    private final By passwordField  = AppiumBy.accessibilityId("passwordField");
    private final By continueButton = AppiumBy.accessibilityId("Continue");

    // Step 2 - personal information
    private final By addressInput   = AppiumBy.accessibilityId("addressInput");
    private final By cityInput      = AppiumBy.accessibilityId("cityInput");
    private final By zipInput       = AppiumBy.accessibilityId("zipInput");
    private final By openDatePicker = AppiumBy.accessibilityId("openDatePicker");
    private final By termConditions = AppiumBy.accessibilityId("termConditions");
    private final By signupButton   = AppiumBy.accessibilityId("Signup!");

    private final By datePickerOk = AppiumBy.id("android:id/button1");

    private final By successTitle = AppiumBy.accessibilityId("title");
    private final By successText  = AppiumBy.xpath(
            "//*[contains(@text,'created') or contains(@text,'Congratulations')]");

    public boolean isLoaded() {
        return waitForVisible(firstNameField, 20);
    }

    public void completeSignupForm(String email, String firstName,
                                   String lastName, String password) {
        type(emailField, email);
        type(firstNameField, firstName);
        type(lastNameField, lastName);
        type(passwordField, password);
        tap(continueButton);

        type(addressInput, "123 Main St");
        type(cityInput, "Springfield");
        type(zipInput, "12345");
    }

    public void interactWithSelectionComponents() {
        if (isPresent(openDatePicker)) {
            tap(openDatePicker);
            if (isPresent(datePickerOk)) {
                tap(datePickerOk);
            }
        }
        if (isPresent(termConditions)) {
            tap(termConditions);
        }
    }

    public void submit() {
        tap(signupButton);
    }

    public boolean isRegistrationSuccessful() {
        return waitForVisible(successText, 20) || isDisplayed(successTitle);
    }
}
