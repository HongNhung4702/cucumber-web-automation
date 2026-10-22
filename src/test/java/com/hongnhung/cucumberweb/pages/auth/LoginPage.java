
package com.hongnhung.cucumberweb.pages.auth;

import com.hongnhung.cucumberweb.config.ConfigManager;
import com.hongnhung.cucumberweb.driver.DriverManager;
import com.hongnhung.cucumberweb.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public final class LoginPage extends BasePage {

    private static final By USERNAME_INPUT =
            By.name("email");

    private static final By PASSWORD_INPUT =
            By.name("password");

    private static final By LOGIN_BUTTON =
            By.cssSelector("form button[type='submit']");

    private static final By INVALID_CREDENTIALS_MESSAGE =
            By.xpath(
                    "//*[normalize-space()='Tên đăng nhập hoặc mật khẩu không chính xác']"
            );

    private static final By USERNAME_REQUIRED_MESSAGE =
            By.xpath(
                    "//*[normalize-space()='Tên đăng nhập là bắt buộc']"
            );

    private static final By PASSWORD_REQUIRED_MESSAGE =
            By.xpath(
                    "//*[normalize-space()='Mật khẩu là bắt buộc']"
            );

    public LoginPage(DriverManager driverManager) {
        super(driverManager);
    }

    public void open() {
        navigateTo(
                ConfigManager.get("base.url") + "/Login"
        );
    }

    public void enterUsername(String username) {
        type(USERNAME_INPUT, username);
    }

    public void enterPassword(String password) {
        type(PASSWORD_INPUT, password);
    }

    public void clickLogin() {
        click(LOGIN_BUTTON);
    }

    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
    }

    public String getInvalidCredentialsMessage() {
        return getText(INVALID_CREDENTIALS_MESSAGE);
    }

    public String getUsernameRequiredMessage() {
        return getText(USERNAME_REQUIRED_MESSAGE);
    }

    public String getPasswordRequiredMessage() {
        return getText(PASSWORD_REQUIRED_MESSAGE);
    }

    public boolean isAtLoginPage() {
        return currentUrlContains("/login");
    }

    public boolean isPasswordMasked() {
        WebElement passwordInput =
                waitUntilVisible(PASSWORD_INPUT);

        String inputType =
                passwordInput.getDomProperty("type");

        return "password".equalsIgnoreCase(inputType);
    }

    public boolean hasEnteredPassword(String expectedPassword) {
        WebElement passwordInput =
                waitUntilVisible(PASSWORD_INPUT);

        String actualValue =
                passwordInput.getDomProperty("value");

        return expectedPassword != null
                && !expectedPassword.isEmpty()
                && expectedPassword.equals(actualValue);
    }

    public boolean isLoginFormDisplayed() {
        return waitUntilVisible(USERNAME_INPUT).isDisplayed()
                && waitUntilVisible(PASSWORD_INPUT).isDisplayed()
                && waitUntilVisible(LOGIN_BUTTON).isDisplayed();
    }
}
