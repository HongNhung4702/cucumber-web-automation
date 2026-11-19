
package com.hongnhung.cucumberweb.steps.marks;

import com.hongnhung.cucumberweb.assertions.marks.MarksComparator;
import com.hongnhung.cucumberweb.models.marks.MarkRecord;
import com.hongnhung.cucumberweb.pages.student.marks.MarksPage;
import com.hongnhung.cucumberweb.testdata.marks.MarksBaselineReader;

import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class MarksSteps {

    private static final List<String> EXPECTED_HEADERS =
            List.of(
                    "STT",
                    "Mã học phần",
                    "Tên học phần",
                    "Tín chỉ",
                    "Điểm 10",
                    "Điểm 4",
                    "Điểm chữ",
                    "Kết quả",
                    "Chi tiết"
            );

    private final MarksPage marksPage;
    private final MarksBaselineReader baselineReader;

    private final MarksComparator comparator =
            new MarksComparator();

    private String selectedMode;
    private String selectedProgram;
    private String selectedYear;
    private String selectedSemester;

    private List<MarkRecord> expectedCourses;
    private List<MarkRecord> actualCourses;

    // =========================================
    // 1. KHỞI TẠO STEP DEFINITIONS
    // =========================================

    public MarksSteps(
            MarksPage marksPage,
            MarksBaselineReader baselineReader
    ) {
        this.marksPage = marksPage;
        this.baselineReader = baselineReader;
    }

    // =========================================
    // 2. TRA CỨU BẢNG ĐIỂM
    // =========================================

    @When("sinh viên tra cứu điểm ở chế độ {string} với chương trình {string}, năm học {string} và học kỳ {string}")
    public void searchStudentMarks(
            String mode,
            String program,
            String year,
            String semester
    ) {
        selectedMode = mode;
        selectedProgram = program;
        selectedYear = year;
        selectedSemester = semester;

        expectedCourses = null;
        actualCourses = null;

        marksPage.search(
                mode,
                program,
                year,
                semester
        );
    }

    // =========================================
    // 3. KIỂM TRA ĐIỀU KIỆN BỘ LỌC
    // =========================================

    @Then("các bộ lọc xem điểm hiển thị đúng điều kiện đã chọn")
    public void verifyFilters() {

        assertEquals(
                "Xem điểm sinh viên",
                selectedMode,
                "Chế độ xem điểm không đúng."
        );

        assertTrue(
                marksPage.isStudentModeSelected(),
                "Chế độ Xem điểm sinh viên chưa được chọn."
        );

        assertEquals(
                selectedProgram,
                marksPage.selectedValue("Chương trình đào tạo"),
                "Chương trình đào tạo không khớp."
        );

        assertEquals(
                selectedYear,
                marksPage.selectedValue("Năm học"),
                "Năm học không khớp."
        );

        assertEquals(
                selectedSemester,
                marksPage.selectedValue("Học kỳ"),
                "Học kỳ không khớp."
        );
    }

    // =========================================
    // 4. KIỂM TRA TIÊU ĐỀ BẢNG
    // =========================================

    @Then("bảng điểm hiển thị các cột đã được xác nhận")
    public void verifyTableHeaders() {

        assertEquals(
                EXPECTED_HEADERS,
                marksPage.headers(),
                "Cấu trúc bảng điểm không khớp."
        );
    }

    // =========================================
    // 5. KIỂM TRA SỐ LƯỢNG HỌC PHẦN
    // =========================================

    @Then("số lượng học phần khớp dữ liệu tham chiếu của TC_33")
    public void verifyCourseCount() {

        loadCourses();

        assertEquals(
                expectedCourses.size(),
                actualCourses.size(),
                "Số lượng học phần không khớp baseline TC_33."
        );

        System.out.println(
                "[TC_33] Expected Count="
                        + expectedCourses.size()
                        + ", Actual Count="
                        + actualCourses.size()
        );
    }

    // =========================================
    // 6. ĐỐI CHIẾU TOÀN BỘ HỌC PHẦN
    // =========================================

    @Then("toàn bộ dữ liệu học phần khớp dữ liệu tham chiếu của TC_33")
    public void verifyCourseRecords() {

        loadCourses();

        MarksComparator.Result result =
                comparator.compareCourses(
                        expectedCourses,
                        actualCourses
                );

        System.out.println(
                "[TC_33] Course Comparison: "
                        + result.status()
        );

        assertTrue(
                result.passed(),
                "Dữ liệu học phần không khớp "
                        + "baseline TC_33.\n"
                        + result.details()
        );
    }

    // =========================================
    // 7. ĐỐI CHIẾU CHỈ TIÊU TỔNG HỢP
    // =========================================

    @Then("các chỉ tiêu tổng hợp học kỳ khớp dữ liệu tham chiếu của TC_33")
    public void verifySemesterSummary() {

        Map<String, String> expected =
                baselineReader.readSemesterSummary();

        Map<String, String> actual =
                marksPage.semesterSummary();

        MarksComparator.Result result =
                comparator.compareSummary(
                        expected,
                        actual
                );

        System.out.println(
                "[TC_33] Summary Comparison: "
                        + result.status()
        );

        assertTrue(
                result.passed(),
                "Các chỉ tiêu tổng hợp học kỳ "
                        + "không khớp baseline TC_33.\n"
                        + result.details()
        );
    }

    // =========================================
    // 8. ĐỌC VÀ LƯU DỮ LIỆU HỌC PHẦN
    // =========================================

    private void loadCourses() {

        if (expectedCourses == null) {
            expectedCourses =
                    baselineReader.readCourses();
        }

        if (actualCourses == null) {
            actualCourses =
                    marksPage.courseRecords();
        }
    }
}
