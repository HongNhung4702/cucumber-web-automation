package com.hongnhung.cucumberweb.pages;

import com.hongnhung.cucumberweb.config.ConfigManager;
import com.hongnhung.cucumberweb.driver.DriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public abstract class BasePage {

    private final DriverManager driverManager;

    protected BasePage(DriverManager driverManager) {
        this.driverManager = driverManager;
    }

    protected WebDriver driver() {
        return driverManager.getDriver();
    }

    protected WebDriverWait waitDriver() {
        return new WebDriverWait(
                driver(),
                Duration.ofSeconds(
                        ConfigManager.getInt("explicit.wait.seconds")
                )
        );
    }

    protected WebElement waitUntilVisible(By locator) {

        return waitDriver().until(webDriver -> {

            List<WebElement> elements =
                    webDriver.findElements(locator);

            for (WebElement element : elements) {

                try {

                    if (element.isDisplayed()) {
                        return element;
                    }

                } catch (StaleElementReferenceException ignored) {
                    // DOM có thể được React/MUI render lại.
                    // WebDriverWait sẽ thử lại ở lần polling tiếp theo.
                }
            }

            return null;
        });
    }

    protected WebElement waitUntilClickable(By locator) {
        return waitDriver().until(
                ExpectedConditions.elementToBeClickable(locator)
        );
    }

    protected void click(By locator) {
        waitUntilClickable(locator).click();
    }

    protected void type(By locator, String value) {

        WebElement element =
                waitUntilVisible(locator);

        element.clear();
        element.sendKeys(value);
    }

    protected String getText(By locator) {
        return waitUntilVisible(locator).getText();
    }

    protected boolean isDisplayed(By locator) {
        return waitUntilVisible(locator).isDisplayed();
    }

    protected void navigateTo(String url) {
        driver().navigate().to(url);
    }

    protected String getCurrentUrl() {
        return driver().getCurrentUrl();
    }

    protected boolean currentUrlContains(String value) {

        return getCurrentUrl()
                .toLowerCase()
                .contains(
                        value.toLowerCase()
                );
    }

    protected boolean waitUntilUrlContains(String value) {

        return waitDriver().until(
                webDriver ->
                        webDriver
                                .getCurrentUrl()
                                .toLowerCase()
                                .contains(
                                        value.toLowerCase()
                                )
        );
    }
}