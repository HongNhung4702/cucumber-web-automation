
package com.hongnhung.cucumberweb.steps.exams;

import com.hongnhung.cucumberweb.pages.student.exams.ExamInvalidDateRangePage;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class ExamInvalidDateRangeSteps {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/uuuu")
                    .withResolverStyle(ResolverStyle.STRICT);

    private final ExamInvalidDateRangePage page;

    public ExamInvalidDateRangeSteps(
            ExamInvalidDateRangePage page
    ) {
        this.page = page;
    }

    @When("sinh viên tra cứu lịch thi với khoảng ngày không hợp lệ TC_32")
    public void searchInvalidDateRange() {

        page.openAndFilter();
    }

    @Then("bộ lọc TC_32 giữ nguyên khoảng ngày bắt đầu lớn hơn ngày kết thúc")
    public void verifyInvalidDates() {

        assertTrue(
                page.timeTabSelected(),
                "TC_32: Tab Thoi gian chua duoc chon."
        );

        assertEquals(
                "14/10/2024",
                page.selectedStartDate(),
                "TC_32: Sai ngay bat dau."
        );

        assertEquals(
                "09/09/2023",
                page.selectedEndDate(),
                "TC_32: Sai ngay ket thuc."
        );

        LocalDate start = LocalDate.parse(
                page.selectedStartDate(),
                DATE_FORMAT
        );

        LocalDate end = LocalDate.parse(
                page.selectedEndDate(),
                DATE_FORMAT
        );

        assertTrue(
                start.isAfter(end),
                "TC_32: Ngay bat dau phai lon hon ngay ket thuc."
        );

        System.out.println(
                "[TC_32] Khoang ngay khong hop le: MATCH."
        );
    }

    @Then("hai bảng lịch thi TC_32 hiển thị trạng thái không có lịch")
    public void verifyEmptyExamTables() {

        assertEmpty(
                page.notYetExamState(),
                "Lich chua thi"
        );

        assertEmpty(
                page.allExamState(),
                "Tat ca"
        );

        assertEquals(
                0,
                page.visibleErrorAlertCount(),
                "TC_32: Xuat hien thong bao loi bat thuong."
        );

        System.out.println(
                "[TC_32] Hai bang: Chua co lich thi."
        );

        System.out.println(
                "[TC_32] Khong co ban ghi lich thi: MATCH."
        );
    }

    private void assertEmpty(
            ExamInvalidDateRangePage.TableState state,
            String tableName
    ) {

        assertEquals(
                9,
                state.headerCount(),
                "TC_32: Sai so cot bang " + tableName
        );

        assertEquals(
                1,
                state.bodyRowCount(),
                "TC_32: Sai so dong bang " + tableName
        );

        assertEquals(
                1,
                state.messageCellCount(),
                "TC_32: Thieu dong thong bao rong tai "
                        + tableName
        );

        assertEquals(
                "Chưa có lịch thi",
                state.message(),
                "TC_32: Sai thong bao tai " + tableName
        );

        assertEquals(
                "11",
                state.colspan(),
                "TC_32: Sai colspan tai " + tableName
        );

        assertEquals(
                0,
                state.dataRecordCount(),
                "TC_32: Van con du lieu lich thi tai "
                        + tableName
        );
    }
}
