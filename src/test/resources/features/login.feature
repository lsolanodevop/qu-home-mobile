@login
Feature: User Authentication
  As a registered user of the Art Gallery app
  I want to log in with my credentials
  So that I can access the art catalog

  Background:
    Given the app is launched on the login screen

  @smoke @happy-path @scenario1
  Scenario: Successful login with valid credentials
    When I log in with valid credentials
    Then I should be taken to the Art Gallery catalog

  @validation @scenario2
  Scenario Outline: Login is blocked with invalid or missing input
    When I log in with email "<email>" and password "<password>"
    Then I should remain on the login screen
    And an error message should be displayed

    Examples:
      | email               | password |
      |                     |          |
      | johndoe@email.com   |          |
      |                     | 123      |
      | wrong@email.com     | badpass  |
      | not-an-email        | 123      |
