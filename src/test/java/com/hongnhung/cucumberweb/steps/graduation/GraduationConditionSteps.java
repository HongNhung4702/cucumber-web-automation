
package com.hongnhung.cucumberweb.steps.graduation;

import com.hongnhung.cucumberweb.assertions.graduation.GraduationConditionComparator;
import com.hongnhung.cucumberweb.models.graduation.GraduationConditionRecord;
import com.hongnhung.cucumberweb.pages.student.graduation.GraduationConditionPage;
import com.hongnhung.cucumberweb.testdata.graduation.GraduationConditionBaselineReader;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class GraduationConditionSteps {

    private final GraduationConditionPage page;
    private final GraduationConditionBaselineReader reader;

    private final GraduationConditionComparator comparator =
            new GraduationConditionComparator();

    private List<GraduationConditionRecord> expected;
    private List<GraduationConditionRecord> actual;

    public GraduationConditionSteps(
            GraduationConditionPage page,
            GraduationConditionBaselineReader reader
    ) {
        this.page = page;
        this.reader = reader;
    }

    @When("sinh viên mở điều kiện tốt nghiệp")
    public void openGraduationConditions() {

        expected = null;
        actual = null;

        page.open();
    }

    @Then("hộp thoại điều kiện tốt nghiệp hiển thị đúng chương trình")
    public void verifyDialogAndProgram() {

        assertEquals(
                "Kỹ thuật phần mềm",
                page.selectedProgram(),
                "TC_18: Sai chuong trinh dao tao."
        );

        assertEquals(
                "119DHCQK45",
                page.selectedProgramCode(),
                "TC_18: Sai ma chuong trinh."
        );

        assertEquals(
                "Điều kiện tốt nghiệp",
                page.dialogTitle(),
                "TC_18: Sai tieu de hop thoai."
        );

        System.out.println(
                "[TC_18] Chuong trinh va tieu de: MATCH."
        );
    }

    @Then("bảng điều kiện tốt nghiệp có đúng các cột")
    public void verifyHeaders() {

        assertEquals(
                page.expectedHeaders(),
                page.headers(),
                "TC_18: Sai tieu de cot."
        );

        System.out.println(
                "[TC_18] Header: MATCH."
        );
    }

    @Then("dữ liệu điều kiện tốt nghiệp khớp baseline TC_18")
    public void verifyBaseline() {

        if (expected == null) {
            expected = reader.readRecords();
        }

        if (actual == null) {
            actual = page.records();
        }

        GraduationConditionComparator.Result result =
                comparator.compare(expected, actual);

        assertEquals(
                7,
                actual.size(),
                "TC_18: Sai so dong du lieu."
        );

        assertTrue(
                result.passed(),
                "TC_18: Du lieu sai lech. "
                        + result.details()
        );

        System.out.println(
                "[TC_18] Baseline: MATCH."
        );

        System.out.println(
                "[TC_18] " + result.details()
        );
    }
}
