package com.homtest.steps;

import com.homtest.pages.GalleryPage;
import com.homtest.pages.LoginPage;
import com.homtest.support.ConfigReader;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.Assert.assertTrue;

public class LoginSteps {

    private LoginPage loginPage;
    private GalleryPage galleryPage;

    @Given("the app is launched on the login screen")
    public void the_app_is_launched_on_the_login_screen() {
        loginPage = new LoginPage();
        assertTrue("Login screen was not displayed", loginPage.waitUntilLoaded());
    }

    @When("I log in with valid credentials")
    public void i_log_in_with_valid_credentials() {
        loginPage.login(
                ConfigReader.get("validEmail"),
                ConfigReader.get("validPassword"));
    }

    @And("I am logged in with valid credentials")
    public void i_am_logged_in_with_valid_credentials() {
        i_log_in_with_valid_credentials();
    }

    @When("I log in with email {string} and password {string}")
    public void i_log_in_with_email_and_password(String email, String password) {
        loginPage.login(email, password);
    }

    @Then("I should be taken to the Art Gallery catalog")
    public void i_should_be_taken_to_the_art_gallery_catalog() {
        galleryPage = new GalleryPage();
        assertTrue("Art Gallery catalog was not displayed after login",
                galleryPage.isLoaded());
    }

    @Then("I should remain on the login screen")
    public void i_should_remain_on_the_login_screen() {
        // Access must have been blocked: the catalog was never reached.
        assertTrue("Access was not blocked - the catalog was reached",
                loginPage.accessWasBlocked());
    }

    @And("an error message should be displayed")
    public void an_error_message_should_be_displayed() {
        assertTrue("Expected a validation/error message but none was shown",
                loginPage.isErrorDisplayed());
    }
}
