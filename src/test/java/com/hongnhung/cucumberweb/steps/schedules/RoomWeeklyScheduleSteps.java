
package com.hongnhung.cucumberweb.steps.schedules;

import com.hongnhung.cucumberweb.assertions.schedules.RoomScheduleComparator;
import com.hongnhung.cucumberweb.models.schedules.RoomScheduleRecord;
import com.hongnhung.cucumberweb.pages.student.schedules.RoomWeeklySchedulePage;
import com.hongnhung.cucumberweb.testdata.schedules.RoomScheduleBaselineReader;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class RoomWeeklyScheduleSteps {

    private final RoomWeeklySchedulePage page;
    private final RoomScheduleBaselineReader reader;

    private final RoomScheduleComparator comparator =
            new RoomScheduleComparator();

    public RoomWeeklyScheduleSteps(
            RoomWeeklySchedulePage page,
            RoomScheduleBaselineReader reader
    ) {
        this.page = page;
        this.reader = reader;
    }

    @When("sinh viên mở thời khóa biểu theo phòng TC_23")
    public void openRoomSchedule() {
        page.open();
    }

    @Then("bộ lọc thời khóa biểu phòng TC_23 hiển thị đúng")
    public void verifyFilters() {

        assertTrue(
                page.roomTabSelected(),
                "TC_23: Tab thoi khoa bieu theo phong chua duoc chon."
        );

        assertEquals(
                "2024-2025",
                page.selectedYear(),
                "TC_23: Sai nam hoc."
        );

        assertEquals(
                "Học kỳ 1",
                page.selectedTerm(),
                "TC_23: Sai hoc ky."
        );

        assertEquals(
                "09/12/2024-15/12/2024",
                page.selectedWeek(),
                "TC_23: Sai tuan."
        );

        assertEquals(
                "50",
                page.selectedWeekCode(),
                "TC_23: Sai ma tuan."
        );

        System.out.println(
                "[TC_23] Bo loc nam, ky, tuan: MATCH."
        );
    }

    @Then("bảng thời khóa biểu phòng TC_23 có đúng ngày và phòng")
    public void verifyGrid() {

        assertEquals(
                page.expectedHeaders(),
                page.headers(),
                "TC_23: Sai cot ngay."
        );

        assertEquals(
                page.expectedRooms(),
                page.rooms(),
                "TC_23: Sai danh sach phong."
        );

        System.out.println(
                "[TC_23] 7 ngay va 7 phong: MATCH."
        );
    }

    @Then("thời khóa biểu theo phòng TC_23 khớp baseline")
    public void verifyBaseline() {

        List<RoomScheduleRecord> expected =
                reader.readRecords();

        RoomWeeklySchedulePage.Snapshot actual =
                page.snapshot();

        RoomScheduleComparator.Result result =
                comparator.compare(
                        expected,
                        actual.lessons(),
                        actual.emptyCells(),
                        actual.occupiedCells()
                );

        assertTrue(
                result.passed(),
                "TC_23: Lich phong sai baseline. "
                        + result.details()
        );

        System.out.println(
                "[TC_23] 8 luot hoc: MATCH."
        );

        System.out.println(
                "[TC_23] 8 o co lich, 41 o trong: MATCH."
        );

        System.out.println(
                "[TC_23] " + result.details()
        );
    }
}
