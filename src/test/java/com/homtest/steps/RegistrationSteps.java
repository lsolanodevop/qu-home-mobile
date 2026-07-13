package com.homtest.steps;

import com.homtest.pages.LoginPage;
import com.homtest.pages.RegistrationPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.Assert.assertTrue;

public class RegistrationSteps {

    private RegistrationPage registrationPage;

    @When("I navigate to the registration screen")
    public void i_navigate_to_the_registration_screen() {
        new LoginPage().goToRegistration();
        registrationPage = new RegistrationPage();
        assertTrue("Registration screen was not displayed",
                registrationPage.isLoaded());
    }

    @And("I complete the sign-up form with valid data")
    public void i_complete_the_sign_up_form_with_valid_data() {
        // Use a unique email so the scenario is repeatable.
        String uniqueEmail = "newuser" + System.currentTimeMillis() + "@email.com";
        registrationPage.completeSignupForm(uniqueEmail, "John", "Tester", "Password123");
    }

    @And("I interact with the selection component")
    public void i_interact_with_the_selection_component() {
        registrationPage.interactWithSelectionComponents();
    }

    @And("I submit the registration form")
    public void i_submit_the_registration_form() {
        registrationPage.submit();
    }

    @Then("I should reach the registration success screen")
    public void i_should_reach_the_registration_success_screen() {
        assertTrue("Registration success screen was not reached",
                registrationPage.isRegistrationSuccessful());
    }
}
