@registration @bonus
Feature: User Registration Flow
  As a new user
  I want to create an account from the login screen
  So that I can access the Art Gallery app

  Background:
    Given the app is launched on the login screen

  @scenario4
  Scenario: Register a new account successfully
    When I navigate to the registration screen
    And I complete the sign-up form with valid data
    And I interact with the selection component
    And I submit the registration form
    Then I should reach the registration success screen
