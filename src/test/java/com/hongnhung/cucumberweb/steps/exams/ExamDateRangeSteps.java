
package com.hongnhung.cucumberweb.steps.exams;

import com.hongnhung.cucumberweb.models.exams.ExamDateRangeRecord;
import com.hongnhung.cucumberweb.pages.student.exams.ExamDateRangePage;
import com.hongnhung.cucumberweb.testdata.exams.ExamDateRangeBaselineReader;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class ExamDateRangeSteps {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/uuuu")
                    .withResolverStyle(ResolverStyle.STRICT);

    private final ExamDateRangePage page;
    private final ExamDateRangeBaselineReader reader;

    public ExamDateRangeSteps(
            ExamDateRangePage page,
            ExamDateRangeBaselineReader reader
    ) {
        this.page = page;
        this.reader = reader;
    }

    @When("sinh viên tra cứu lịch thi theo khoảng ngày TC_31")
    public void openAndFilter() {
        page.openAndFilter();
    }

    @Then("bộ lọc thời gian TC_31 hiển thị đúng khoảng ngày")
    public void verifyDateFilter() {

        assertTrue(
                page.timeTabSelected(),
                "TC_31: Tab Thoi gian chua duoc chon."
        );

        assertEquals(
                "14/10/2024",
                page.selectedStartDate(),
                "TC_31: Sai ngay bat dau."
        );

        assertEquals(
                "09/09/2026",
                page.selectedEndDate(),
                "TC_31: Sai ngay ket thuc."
        );

        assertEquals(
                page.expectedHeaders(),
                page.headers(),
                "TC_31: Sai 9 cot cua bang lich thi."
        );

        System.out.println(
                "[TC_31] Bo loc va 9 cot: MATCH."
        );
    }

    @Then("lịch thi trong khoảng ngày TC_31 khớp baseline")
    public void verifyExamRecords() {

        List<ExamDateRangeRecord> expected =
                reader.readRecords();

        List<ExamDateRangeRecord> actual =
                page.records();

        assertEquals(
                0,
                page.notYetExamCount(),
                "TC_31: Bang Lich chua thi khac baseline."
        );

        assertEquals(
                expected.size(),
                actual.size(),
                "TC_31: Sai so ban ghi."
        );

        // So sánh số lần xuất hiện của mỗi bản ghi,
        // bao gồm kiểm tra thiếu, thừa và trùng.
        assertEquals(
                countRecords(expected),
                countRecords(actual),
                "TC_31: Du lieu lich thi sai baseline."
        );

        LocalDate start = LocalDate.parse(
                page.selectedStartDate(), DATE_FORMAT
        );

        LocalDate end = LocalDate.parse(
                page.selectedEndDate(), DATE_FORMAT
        );

        assertFalse(
                start.isAfter(end),
                "TC_31: Ngay bat dau lon hon ngay ket thuc."
        );

        for (ExamDateRangeRecord record : actual) {

            LocalDate examDate = LocalDate.parse(
                    record.ngayThi(), DATE_FORMAT
            );

            assertFalse(
                    examDate.isBefore(start),
                    "TC_31: Ngay thi truoc khoang: "
                            + record
            );

            assertFalse(
                    examDate.isAfter(end),
                    "TC_31: Ngay thi sau khoang: "
                            + record
            );
        }

        System.out.println(
                "[TC_31] Baseline: MATCH."
        );

        System.out.println(
                "[TC_31] 2 ban ghi trong khoang ngay: MATCH."
        );
    }

    private Map<ExamDateRangeRecord, Integer> countRecords(
            List<ExamDateRangeRecord> records
    ) {

        Map<ExamDateRangeRecord, Integer> counts =
                new HashMap<>();

        for (ExamDateRangeRecord record : records) {
            counts.merge(record, 1, Integer::sum);
        }

        return counts;
    }
}
