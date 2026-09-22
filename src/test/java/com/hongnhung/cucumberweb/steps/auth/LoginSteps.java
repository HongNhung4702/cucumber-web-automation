package com.hongnhung.cucumberweb.steps.auth;

import com.hongnhung.cucumberweb.config.ConfigManager;
import com.hongnhung.cucumberweb.pages.auth.LoginPage;
import com.hongnhung.cucumberweb.pages.student.StudentPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class LoginSteps {

    private static final String INVALID_USERNAME =
            "invalid_user_for_test";

    private static final String INVALID_PASSWORD =
            "invalid_password_for_test";

    private final LoginPage loginPage;
    private final StudentPage studentPage;

    public LoginSteps(
            LoginPage loginPage,
            StudentPage studentPage
    ) {
        this.loginPage = loginPage;
        this.studentPage = studentPage;
    }

    @Given("người dùng đang ở trang đăng nhập")
    public void userIsOnLoginPage() {

        loginPage.open();

        assertTrue(
                loginPage.isAtLoginPage(),
                "Không thể mở trang đăng nhập."
        );
    }

    @When("người dùng đăng nhập với thông tin hợp lệ")
    public void loginWithValidCredentials() {

        loginPage.login(
                getValidUsername(),
                getValidPassword()
        );
    }

    @When("người dùng nhập tên đăng nhập không hợp lệ")
    public void enterInvalidUsername() {

        loginPage.enterUsername(
                INVALID_USERNAME
        );
    }

    @When("người dùng nhập tên đăng nhập hợp lệ")
    public void enterValidUsername() {

        loginPage.enterUsername(
                getValidUsername()
        );
    }

    @When("người dùng nhập mật khẩu")
    public void enterValidPassword() {

        loginPage.enterPassword(
                getValidPassword()
        );
    }

    @When("người dùng nhập mật khẩu không hợp lệ")
    public void enterInvalidPassword() {

        loginPage.enterPassword(
                INVALID_PASSWORD
        );
    }

    @When("người dùng chỉ nhập mật khẩu")
    public void enterOnlyPassword() {

        loginPage.enterPassword(
                getValidPassword()
        );
    }

    @When("người dùng chỉ nhập tên đăng nhập")
    public void enterOnlyUsername() {

        loginPage.enterUsername(
                getValidUsername()
        );
    }

    @When("người dùng nhấn nút đăng nhập")
    public void clickLoginButton() {

        loginPage.clickLogin();
    }

    @Then("người dùng được chuyển đến trang thông tin sinh viên")
    public void userIsRedirectedToStudentPage() {

        assertTrue(
                studentPage.isAtStudentPage(),
                "Không chuyển đến trang thông tin sinh viên."
        );
    }

    @Then("hệ thống hiển thị thông báo {string}")
    public void systemDisplaysLoginError(
            String expectedMessage
    ) {

        assertEquals(
                expectedMessage,
                loginPage.getInvalidCredentialsMessage()
        );
    }

    @Then("người dùng vẫn ở trang đăng nhập")
    public void userRemainsOnLoginPage() {

        assertTrue(
                loginPage.isAtLoginPage(),
                "Người dùng không còn ở trang đăng nhập."
        );
    }

    @Then("hệ thống hiển thị lỗi {string}")
    public void systemDisplaysValidationError(
            String expectedMessage
    ) {

        String actualMessage;

        if ("Tên đăng nhập là bắt buộc"
                .equals(expectedMessage)) {

            actualMessage =
                    loginPage.getUsernameRequiredMessage();

        } else if ("Mật khẩu là bắt buộc"
                .equals(expectedMessage)) {

            actualMessage =
                    loginPage.getPasswordRequiredMessage();

        } else {

            throw new IllegalArgumentException(
                    "Thông báo validation chưa được hỗ trợ: "
                            + expectedMessage
            );
        }

        assertEquals(
                expectedMessage,
                actualMessage
        );
    }

    private String getValidUsername() {
        return ConfigManager.get(
                "test.username"
        );
    }

    private String getValidPassword() {
        return ConfigManager.get(
                "test.password"
        );
    }
}