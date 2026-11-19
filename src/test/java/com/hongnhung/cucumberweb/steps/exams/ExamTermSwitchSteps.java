
package com.hongnhung.cucumberweb.steps.exams;

import com.hongnhung.cucumberweb.assertions.exams.ExamScheduleComparator;
import com.hongnhung.cucumberweb.models.exams.ExamRecord;
import com.hongnhung.cucumberweb.pages.student.exams.ExamTermSwitchPage;
import com.hongnhung.cucumberweb.testdata.exams.ExamBaselineReader;
import com.hongnhung.cucumberweb.testdata.exams.ExamTermBaselineReader;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class ExamTermSwitchSteps {

    private static final List<String> UI_HEADERS =
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

    private final ExamTermSwitchPage page;
    private final ExamBaselineReader initialReader;
    private final ExamTermBaselineReader targetReader;

    private final ExamScheduleComparator comparator =
            new ExamScheduleComparator();

    private List<ExamRecord> expectedInitial;
    private List<ExamRecord> actualInitial;

    private List<ExamRecord> expectedTarget;
    private List<ExamRecord> actualTarget;

    public ExamTermSwitchSteps(
            ExamTermSwitchPage page,
            ExamBaselineReader initialReader,
            ExamTermBaselineReader targetReader
    ) {
        this.page = page;
        this.initialReader = initialReader;
        this.targetReader = targetReader;
    }

    @When("sinh viên mở lịch thi học kỳ ban đầu TC_30")
    public void openInitialTerm() {

        expectedInitial = initialReader.readTc28();
        expectedTarget = targetReader.readTargetTerm();

        page.openInitialTerm();
        actualInitial = page.records();
    }

    @Then("lịch thi học kỳ ban đầu TC_30 khớp baseline")
    public void verifyInitialTerm() {

        assertTrue(
                page.tabSelected(),
                "TC_30: Sai tab tra cuu."
        );

        assertEquals(
                "2022-2023",
                page.selectedYear(),
                "TC_30: Sai nam hoc."
        );

        assertEquals(
                "Học kỳ 1",
                page.selectedTerm(),
                "TC_30: Sai hoc ky ban dau."
        );

        assertEquals(
                "HK01",
                page.selectedTermCode(),
                "TC_30: Sai ma hoc ky ban dau."
        );

        assertEquals(
                UI_HEADERS,
                page.headers(),
                "TC_30: Sai cau truc bang."
        );

        assertEquals(
                5,
                actualInitial.size(),
                "TC_30: HK1 phai co 5 ban ghi."
        );

        compareRecords(
                expectedInitial,
                actualInitial,
                "HK1"
        );
    }

    @When("sinh viên đổi lịch thi sang học kỳ 2 TC_30")
    public void changeTerm() {

        page.switchToTargetTerm();
        actualTarget = page.records();
    }

    @Then("lịch thi học kỳ 2 TC_30 khớp bộ lọc và baseline")
    public void verifyTargetTerm() {

        assertTrue(
                page.tabSelected(),
                "TC_30: Tab tra cuu bi thay doi."
        );

        assertEquals(
                "2022-2023",
                page.selectedYear(),
                "TC_30: Nam hoc bi thay doi."
        );

        assertEquals(
                "2022-2023",
                page.selectedYearCode(),
                "TC_30: Ma nam hoc bi thay doi."
        );

        assertEquals(
                "Học kỳ 2",
                page.selectedTerm(),
                "TC_30: Hoc ky khong duoc cap nhat."
        );

        assertEquals(
                "HK02",
                page.selectedTermCode(),
                "TC_30: Ma hoc ky khong dung."
        );

        assertEquals(
                UI_HEADERS,
                page.headers(),
                "TC_30: Sai tieu de cot sau khi doi hoc ky."
        );

        assertEquals(
                4,
                actualTarget.size(),
                "TC_30: HK2 phai co 4 ban ghi."
        );

        compareRecords(
                expectedTarget,
                actualTarget,
                "HK2"
        );

        System.out.println(
                "[TC_30] Chuyen HK1 sang HK2: PASS."
        );
    }

    private void compareRecords(
            List<ExamRecord> expected,
            List<ExamRecord> actual,
            String stage
    ) {

        ExamScheduleComparator.Result result =
                comparator.compare(expected, actual);

        assertTrue(
                result.passed(),
                "TC_30 - " + stage
                        + ": Du lieu khong khop baseline. "
                        + result.details()
        );

        System.out.println(
                "[TC_30] " + stage
                        + " - " + result.details()
        );
    }
}
