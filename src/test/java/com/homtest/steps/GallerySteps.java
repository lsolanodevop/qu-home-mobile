package com.homtest.steps;

import com.homtest.pages.GalleryPage;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.Assert.assertTrue;

public class GallerySteps {

    private final GalleryPage galleryPage = new GalleryPage();

    @When("I scroll the catalog to the art piece {string}")
    public void i_scroll_the_catalog_to_the_art_piece(String itemName) {
        assertTrue("Could not scroll to the art piece: " + itemName,
                galleryPage.scrollToItem(itemName));
    }

    @Then("the art piece {string} should be visible")
    public void the_art_piece_should_be_visible(String itemName) {
        assertTrue("Art piece '" + itemName + "' is not visible on screen",
                galleryPage.isItemVisible(itemName));
    }

    @When("I open the art piece {string}")
    public void i_open_the_art_piece(String itemName) {
        galleryPage.openItem(itemName);
    }

    @Then("its details should be displayed for {string}")
    public void its_details_should_be_displayed_for(String itemName) {
        assertTrue("Item detail screen was not shown for '" + itemName + "'",
                galleryPage.areDetailsVisible(itemName));
    }
}
