
package com.hongnhung.cucumberweb.steps.graduation;

import com.hongnhung.cucumberweb.assertions.graduation.GraduationResultComparator;
import com.hongnhung.cucumberweb.models.graduation.GraduationResultRecord;
import com.hongnhung.cucumberweb.pages.student.graduation.GraduationResultPage;
import com.hongnhung.cucumberweb.testdata.graduation.GraduationResultBaselineReader;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class GraduationResultSteps {

    private final GraduationResultPage page;
    private final GraduationResultBaselineReader reader;

    private final GraduationResultComparator comparator =
            new GraduationResultComparator();

    private List<GraduationResultRecord> expected;
    private List<GraduationResultRecord> actual;

    public GraduationResultSteps(
            GraduationResultPage page,
            GraduationResultBaselineReader reader
    ) {
        this.page = page;
        this.reader = reader;
    }

    // TC_19: Mo hop thoai
    @When("sinh viên mở kết quả xét tốt nghiệp")
    public void openGraduationResults() {

        expected = null;
        actual = null;

        page.open();
    }

    // TC_19: Kiem tra ngữ canh
    @Then("hộp thoại kết quả xét tốt nghiệp hiển thị đúng chương trình")
    public void verifyDialogAndProgram() {

        assertEquals(
                "Kỹ thuật phần mềm",
                page.selectedProgram(),
                "TC_19: Sai chuong trinh dao tao."
        );

        assertEquals(
                "119DHCQK45",
                page.selectedProgramCode(),
                "TC_19: Sai ma chuong trinh."
        );

        assertEquals(
                "KẾT QUẢ XÉT TỐT NGHIỆP",
                page.dialogTitle(),
                "TC_19: Sai tieu de hop thoai."
        );

        System.out.println(
                "[TC_19] Chuong trinh va tieu de: MATCH."
        );
    }

    // TC_19: Kiem tra cot
    @Then("bảng kết quả xét tốt nghiệp có đúng các cột")
    public void verifyHeaders() {

        assertEquals(
                page.expectedHeaders(),
                page.headers(),
                "TC_19: Sai tieu de cot."
        );

        System.out.println(
                "[TC_19] Header: MATCH."
        );
    }

    // TC_19: Doi chieu toan bo
    @Then("dữ liệu kết quả xét tốt nghiệp khớp baseline TC_19")
    public void verifyBaseline() {

        if (expected == null) {
            expected = reader.readRecords();
        }

        if (actual == null) {
            actual = page.records();
        }

        GraduationResultComparator.Result result =
                comparator.compare(expected, actual);

        assertEquals(
                2,
                actual.size(),
                "TC_19: Sai so dong du lieu."
        );

        assertTrue(
                result.passed(),
                "TC_19: Du lieu sai lech. "
                        + result.details()
        );

        System.out.println(
                "[TC_19] Baseline: MATCH."
        );

        System.out.println(
                "[TC_19] " + result.details()
        );
    }
}
