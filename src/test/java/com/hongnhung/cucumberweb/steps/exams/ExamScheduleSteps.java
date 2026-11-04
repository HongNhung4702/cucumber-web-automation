
package com.hongnhung.cucumberweb.steps.exams;

import com.hongnhung.cucumberweb.assertions.exams.ExamScheduleComparator;
import com.hongnhung.cucumberweb.config.ConfigManager;
import com.hongnhung.cucumberweb.models.exams.ExamRecord;
import com.hongnhung.cucumberweb.pages.auth.LoginPage;
import com.hongnhung.cucumberweb.pages.student.ExamSchedulePage;
import com.hongnhung.cucumberweb.pages.student.StudentPage;
import com.hongnhung.cucumberweb.testdata.exams.ExamBaselineReader;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class ExamScheduleSteps {

    private static final List<String> EXPECTED_UI_HEADERS =
            List.of(
                    "Mã lớp học phần/Tên lớp học phần",
                    "Lần thi",
                    "Ngày thi",
                    "Giờ thi",
                    "Phòng thi",
                    "Địa điểm",
                    "Hình thức thi",
                    "Kỳ thi",
                    "Ghi chú",
                    "Vắng thi"
            );

    private final LoginPage loginPage;
    private final StudentPage studentPage;
    private final ExamSchedulePage examPage;
    private final ExamBaselineReader baselineReader;

    private final ExamScheduleComparator comparator =
            new ExamScheduleComparator();

    private List<ExamRecord> expected;
    private List<ExamRecord> actual;

    public ExamScheduleSteps(
            LoginPage loginPage,
            StudentPage studentPage,
            ExamSchedulePage examPage,
            ExamBaselineReader baselineReader
    ) {

        this.loginPage = loginPage;
        this.studentPage = studentPage;
        this.examPage = examPage;
        this.baselineReader = baselineReader;
    }

    // =========================================
    // 1. ĐĂNG NHẬP
    // =========================================

    @Given("sinh viên đã đăng nhập bằng tài khoản kiểm thử hợp lệ")
    public void loginWithAuthorizedAccount() {

        String username =
                ConfigManager.get("test.username");

        String password =
                ConfigManager.get("test.password");

        loginPage.open();
        loginPage.login(username, password);

        assertTrue(
                studentPage.isAtStudentPage(),
                "Đăng nhập không thành công; "
                        + "không tiếp tục kiểm thử lịch thi."
        );
    }

    // =========================================
    // 2. TRA CỨU LỊCH THI
    // =========================================

    @When("sinh viên tra cứu lịch thi năm học {string} và học kỳ {string}")
    public void searchExamSchedule(
            String year,
            String semester
    ) {

        expected = null;
        actual = null;

        examPage.open();

        examPage.selectYearSemesterTab();

        assertTrue(
                examPage.isYearSemesterTabSelected(),
                "TC_28: Tab NĂM HỌC-HỌC KỲ "
                        + "không được chọn."
        );

        examPage.choose("Năm học", year);
        examPage.choose("Học kỳ", semester);

        System.out.println(
                "[TC_28] Tra cứu: "
                        + year + " | " + semester
        );
    }

    // =========================================
    // 3. KIỂM TRA NĂM HỌC
    // =========================================

    @Then("bộ lọc năm học hiển thị {string}")
    public void verifyYear(String year) {

        assertEquals(
                year,
                examPage.selectedValue("Năm học"),
                "TC_28: Sai năm học."
        );
    }

    // =========================================
    // 4. KIỂM TRA HỌC KỲ
    // =========================================

    @Then("bộ lọc học kỳ hiển thị {string}")
    public void verifySemester(String semester) {

        assertEquals(
                semester,
                examPage.selectedValue("Học kỳ"),
                "TC_28: Sai học kỳ."
        );
    }

    // =========================================
    // 5. KIỂM TRA TAB VÀ HEADER BẢNG
    // =========================================

    @Then("bảng lịch thi {string} hiển thị các cột đã được xác nhận")
    public void verifyHeaders(String tableName) {

        assertEquals(
                "Tất cả",
                tableName,
                "TC_28 chỉ dùng bảng Tất cả."
        );

        assertTrue(
                examPage.isYearSemesterTabSelected(),
                "TC_28: Tab NĂM HỌC-HỌC KỲ "
                        + "không còn được chọn."
        );

        assertEquals(
                EXPECTED_UI_HEADERS,
                examPage.headers(),
                "Các cột hiển thị khác dữ liệu đã xác nhận."
        );

        System.out.println(
                "[TC_28] Tab và cấu trúc bảng: MATCH."
        );
    }

    // =========================================
    // 6. KIỂM TRA SỐ BẢN GHI
    // =========================================

    @Then("số lượng bản ghi trong bảng {string} khớp dữ liệu tham chiếu của TC_28")
    public void verifyCount(String tableName) {

        assertEquals("Tất cả", tableName);

        ExamScheduleComparator.Result result =
                compareCurrentData();

        assertEquals(
                result.expectedCount(),
                result.actualCount(),
                "Số lượng bản ghi không khớp baseline TC_28. "
                        + result.details()
        );
    }

    // =========================================
    // 7. ĐỐI CHIẾU TOÀN BỘ BẢN GHI
    // =========================================

    @Then("toàn bộ bản ghi trong bảng {string} khớp dữ liệu tham chiếu của TC_28")
    public void verifyAllRecords(String tableName) {

        assertEquals("Tất cả", tableName);

        ExamScheduleComparator.Result result =
                compareCurrentData();

        assertTrue(
                result.passed(),
                "Dữ liệu lịch thi không khớp baseline. "
                        + result.details()
        );

        if (result.unicodeNormalized()) {

            System.out.println(
                    "[TC_28] Expected/Actual khác "
                            + "biểu diễn Unicode nhưng "
                            + "tương đương sau chuẩn hóa NFC."
            );
        }

        System.out.println(
                "[TC_28] Dữ liệu lịch thi: MATCH."
        );
    }

    // =========================================
    // 8. ĐỌC VÀ SO SÁNH EXPECTED/ACTUAL
    // =========================================

    private ExamScheduleComparator.Result compareCurrentData() {

        if (expected == null) {
            expected = baselineReader.readTc28();
        }

        if (actual == null) {
            actual = examPage.records();
        }

        return comparator.compare(expected, actual);
    }
}
