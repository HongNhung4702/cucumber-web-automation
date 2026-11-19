
package com.hongnhung.cucumberweb.steps.schedules;

import com.hongnhung.cucumberweb.assertions.schedules.WeeklyScheduleComparator;
import com.hongnhung.cucumberweb.models.schedules.WeeklyScheduleRecord;
import com.hongnhung.cucumberweb.pages.student.schedules.StudentWeeklySchedulePage;
import com.hongnhung.cucumberweb.testdata.schedules.WeeklyScheduleBaselineReader;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class StudentWeeklyScheduleSteps {

    private final StudentWeeklySchedulePage page;
    private final WeeklyScheduleBaselineReader reader;

    private final WeeklyScheduleComparator comparator =
            new WeeklyScheduleComparator();

    public StudentWeeklyScheduleSteps(
            StudentWeeklySchedulePage page,
            WeeklyScheduleBaselineReader reader
    ) {
        this.page = page;
        this.reader = reader;
    }

    @When("sinh viên mở thời khóa biểu tuần TC_22")
    public void openWeeklySchedule() {

        page.open();
    }

    @Then("bộ lọc thời khóa biểu tuần TC_22 hiển thị đúng")
    public void verifyFilters() {

        assertTrue(
                page.studentTabSelected(),
                "TC_22: Tab sinh vien chua duoc chon."
        );

        assertEquals(
                "2024-2025",
                page.selectedYear(),
                "TC_22: Sai nam hoc."
        );

        assertEquals(
                "Học kỳ 1",
                page.selectedTerm(),
                "TC_22: Sai hoc ky."
        );

        assertEquals(
                "18/11/2024-24/11/2024",
                page.selectedWeek(),
                "TC_22: Sai tuan."
        );

        assertEquals(
                "47",
                page.selectedWeekCode(),
                "TC_22: Sai ma tuan."
        );

        System.out.println(
                "[TC_22] Bo loc nam, ky, tuan: MATCH."
        );
    }

    @Then("lưới thời khóa biểu TC_22 có đúng ngày và buổi học")
    public void verifyGrid() {

        assertEquals(
                page.expectedHeaders(),
                page.headers(),
                "TC_22: Sai 7 cot ngay trong tuan."
        );

        assertEquals(
                List.of("Sáng", "Chiều", "Tối"),
                page.sessions(),
                "TC_22: Sai 3 buoi hoc."
        );

        System.out.println(
                "[TC_22] 7 ngay va 3 buoi: MATCH."
        );
    }

    @Then("thời khóa biểu tuần TC_22 khớp baseline")
    public void verifyBaseline() {

        List<WeeklyScheduleRecord> expected =
                reader.readRecords();

        StudentWeeklySchedulePage.Snapshot actual =
                page.snapshot();

        WeeklyScheduleComparator.Result result =
                comparator.compare(
                        expected,
                        actual.lessons(),
                        actual.emptyCells(),
                        actual.occupiedCells()
                );

        assertTrue(
                result.passed(),
                "TC_22: Thoi khoa bieu sai baseline. "
                        + result.details()
        );

        System.out.println(
                "[TC_22] 14 luot hoc: MATCH."
        );

        System.out.println(
                "[TC_22] 10 o co lich, 11 o trong: MATCH."
        );

        System.out.println(
                "[TC_22] " + result.details()
        );
    }
}
