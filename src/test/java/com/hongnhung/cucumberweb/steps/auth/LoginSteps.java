
package com.hongnhung.cucumberweb.steps.auth;

import com.hongnhung.cucumberweb.context.ScenarioContext;
import com.hongnhung.cucumberweb.models.auth.LoginTestData;
import com.hongnhung.cucumberweb.pages.auth.LoginPage;
import com.hongnhung.cucumberweb.pages.student.StudentPage;
import com.hongnhung.cucumberweb.testdata.auth.LoginTestDataRepository;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class LoginSteps {

    private static final String SUCCESS = "SUCCESS";
    private static final String INVALID_CREDENTIALS =
            "INVALID_CREDENTIALS";
    private static final String USERNAME_REQUIRED =
            "USERNAME_REQUIRED";
    private static final String PASSWORD_REQUIRED =
            "PASSWORD_REQUIRED";
    private static final String PASSWORD_MASKED =
            "PASSWORD_MASKED";
    private static final String SESSION_PROTECTED =
            "SESSION_PROTECTED";

    private final LoginPage loginPage;
    private final StudentPage studentPage;
    private final ScenarioContext scenarioContext;
    private final LoginTestDataRepository testDataRepository;

    private LoginTestData testData;

    private String firstTabHandle;
    private String secondTabHandle;

    public LoginSteps(
            LoginPage loginPage,
            StudentPage studentPage,
            ScenarioContext scenarioContext,
            LoginTestDataRepository testDataRepository
    ) {
        this.loginPage = loginPage;
        this.studentPage = studentPage;
        this.scenarioContext = scenarioContext;
        this.testDataRepository = testDataRepository;
    }

    // =========================================
    // TC_01 - TC_03
    // =========================================

    @Given("người dùng đang ở trang đăng nhập")
    public void userIsOnLoginPage() {

        loginPage.open();

        assertTrue(
                loginPage.isAtLoginPage(),
                "Không thể mở trang đăng nhập."
        );
    }

    @When("người dùng thực hiện đăng nhập")
    public void userPerformsLogin() {

        LoginTestData currentData = getCurrentTestData();

        if (!currentData.getUsername().isBlank()) {
            loginPage.enterUsername(
                    currentData.getUsername()
            );
        }

        if (!currentData.getPassword().isBlank()) {
            loginPage.enterPassword(
                    currentData.getPassword()
            );
        }

        loginPage.clickLogin();
    }

    @Then("người dùng được chuyển đến trang thông tin sinh viên")
    public void userIsRedirectedToStudentPage() {

        LoginTestData currentData = getCurrentTestData();

        assertEquals(
                SUCCESS,
                currentData.getExpectedResult(),
                "Test data không được cấu hình là SUCCESS."
        );

        assertTrue(
                studentPage.isAtStudentPage(),
                "Không chuyển đến trang thông tin sinh viên."
        );
    }

    @Then("hệ thống hiển thị thông báo đăng nhập không hợp lệ")
    public void systemDisplaysInvalidCredentialsMessage() {

        LoginTestData currentData = getCurrentTestData();

        assertEquals(
                INVALID_CREDENTIALS,
                currentData.getExpectedResult()
        );

        assertEquals(
                currentData.getExpectedMessage(),
                loginPage.getInvalidCredentialsMessage()
        );
    }

    @Then("hệ thống hiển thị lỗi validation")
    public void systemDisplaysValidationError() {

        LoginTestData currentData = getCurrentTestData();

        String actualMessage = switch (
                currentData.getExpectedResult()
                ) {
            case USERNAME_REQUIRED ->
                    loginPage.getUsernameRequiredMessage();

            case PASSWORD_REQUIRED ->
                    loginPage.getPasswordRequiredMessage();

            default -> throw new IllegalStateException(
                    "Expected result không phải validation: "
                            + currentData.getExpectedResult()
            );
        };

        assertEquals(
                currentData.getExpectedMessage(),
                actualMessage
        );
    }

    @Then("người dùng vẫn ở trang đăng nhập")
    public void userRemainsOnLoginPage() {

        assertTrue(
                loginPage.isAtLoginPage(),
                "Người dùng không còn ở trang đăng nhập."
        );
    }

    // =========================================
    // TC_04 - Password masking
    // =========================================

    @When("người dùng nhập mật khẩu vào ô mật khẩu")
    public void userEntersPasswordForMaskingTest() {

        LoginTestData currentData = getCurrentTestData();

        assertEquals(
                PASSWORD_MASKED,
                currentData.getExpectedResult()
        );

        assertFalse(
                currentData.getPassword().isBlank(),
                "TC_04 chưa có mật khẩu kiểm thử."
        );

        loginPage.enterPassword(
                currentData.getPassword()
        );
    }

    @Then("nội dung mật khẩu phải được che")
    public void passwordMustBeMasked() {

        LoginTestData currentData = getCurrentTestData();

        assertTrue(
                loginPage.hasEnteredPassword(
                        currentData.getPassword()
                ),
                "Mật khẩu chưa được nhập đúng."
        );

        assertTrue(
                loginPage.isPasswordMasked(),
                "Ô mật khẩu không được che."
        );
    }

    // =========================================
    // TC_06 - Shared authentication step
    // =========================================

    @Given("sinh viên đã đăng nhập thành công")
    public void studentHasLoggedInSuccessfully() {

        LoginTestData currentData = getCurrentTestData();

        assertEquals(
                SESSION_PROTECTED,
                currentData.getExpectedResult(),
                "Test data phải là SESSION_PROTECTED."
        );

        assertFalse(
                currentData.getUsername().isBlank(),
                "Chưa có tên đăng nhập kiểm thử."
        );

        assertFalse(
                currentData.getPassword().isBlank(),
                "Chưa có mật khẩu kiểm thử."
        );

        loginPage.open();

        loginPage.login(
                currentData.getUsername(),
                currentData.getPassword()
        );

        assertTrue(
                studentPage.isAtStudentPage(),
                "Sinh viên đăng nhập không thành công."
        );
    }

    // =========================================
    // TC_06 - Session invalidation across tabs
    // =========================================

    @When("sinh viên mở trang Tiến độ học tập ở tab thứ nhất")
    public void studentOpensOverviewInFirstTab() {

        firstTabHandle =
                studentPage.getCurrentWindowHandle();

        studentPage.openOverviewPage();

        assertTrue(
                studentPage.isAtStudentPage(),
                "Không truy cập được trang sinh viên."
        );

        assertTrue(
                studentPage.getMarksPageUrl()
                        .toLowerCase()
                        .contains("/student/overview"),
                "Tab thứ nhất không ở trang Tiến độ học tập."
        );
    }

    @When("sinh viên mở trang Xem điểm ở tab thứ hai")
    public void studentOpensMarksInSecondTab() {

        studentPage.openNewTab();

        secondTabHandle =
                studentPage.getCurrentWindowHandle();

        assertFalse(
                firstTabHandle.equals(secondTabHandle),
                "Hai tab có cùng window handle."
        );

        assertEquals(
                2,
                studentPage.getOpenTabCount(),
                "Trình duyệt không có đúng hai tab."
        );

        studentPage.openMarksPage();
        studentPage.waitForMarksTable();

        assertTrue(
                studentPage.isMarksTableDisplayed(),
                "Bảng điểm không hiển thị ở tab thứ hai."
        );
    }

    @When("sinh viên đăng xuất tại tab thứ nhất")
    public void studentLogsOutFromFirstTab() {

        assertTrue(
                firstTabHandle != null
                        && secondTabHandle != null,
                "Chưa khởi tạo đầy đủ hai tab."
        );

        studentPage.switchToTab(firstTabHandle);

        studentPage.logout();

        assertTrue(
                studentPage.isAtPublicHomePage(),
                "Tab thứ nhất không trở về trang chủ công khai."
        );
    }

    @When("sinh viên chuyển sang tab thứ hai và tải lại trang")
    public void studentRefreshesSecondTab() {

        assertTrue(
                secondTabHandle != null,
                "Chưa lưu window handle của tab thứ hai."
        );

        studentPage.switchToTab(secondTabHandle);

        studentPage.refreshCurrentTab();
    }

    @Then("tab thứ hai không còn hiển thị dữ liệu điểm")
    public void secondTabMustNotShowMarks() {

        assertTrue(
                loginPage.isLoginFormDisplayed(),
                "Tab thứ hai không yêu cầu đăng nhập sau khi tải lại."
        );

        assertFalse(
                studentPage.isMarksTableDisplayed(),
                "Tab thứ hai vẫn hiển thị bảng điểm sau đăng xuất."
        );
    }

    @Then("hệ thống yêu cầu đăng nhập lại")
    public void systemRequiresLoginAgain() {

        assertTrue(
                loginPage.isLoginFormDisplayed(),
                "Hệ thống không hiển thị biểu mẫu đăng nhập."
        );
    }

    // =========================================
    // Test data
    // =========================================

    private LoginTestData getCurrentTestData() {

        if (testData == null) {
            testData = testDataRepository.findByTestCaseId(
                    scenarioContext.getTestCaseId()
            );
        }

        return testData;
    }
}
