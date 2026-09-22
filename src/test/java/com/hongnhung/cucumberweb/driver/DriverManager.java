package com.hongnhung.cucumberweb.driver;

import org.openqa.selenium.WebDriver;

public final class DriverManager {

    private final DriverFactory driverFactory;
    private WebDriver driver;

    public DriverManager(DriverFactory driverFactory) {
        this.driverFactory = driverFactory;
    }

    public void startDriver() {
        if (driver != null) {
            throw new IllegalStateException(
                    "WebDriver has already been started."
            );
        }

        driver = driverFactory.createDriver();
    }

    public WebDriver getDriver() {
        if (driver == null) {
            throw new IllegalStateException(
                    "WebDriver has not been started."
            );
        }

        return driver;
    }

    public boolean isStarted() {
        return driver != null;
    }

    public void quitDriver() {
        if (driver == null) {
            return;
        }

        try {
            driver.quit();
        } finally {
            driver = null;
        }
    }
}