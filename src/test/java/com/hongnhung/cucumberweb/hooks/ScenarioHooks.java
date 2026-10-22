package com.hongnhung.cucumberweb.hooks;

import com.hongnhung.cucumberweb.config.ConfigManager;
import com.hongnhung.cucumberweb.context.ScenarioContext;
import com.hongnhung.cucumberweb.driver.DriverManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

public final class ScenarioHooks {

    private static final Path SCREENSHOT_DIRECTORY =
            Path.of("target", "screenshots");

    private static final String TEST_CASE_TAG_PREFIX =
            "@TC_";

    private final DriverManager driverManager;
    private final ScenarioContext scenarioContext;

    public ScenarioHooks(
            DriverManager driverManager,
            ScenarioContext scenarioContext
    ) {
        this.driverManager = driverManager;
        this.scenarioContext = scenarioContext;
    }

    @Before
    public void setUp(Scenario scenario) {

        initializeTestCaseId(scenario);

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

    private void initializeTestCaseId(
            Scenario scenario
    ) {

        List<String> testCaseTags =
                scenario
                        .getSourceTagNames()
                        .stream()
                        .filter(tag ->
                                tag.startsWith(
                                        TEST_CASE_TAG_PREFIX
                                )
                        )
                        .toList();

        if (testCaseTags.size() > 1) {

            throw new IllegalStateException(
                    "Scenario must not contain more than one Test Case ID tag: "
                            + testCaseTags
            );
        }

        if (testCaseTags.isEmpty()) {
            return;
        }

        String testCaseId =
                testCaseTags
                        .get(0)
                        .substring(1);

        scenarioContext.setTestCaseId(
                testCaseId
        );
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