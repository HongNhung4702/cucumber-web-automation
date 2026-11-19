
package com.hongnhung.cucumberweb.steps.schedules;

import com.hongnhung.cucumberweb.models.schedules.PrintScheduleRecord;
import com.hongnhung.cucumberweb.pages.student.schedules.StudentSchedulePrintPage;
import com.hongnhung.cucumberweb.testdata.schedules.PrintScheduleBaselineReader;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class StudentSchedulePrintSteps {

    private final StudentSchedulePrintPage page;
    private final PrintScheduleBaselineReader reader;

    public StudentSchedulePrintSteps(
            StudentSchedulePrintPage page,
            PrintScheduleBaselineReader reader
    ) {
        this.page = page;
        this.reader = reader;
    }

    @When("sinh viên mở bản xem trước thời khóa biểu TC_26")
    public void openPrintPreview() {
        page.open();
    }

    @Then("bản xem trước TC_26 hiển thị đúng tiêu đề và chức năng in")
    public void verifyPreview() {

        assertTrue(
                page.studentTabSelected(),
                "TC_26: Sai tab thoi khoa bieu."
        );

        assertEquals(
                "2022-2023",
                page.selectedYear(),
                "TC_26: Sai nam hoc."
        );

        assertEquals(
                "Học kỳ 2",
                page.selectedTerm(),
                "TC_26: Sai hoc ky."
        );

        assertEquals(
                "XEM THỜI KHÓA BIỂU",
                page.dialogTitle(),
                "TC_26: Sai tieu de hop thoai."
        );

        assertEquals(
                "THỜI KHÓA BIỂU SINH VIÊN",
                page.previewTitle(),
                "TC_26: Sai tieu de ban xem truoc."
        );

        assertTrue(
                page.printActionVisible(),
                "TC_26: Khong hien thi nut In thoi khoa bieu."
        );

        assertEquals(
                page.expectedHeaders(),
                page.headers(),
                "TC_26: Sai 10 cot bang xem truoc."
        );

        System.out.println(
                "[TC_26] Bo loc, tieu de va nut in: MATCH."
        );
    }

    @Then("dữ liệu bản xem trước TC_26 khớp baseline")
    public void verifyPreviewData() {

        List<PrintScheduleRecord> expected =
                reader.readRecords();

        List<PrintScheduleRecord> actual =
                page.records();

        assertEquals(
                16,
                actual.size(),
                "TC_26: Sai so dong lich hoc."
        );

        assertEquals(
                7L,
                actual.stream()
                        .map(PrintScheduleRecord::stt)
                        .distinct()
                        .count(),
                "TC_26: Sai so hoc phan."
        );

        List<String> differences = new ArrayList<>();

        int length = Math.min(
                expected.size(), actual.size()
        );

        for (int i = 0; i < length; i++) {

            if (!expected.get(i).equals(actual.get(i))) {

                differences.add(
                        "Dong " + (i + 1)
                                + ": expected=" + expected.get(i)
                                + ", actual=" + actual.get(i)
                );
            }
        }

        assertTrue(
                differences.isEmpty(),
                "TC_26: Ban xem truoc sai baseline. "
                        + String.join("\n", differences)
        );

        System.out.println(
                "[TC_26] Baseline: MATCH."
        );

        System.out.println(
                "[TC_26] 7 hoc phan, 16 dong lich: MATCH."
        );
    }
}
