
package com.hongnhung.cucumberweb.steps.progress;

import com.hongnhung.cucumberweb.assertions.progress.StudyProgressComparator;
import com.hongnhung.cucumberweb.models.progress.StudyProgressRecord;
import com.hongnhung.cucumberweb.pages.student.progress.StudyProgressPage;
import com.hongnhung.cucumberweb.testdata.progress.StudyProgressBaselineReader;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class StudyProgressSteps {

    private final StudyProgressPage page;
    private final StudyProgressBaselineReader reader;

    private final StudyProgressComparator comparator =
            new StudyProgressComparator();

    public StudyProgressSteps(
            StudyProgressPage page,
            StudyProgressBaselineReader reader
    ) {
        this.page = page;
        this.reader = reader;
    }

    @When("sinh viên mở kiểm tra tiến độ học tập")
    public void openStudyProgress() {
        page.open();
    }

    @Then("hộp thoại tiến độ học tập hiển thị đúng chương trình")
    public void verifyDialogAndProgram() {

        assertEquals(
                "Kỹ thuật phần mềm",
                page.selectedProgram(),
                "TC_20: Sai ten chuong trinh dao tao."
        );

        assertEquals(
                "119DHCQK45",
                page.selectedProgramCode(),
                "TC_20: Sai ma chuong trinh dao tao."
        );

        assertEquals(
                "KIỂM TRA TIẾN ĐỘ HỌC TẬP",
                page.dialogTitle(),
                "TC_20: Sai tieu de hop thoai."
        );

        System.out.println(
                "[TC_20] Chuong trinh va tieu de: MATCH."
        );
    }

    @Then("bảng tiến độ học tập có đúng các cột")
    public void verifyHeaders() {

        assertEquals(
                page.expectedHeaders(),
                page.headers(),
                "TC_20: Sai tieu de cot."
        );

        System.out.println(
                "[TC_20] Header: MATCH."
        );
    }

    @Then("dữ liệu tiến độ học tập khớp baseline TC_20")
    public void verifyBaseline() {

        List<StudyProgressRecord> expected =
                reader.readRecords();

        List<StudyProgressRecord> actual =
                page.records();

        assertEquals(
                11,
                actual.size(),
                "TC_20: Sai so dong. "
                        + "Yeu cau 9 hoc ky, 1 tong cong, 1 ghi chu."
        );

        StudyProgressComparator.Result result =
                comparator.compare(expected, actual);

        assertTrue(
                result.passed(),
                "TC_20: Du lieu sai lech. "
                        + result.details()
        );

        System.out.println(
                "[TC_20] Baseline: MATCH."
        );

        System.out.println(
                "[TC_20] 9 hoc ky, tong cong va ghi chu: MATCH."
        );

        System.out.println(
                "[TC_20] " + result.details()
        );
    }
}
