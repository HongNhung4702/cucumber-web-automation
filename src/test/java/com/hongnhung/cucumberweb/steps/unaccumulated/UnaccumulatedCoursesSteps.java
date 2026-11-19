
package com.hongnhung.cucumberweb.steps.unaccumulated;

import com.hongnhung.cucumberweb.assertions.unaccumulated.UnaccumulatedCourseComparator;
import com.hongnhung.cucumberweb.models.unaccumulated.UnaccumulatedCourseRecord;
import com.hongnhung.cucumberweb.pages.student.unaccumulated.UnaccumulatedCoursesPage;
import com.hongnhung.cucumberweb.testdata.unaccumulated.UnaccumulatedBaselineReader;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class UnaccumulatedCoursesSteps {

    private final UnaccumulatedCoursesPage page;

    private final UnaccumulatedBaselineReader reader;

    private final UnaccumulatedCourseComparator comparator;

    private String program;

    private List<UnaccumulatedCourseRecord> expected;
    private List<UnaccumulatedCourseRecord> actual;

    public UnaccumulatedCoursesSteps(
            UnaccumulatedCoursesPage page,
            UnaccumulatedBaselineReader reader
    ) {

        this.page = page;
        this.reader = reader;
        this.comparator =
                new UnaccumulatedCourseComparator();
    }

    // =========================================
    // 1. MỞ TRANG
    // =========================================

    @When("sinh viên mở trang Tổng quan học tập")
    public void openOverview() {

        page.open();

        expected = null;
        actual = null;
    }

    // =========================================
    // 2. CHỌN CHƯƠNG TRÌNH ĐÀO TẠO
    // =========================================

    @And(
            "sinh viên chọn chương trình đào tạo "
                    + "{string} cho TC_17"
    )
    public void selectProgram(String program) {

        this.program = program;

        page.selectProgram(program);
    }

    // =========================================
    // 3. MỞ DANH SÁCH
    // =========================================

    @And(
            "sinh viên mở danh sách học phần "
                    + "chưa tích lũy"
    )
    public void openDialog() {

        page.openDialog();

        actual = null;
    }

    // =========================================
    // 4. KIỂM TRA DANH TÍNH VÀ CHƯƠNG TRÌNH
    // =========================================

    @Then(
            "thông tin sinh viên và chương trình "
                    + "đào tạo khớp ngữ cảnh TC_17"
    )
    public void verifyContext() {

        String studentInfo =
                page.studentInformation();

        assertTrue(
                studentInfo.contains(
                        "Lê Thị Hồng Nhung"
                ),
                "TC_17: Sai họ tên sinh viên."
        );

        assertTrue(
                studentInfo.contains("4551190041"),
                "TC_17: Sai mã sinh viên."
        );

        assertTrue(
                studentInfo.contains(
                        "Kỹ thuật phần mềm K45"
                ),
                "TC_17: Sai lớp sinh viên."
        );

        assertTrue(
                studentInfo.contains(
                        "Đại học chính quy K45"
                ),
                "TC_17: Sai khóa học."
        );

        assertEquals(
                program,
                page.selectedProgram(),
                "TC_17: Sai chương trình đào tạo."
        );

        assertEquals(
                "119DHCQK45",
                page.selectedProgramCode(),
                "TC_17: Sai mã chương trình đào tạo."
        );

        System.out.println(
                "[TC_17] Danh tính và "
                        + "chương trình đào tạo: MATCH."
        );
    }

    // =========================================
    // 5. KIỂM TRA TIÊU ĐỀ HỘP THOẠI
    // =========================================

    @And(
            "hộp thoại Học phần chưa tích lũy "
                    + "hiển thị đúng tiêu đề"
    )
    public void verifyDialogTitle() {

        assertEquals(
                "HỌC PHẦN CHƯA TÍCH LŨY",
                page.dialogTitle(),
                "TC_17: Sai tiêu đề hộp thoại."
        );

        System.out.println(
                "[TC_17] Tiêu đề hộp thoại: MATCH."
        );
    }

    // =========================================
    // 6. KIỂM TRA CẤU TRÚC BẢNG
    // =========================================

    @And(
            "bảng học phần chưa tích lũy "
                    + "hiển thị đầy đủ các cột đã xác nhận"
    )
    public void verifyTableHeaders() {

        assertEquals(
                page.expectedTopHeaders(),
                page.topHeaders(),
                "TC_17: Sai tiêu đề cột chính."
        );

        assertEquals(
                page.expectedSubHeaders(),
                page.subHeaders(),
                "TC_17: Sai cột LT, TH, TL."
        );

        assertTrue(
                page.headerStructureMatches(),
                "TC_17: Sai cấu trúc colspan/rowspan."
        );

        System.out.println(
                "[TC_17] Cấu trúc bảng: MATCH."
        );
    }

    // =========================================
    // 7. KIỂM TRA SỐ LƯỢNG HỌC PHẦN
    // =========================================

    @Then(
            "số lượng học phần chưa tích lũy "
                    + "khớp baseline của TC_17"
    )
    public void verifyCourseCount() {

        loadData();

        int expectedCount =
                comparator.countCourses(expected);

        int actualCount =
                comparator.countCourses(actual);

        assertEquals(
                expected.size(),
                actual.size(),
                "TC_17: Sai số dòng học phần."
        );

        assertEquals(
                expectedCount,
                actualCount,
                "TC_17: Sai số mã học phần."
        );

        System.out.println(
                "[TC_17] Số học phần: Expected="
                        + expectedCount
                        + ", Actual=" + actualCount
        );
    }

    // =========================================
    // 8. KIỂM TRA TỔNG TÍN CHỈ
    // =========================================

    @And(
            "tổng số tín chỉ trong danh sách "
                    + "khớp baseline của TC_17"
    )
    public void verifyTotalCredits() {

        loadData();

        int expectedCredits =
                comparator.totalCredits(expected);

        int actualCredits =
                comparator.totalCredits(actual);

        assertEquals(
                expectedCredits,
                actualCredits,
                "TC_17: Tổng số tín chỉ không khớp."
        );

        System.out.println(
                "[TC_17] Tổng tín chỉ: Expected="
                        + expectedCredits
                        + ", Actual=" + actualCredits
        );
    }

    // =========================================
    // 9. SO SÁNH TOÀN BỘ DỮ LIỆU
    // =========================================

    @And(
            "toàn bộ dữ liệu học phần chưa tích lũy "
                    + "khớp baseline của TC_17"
    )
    public void verifyAllData() {

        loadData();

        UnaccumulatedCourseComparator.Result result =
                comparator.compare(expected, actual);

        System.out.println(
                "[TC_17] " + result.details()
        );

        assertTrue(
                result.passed(),
                "TC_17: Dữ liệu không khớp baseline.\n"
                        + result.details()
        );
    }

    // =========================================
    // 10. ĐỌC EXPECTED VÀ ACTUAL
    // =========================================

    private void loadData() {

        if (expected == null) {
            expected = reader.readRecords();
        }

        if (actual == null) {
            actual = page.readRecords();
        }
    }
}
