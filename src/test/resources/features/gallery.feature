@gallery
Feature: Catalog Exploration
  As an authenticated user
  I want to browse the art gallery feed
  So that I can find a specific art piece deep in the catalog

  Background:
    Given the app is launched on the login screen
    And I am logged in with valid credentials

  @scenario3
  Scenario: Locate an art piece deep in the scrollable catalog
    When I scroll the catalog to the art piece "Twilight Glow"
    Then the art piece "Twilight Glow" should be visible
