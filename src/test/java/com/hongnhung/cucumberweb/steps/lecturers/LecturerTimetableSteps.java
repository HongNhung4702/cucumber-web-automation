
package com.hongnhung.cucumberweb.steps.lecturers;

import com.hongnhung.cucumberweb.assertions.lecturers.LecturerScheduleComparator;
import com.hongnhung.cucumberweb.models.lecturers.LecturerScheduleRecord;
import com.hongnhung.cucumberweb.pages.publicpages.LecturerTimetablePage;
import com.hongnhung.cucumberweb.testdata.lecturers.LecturerScheduleBaselineReader;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class LecturerTimetableSteps {

    private final LecturerTimetablePage page;

    private final LecturerScheduleBaselineReader reader;

    private final LecturerScheduleComparator comparator;

    private String year;
    private String term;
    private String faculty;
    private String lecturer;

    private List<LecturerScheduleRecord> expected;
    private List<LecturerScheduleRecord> actual;

    public LecturerTimetableSteps(
            LecturerTimetablePage page,
            LecturerScheduleBaselineReader reader
    ) {
        this.page = page;
        this.reader = reader;

        this.comparator =
                new LecturerScheduleComparator();
    }

    // =========================================
    // 1. MỞ TRANG TRA CỨU
    // =========================================

    @Given(
            "người tra cứu đã mở trang "
                    + "thời khóa biểu giảng viên"
    )
    public void openLecturerTimetable() {

        page.open();

        expected = null;
        actual = null;

        System.out.println(
                "[TC_38] Bắt đầu tra cứu "
                        + "thời khóa biểu giảng viên."
        );
    }

    // =========================================
    // 2. CHỌN BỘ LỌC
    // =========================================

    @When(
            "người tra cứu chọn năm học {string}, "
                    + "học kỳ {string}, khoa {string} "
                    + "và giảng viên {string}"
    )
    public void selectFilters(
            String year,
            String term,
            String faculty,
            String lecturer
    ) {

        this.year = year;
        this.term = term;
        this.faculty = faculty;
        this.lecturer = lecturer;

        page.selectFilters(
                year,
                term,
                faculty,
                lecturer
        );
    }

    // =========================================
    // 3. NHẤN LỌC
    // =========================================

    @And(
            "người tra cứu nhấn Lọc "
                    + "thời khóa biểu giảng viên"
    )
    public void clickFilter() {

        page.clickFilter(
                year,
                term,
                faculty,
                lecturer
        );

        // Mỗi lần tra cứu phải đọc lại Actual.
        actual = null;
    }

    // =========================================
    // 4. XÁC THỰC BỘ LỌC
    // =========================================

    @Then(
            "bộ lọc thời khóa biểu giảng viên "
                    + "hiển thị đúng điều kiện đã chọn"
    )
    public void verifyFilters() {

        assertEquals(
                year,
                page.selectedYear(),
                "TC_38: Sai năm học."
        );

        assertEquals(
                term,
                page.selectedTerm(),
                "TC_38: Sai học kỳ."
        );

        assertEquals(
                faculty,
                page.selectedFaculty(),
                "TC_38: Sai khoa."
        );

        assertEquals(
                lecturer,
                page.selectedLecturer(),
                "TC_38: Sai giảng viên."
        );

        System.out.println(
                "[TC_38] Bộ lọc khớp điều kiện."
        );
    }

    // =========================================
    // 5. XÁC THỰC TIÊU ĐỀ
    // =========================================

    @And(
            "tiêu đề kết quả hiển thị khoa {string}, "
                    + "học kỳ {string} và năm học {string}"
    )
    public void verifyHeading(
            String expectedFaculty,
            String expectedTermCode,
            String expectedYear
    ) {

        assertTrue(
                page.headingMatches(
                        expectedYear,
                        expectedTermCode,
                        expectedFaculty
                ),
                "TC_38: Tiêu đề kết quả "
                        + "không khớp điều kiện tra cứu."
        );

        System.out.println(
                "[TC_38] Tiêu đề kết quả khớp."
        );
    }

    // =========================================
    // 6. XÁC THỰC CẤU TRÚC BẢNG
    // =========================================

    @And(
            "bảng thời khóa biểu giảng viên "
                    + "hiển thị đầy đủ các cột đã xác nhận"
    )
    public void verifyHeaders() {

        assertEquals(
                page.expectedTopHeaders(),
                page.topHeaders(),
                "TC_38: Sai tiêu đề cột chính."
        );

        assertEquals(
                page.expectedSubHeaders(),
                page.subHeaders(),
                "TC_38: Sai các cột LT, TH, TL."
        );

        System.out.println(
                "[TC_38] Các cột bảng khớp."
        );
    }

    // =========================================
    // 7. KIỂM TRA SỐ MÃ LHP
    // =========================================

    @Then(
            "số lượng lớp học phần "
                    + "khớp baseline của TC_38"
    )
    public void verifyCourseCount() {

        loadData();

        int expectedCount =
                comparator.countCourses(expected);

        int actualCount =
                comparator.countCourses(actual);

        System.out.println(
                "[TC_38] Mã LHP: Expected="
                        + expectedCount
                        + ", Actual=" + actualCount
        );

        assertEquals(
                expectedCount,
                actualCount,
                "TC_38: Sai số lượng mã LHP."
        );
    }

    // =========================================
    // 8. KIỂM TRA SỐ DÒNG
    // =========================================

    @And(
            "số lượng dòng lịch giảng dạy "
                    + "khớp baseline của TC_38"
    )
    public void verifyRowCount() {

        loadData();

        System.out.println(
                "[TC_38] Số dòng: Expected="
                        + expected.size()
                        + ", Actual=" + actual.size()
        );

        assertEquals(
                expected.size(),
                actual.size(),
                "TC_38: Sai số dòng lịch giảng dạy."
        );
    }

    // =========================================
    // 9. ĐỐI CHIẾU TOÀN BỘ DỮ LIỆU
    // =========================================

    @And(
            "toàn bộ dữ liệu lịch giảng dạy "
                    + "khớp baseline của TC_38"
    )
    public void verifyData() {

        loadData();

        LecturerScheduleComparator.Result result =
                comparator.compare(
                        expected,
                        actual
                );

        System.out.println(
                "[TC_38] " + result.details()
        );

        assertTrue(
                result.passed(),
                "TC_38: Dữ liệu không khớp baseline.\n"
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
