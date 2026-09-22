package com.hongnhung.cucumberweb.hooks;

import com.hongnhung.cucumberweb.config.ConfigManager;
import com.hongnhung.cucumberweb.driver.DriverManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public final class ScenarioHooks {

    private static final Path SCREENSHOT_DIRECTORY =
            Path.of("target", "screenshots");

    private final DriverManager driverManager;

    public ScenarioHooks(DriverManager driverManager) {
        this.driverManager = driverManager;
    }

    @Before
    public void setUp() {
        driverManager.startDriver();
    }

    @After
    public void tearDown(Scenario scenario) {

        try {

            if (shouldCaptureScreenshot(scenario)) {
                captureFailureScreenshot(scenario);
            }

        } finally {

            driverManager.quitDriver();
        }
    }

    private boolean shouldCaptureScreenshot(
            Scenario scenario
    ) {

        return scenario.isFailed()
                && driverManager.isStarted()
                && ConfigManager.getBoolean(
                "screenshot.on.failure"
        );
    }

    private void captureFailureScreenshot(
            Scenario scenario
    ) {

        try {

            byte[] screenshot =
                    ((TakesScreenshot)
                            driverManager.getDriver())
                            .getScreenshotAs(
                                    OutputType.BYTES
                            );

            attachToScenario(
                    scenario,
                    screenshot
            );

            saveScreenshotToFile(
                    scenario,
                    screenshot
            );

        } catch (Exception exception) {

            scenario.log(
                    "Không thể chụp screenshot: "
                            + exception.getMessage()
            );
        }
    }

    private void attachToScenario(
            Scenario scenario,
            byte[] screenshot
    ) {

        scenario.attach(
                screenshot,
                "image/png",
                "Failure Screenshot"
        );
    }

    private void saveScreenshotToFile(
            Scenario scenario,
            byte[] screenshot
    ) throws IOException {

        Files.createDirectories(
                SCREENSHOT_DIRECTORY
        );

        String scenarioName =
                sanitizeFileName(
                        scenario.getName()
                );

        String fileName =
                scenarioName
                        + "-"
                        + UUID.randomUUID()
                        + ".png";

        Path screenshotPath =
                SCREENSHOT_DIRECTORY.resolve(
                        fileName
                );

        Files.write(
                screenshotPath,
                screenshot
        );

        scenario.log(
                "Failure screenshot: "
                        + screenshotPath
                        .toAbsolutePath()
        );
    }

    private String sanitizeFileName(
            String value
    ) {

        String sanitized =
                value.replaceAll(
                        "[^\\p{L}\\p{N}._-]+",
                        "_"
                );

        if (sanitized.length() > 80) {
            sanitized =
                    sanitized.substring(
                            0,
                            80
                    );
        }

        return sanitized;
    }
}