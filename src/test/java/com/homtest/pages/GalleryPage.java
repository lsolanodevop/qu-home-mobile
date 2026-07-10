package com.homtest.pages;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/** Catalog view shown after a successful login. */
public class GalleryPage extends BasePage {

    private final By itemsList = AppiumBy.accessibilityId("itemsList");

    public boolean isLoaded() {
        return waitForVisible(itemsList, 20);
    }

    public boolean scrollToItem(String itemName) {
        WebElement item = scrollToText(itemName);
        return item != null && item.isDisplayed();
    }

    public void openItem(String itemName) {
        scrollToText(itemName).click();
    }

    public boolean isItemVisible(String itemName) {
        return isPresent(By.xpath("//*[contains(@text,'" + itemName + "')]"));
    }
}
