package com.homtest.pages;

import com.homtest.support.DriverManager;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/** Shared helpers for the page objects: waits, typing, tapping and scrolling. */
public abstract class BasePage {

    protected final AndroidDriver driver;
    protected final WebDriverWait wait;

    protected BasePage() {
        this.driver = DriverManager.getDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        this.wait.ignoring(StaleElementReferenceException.class);
    }

    protected WebElement waitVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected void tap(By locator) {
        for (int attempt = 0; attempt < 3; attempt++) {
            try {
                waitClickable(locator).click();
                return;
            } catch (StaleElementReferenceException e) {
                // retry on re-render
            }
        }
        waitClickable(locator).click();
    }

    protected void type(By locator, String text) {
        for (int attempt = 0; attempt < 3; attempt++) {
            try {
                WebElement field = waitVisible(locator);
                field.clear();
                field.sendKeys(text);
                return;
            } catch (StaleElementReferenceException e) {
                // retry on re-render
            }
        }
    }

    protected boolean waitForVisible(By locator, int seconds) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(seconds))
                    .ignoring(StaleElementReferenceException.class)
                    .until(ExpectedConditions.visibilityOfElementLocated(locator));
            return true;
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    protected boolean waitForPresent(By locator, int seconds) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(seconds))
                    .ignoring(StaleElementReferenceException.class)
                    .until(ExpectedConditions.presenceOfElementLocated(locator));
            return true;
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    protected String textOf(By locator) {
        return waitVisible(locator).getText();
    }

    protected boolean isDisplayed(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    protected boolean isPresent(By locator) {
        try {
            return !driver.findElements(locator).isEmpty();
        } catch (StaleElementReferenceException e) {
            return false;
        }
    }

    /** Scrolls the list until an element containing the given text is on screen. */
    protected WebElement scrollToText(String visibleText) {
        String uiSelector = "new UiScrollable(new UiSelector().scrollable(true))"
                + ".scrollIntoView(new UiSelector().textContains(\"" + visibleText + "\"))";
        return driver.findElement(AppiumBy.androidUIAutomator(uiSelector));
    }

    protected List<WebElement> findAll(By locator) {
        return driver.findElements(locator);
    }
}
