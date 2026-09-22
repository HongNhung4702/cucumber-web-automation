package com.hongnhung.cucumberweb.driver;

import com.hongnhung.cucumberweb.config.ConfigManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;
import java.util.Locale;

public final class DriverFactory {

    public DriverFactory() {
    }

    public WebDriver createDriver() {
        String browser = ConfigManager
                .get("browser")
                .toLowerCase(Locale.ROOT);

        WebDriver driver = switch (browser) {
            case "chrome" -> createChromeDriver();
            case "firefox" -> createFirefoxDriver();
            case "edge" -> createEdgeDriver();

            default -> throw new IllegalArgumentException(
                    "Unsupported browser: " + browser
            );
        };

        configureDriver(driver);

        return driver;
    }

    private WebDriver createChromeDriver() {
        ChromeOptions options = new ChromeOptions();

        if (ConfigManager.getBoolean("headless")) {
            options.addArguments(
                    "--headless=new",
                    "--window-size=1920,1080"
            );
        }

        return new ChromeDriver(options);
    }

    private WebDriver createFirefoxDriver() {
        FirefoxOptions options = new FirefoxOptions();

        if (ConfigManager.getBoolean("headless")) {
            options.addArguments("-headless");
        }

        return new FirefoxDriver(options);
    }

    private WebDriver createEdgeDriver() {
        EdgeOptions options = new EdgeOptions();

        if (ConfigManager.getBoolean("headless")) {
            options.addArguments(
                    "--headless=new",
                    "--window-size=1920,1080"
            );
        }

        return new EdgeDriver(options);
    }

    private void configureDriver(WebDriver driver) {
        driver.manage()
                .timeouts()
                .implicitlyWait(Duration.ZERO);

        driver.manage()
                .timeouts()
                .pageLoadTimeout(
                        Duration.ofSeconds(
                                ConfigManager.getInt(
                                        "page.load.timeout.seconds"
                                )
                        )
                );

        if (ConfigManager.getBoolean("window.maximize")
                && !ConfigManager.getBoolean("headless")) {

            driver.manage()
                    .window()
                    .maximize();
        }
    }
}