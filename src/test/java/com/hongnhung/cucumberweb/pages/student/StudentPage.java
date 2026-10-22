
package com.hongnhung.cucumberweb.pages.student;

import com.hongnhung.cucumberweb.config.ConfigManager;
import com.hongnhung.cucumberweb.driver.DriverManager;
import com.hongnhung.cucumberweb.pages.BasePage;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WindowType;

import java.net.URI;
import java.util.Set;

public final class StudentPage extends BasePage {

    private static final By ACCOUNT_MENU_BUTTON =
            By.cssSelector(
                    "button.MuiButtonBase-root:has(> div.MuiAvatar-root)"
            );

    private static final By LOGOUT_BUTTON =
            By.xpath(
                    "//button[@type='button' " +
                            "and normalize-space(.)='Đăng xuất']"
            );

    private static final By MARKS_TABLE =
            By.xpath(
                    "//table[@aria-label='a dense table']" +
                            "[.//thead//th[normalize-space(.)='STT'] " +
                            "and .//tbody//button[@aria-label='expand row']]"
            );

    public StudentPage(DriverManager driverManager) {
        super(driverManager);
    }

    // =========================================
    // Student page
    // =========================================

    public boolean isAtStudentPage() {
        return waitUntilUrlContains("/student");
    }

    // =========================================
    // Account actions
    // =========================================

    public void openAccountMenu() {
        click(ACCOUNT_MENU_BUTTON);
    }

    public void logout() {
        openAccountMenu();
        click(LOGOUT_BUTTON);
    }

    // =========================================
    // Public home page
    // =========================================

    public boolean isAtPublicHomePage() {

        URI expectedUri = URI.create(
                ConfigManager.get("base.url")
        );

        return waitDriver().until(webDriver -> {

            URI actualUri = URI.create(
                    webDriver.getCurrentUrl()
            );

            String actualPath = actualUri.getPath();

            boolean sameOrigin =
                    expectedUri.getScheme().equalsIgnoreCase(
                            actualUri.getScheme()
                    )
                            && expectedUri.getAuthority().equalsIgnoreCase(
                            actualUri.getAuthority()
                    );

            boolean rootPath =
                    actualPath == null
                            || actualPath.isEmpty()
                            || actualPath.equals("/");

            return sameOrigin && rootPath;
        });
    }

    // =========================================
    // Navigation
    // =========================================

    public void openMarksPage() {
        navigateTo(baseUrl() + "/student/marks");
    }

    public void openOverviewPage() {
        navigateTo(baseUrl() + "/student/overview");
    }

    public String getMarksPageUrl() {
        return getCurrentUrl();
    }

    public void openUrl(String url) {
        navigateTo(url);
    }

    private String baseUrl() {
        return ConfigManager.get("base.url")
                .replaceAll("/+$", "");
    }

    // =========================================
    // Marks verification
    // =========================================

    public boolean isMarksTableDisplayed() {

        for (WebElement table :
                driver().findElements(MARKS_TABLE)) {

            try {
                if (table.isDisplayed()) {
                    return true;
                }
            } catch (StaleElementReferenceException ignored) {
                // DOM có thể được React render lại.
            }
        }

        return false;
    }

    public void waitForMarksTable() {
        waitUntilVisible(MARKS_TABLE);
    }

    // =========================================
    // Browser tab management
    // =========================================

    public String getCurrentWindowHandle() {
        return driver().getWindowHandle();
    }

    public void openNewTab() {
        driver().switchTo().newWindow(WindowType.TAB);
    }

    public void switchToTab(String windowHandle) {

        if (windowHandle == null
                || windowHandle.isBlank()) {
            throw new IllegalArgumentException(
                    "Window handle không hợp lệ."
            );
        }

        Set<String> availableHandles =
                driver().getWindowHandles();

        if (!availableHandles.contains(windowHandle)) {
            throw new IllegalStateException(
                    "Không tìm thấy tab trình duyệt cần chuyển."
            );
        }

        driver().switchTo().window(windowHandle);
    }

    public void refreshCurrentTab() {
        driver().navigate().refresh();
    }

    public int getOpenTabCount() {
        return driver().getWindowHandles().size();
    }
}
