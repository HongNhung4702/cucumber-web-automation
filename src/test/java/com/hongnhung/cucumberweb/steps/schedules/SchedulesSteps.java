
package com.hongnhung.cucumberweb.steps.schedules;

import com.hongnhung.cucumberweb.assertions.schedules.SchedulesComparator;
import com.hongnhung.cucumberweb.models.schedules.ScheduleRecord;
import com.hongnhung.cucumberweb.pages.student.SchedulesPage;
import com.hongnhung.cucumberweb.testdata.schedules.SchedulesBaselineReader;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class SchedulesSteps {

    private final SchedulesPage schedulesPage;
    private final SchedulesBaselineReader baselineReader;
    private final SchedulesComparator comparator;

    private List<ScheduleRecord> expected;
    private List<ScheduleRecord> actual;

    public SchedulesSteps(
            SchedulesPage schedulesPage,
            SchedulesBaselineReader baselineReader
    ) {
        this.schedulesPage = schedulesPage;
        this.baselineReader = baselineReader;
        this.comparator = new SchedulesComparator();
    }

    @When(
            "sinh viên tra cứu thời khóa biểu lớp học phần "
                    + "năm học {string} và học kỳ {string}"
    )
    public void searchSchedules(
            String year,
            String term
    ) {
        schedulesPage.open();
        schedulesPage.search(year, term);

        expected = null;
        actual = null;

        System.out.println(
                "[TC_21] Tra cứu: "
                        + year + " | " + term
        );
    }

    @Then(
            "bộ lọc năm học của thời khóa biểu "
                    + "hiển thị {string}"
    )
    public void verifyYear(String expectedYear) {

        assertEquals(
                expectedYear,
                schedulesPage.selectedYear(),
                "TC_21: Năm học hiển thị không đúng."
        );
    }

    @Then(
            "bộ lọc học kỳ của thời khóa biểu "
                    + "hiển thị {string}"
    )
    public void verifyTerm(String expectedTerm) {

        assertEquals(
                expectedTerm,
                schedulesPage.selectedTerm(),
                "TC_21: Học kỳ hiển thị không đúng."
        );
    }

    @And(
            "bảng thời khóa biểu hiển thị "
                    + "các cột đã được xác nhận"
    )
    public void verifyTableHeaders() {

        assertEquals(
                schedulesPage.expectedHeaders(),
                schedulesPage.tableHeaders(),
                "TC_21: Danh sách cột không khớp."
        );
    }

    @Then(
            "số lượng lớp học phần khớp "
                    + "dữ liệu tham chiếu của TC_21"
    )
    public void verifyCourseCount() {

        loadData();

        int expectedCount =
                comparator.countCourses(expected);

        int actualCount =
                comparator.countCourses(actual);

        System.out.println(
                "[TC_21] Số lớp học phần: "
                        + "Expected=" + expectedCount
                        + ", Actual=" + actualCount
        );

        assertEquals(
                expectedCount,
                actualCount,
                "TC_21: Sai số lượng lớp học phần."
        );
    }

    @And(
            "số lượng dòng lịch học khớp "
                    + "dữ liệu tham chiếu của TC_21"
    )
    public void verifyRowCount() {

        loadData();

        System.out.println(
                "[TC_21] Số dòng lịch học: "
                        + "Expected=" + expected.size()
                        + ", Actual=" + actual.size()
        );

        assertEquals(
                expected.size(),
                actual.size(),
                "TC_21: Sai số lượng dòng lịch học."
        );
    }

    @And(
            "toàn bộ dữ liệu lịch học khớp "
                    + "dữ liệu tham chiếu của TC_21"
    )
    public void verifyScheduleData() {

        loadData();

        SchedulesComparator.Result result =
                comparator.compare(expected, actual);

        System.out.println(
                "[TC_21] " + result.details()
        );

        assertTrue(
                result.passed(),
                "TC_21: Dữ liệu không khớp baseline.\n"
                        + result.details()
        );
    }

    private void loadData() {

        if (expected == null) {
            expected = baselineReader.readRecords();
        }

        if (actual == null) {
            actual = schedulesPage.readRecords();
        }
    }
}
