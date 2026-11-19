
package com.hongnhung.cucumberweb.pages.student.exams;

import com.hongnhung.cucumberweb.driver.DriverManager;
import com.hongnhung.cucumberweb.models.exams.ExamRecord;
import com.hongnhung.cucumberweb.pages.BasePage;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import java.util.List;

public final class ExamYearSwitchPage extends BasePage {

    public static final String INITIAL_YEAR = "2023-2024";
    public static final String TARGET_YEAR = "2022-2023";
    public static final String TERM = "Học kỳ 1";

    private static final By ALL_TABLE = By.xpath(
            "//*[@id='ExamDetailToYearAndTerm']"
                    + "//*[normalize-space(text())='Tất cả']"
                    + "/following::table[1]"
    );

    private static final By BODY_ROWS =
            By.cssSelector("tbody tr");

    private static final By BODY_CELLS =
            By.cssSelector("td");

    private final ExamSchedulePage examPage;

    public ExamYearSwitchPage(
            DriverManager driverManager,
            ExamSchedulePage examPage
    ) {
        super(driverManager);
        this.examPage = examPage;
    }

    // Mở lịch thi và chọn trạng thái ban đầu.
    public void openInitialYear() {

        examPage.open();
        examPage.selectYearSemesterTab();

        // Chọn học kỳ trước, sau đó chọn năm học.
        examPage.choose("Học kỳ", TERM);
        examPage.choose("Năm học", INITIAL_YEAR);

        waitForState(INITIAL_YEAR, TERM, 3);

        System.out.println(
                "[TC_29] Da mo nam 2023-2024, HK1."
        );
    }

    // Đổi trực tiếp năm học, không tải lại trang.
    public void switchToTargetYear() {

        assertSelected(INITIAL_YEAR, TERM);

        examPage.choose("Năm học", TARGET_YEAR);

        waitForState(TARGET_YEAR, TERM, 5);

        System.out.println(
                "[TC_29] Da doi sang nam 2022-2023, HK1."
        );
    }

    public String selectedYear() {
        return examPage.selectedValue("Năm học");
    }

    public String selectedTerm() {
        return examPage.selectedValue("Học kỳ");
    }

    public boolean yearSemesterTabSelected() {
        return examPage.isYearSemesterTabSelected();
    }

    public List<String> headers() {
        return examPage.headers();
    }

    public List<ExamRecord> records() {
        return examPage.records();
    }

    public void assertSelected(
            String expectedYear,
            String expectedTerm
    ) {

        if (!expectedYear.equals(selectedYear())
                || !expectedTerm.equals(selectedTerm())) {

            throw new IllegalStateException(
                    "TC_29: Sai bo loc. "
                            + "Expected=" + expectedYear
                            + ", " + expectedTerm
                            + "; Actual=" + selectedYear()
                            + ", " + selectedTerm()
            );
        }
    }

    private void waitForState(
            String year,
            String term,
            int expectedRows
    ) {

        String[] previousSnapshot = {null};
        int[] stablePolls = {0};

        waitDriver().until(d -> {

            try {
                if (!examPage.isYearSemesterTabSelected()) {
                    return false;
                }

                if (!year.equals(selectedYear())
                        || !term.equals(selectedTerm())) {
                    return false;
                }

                WebElement table =
                        d.findElement(ALL_TABLE);

                if (!table.isDisplayed()) {
                    return false;
                }

                List<WebElement> rows =
                        table.findElements(BODY_ROWS);

                if (rows.size() != expectedRows) {
                    previousSnapshot[0] = null;
                    stablePolls[0] = 0;
                    return false;
                }

                for (WebElement row : rows) {
                    if (row.findElements(BODY_CELLS).size() != 10) {
                        return false;
                    }
                }

                String snapshot = table.getText();

                if (snapshot.equals(previousSnapshot[0])) {
                    stablePolls[0]++;
                } else {
                    stablePolls[0] = 0;
                }

                previousSnapshot[0] = snapshot;

                return stablePolls[0] >= 2;

            } catch (StaleElementReferenceException ex) {
                previousSnapshot[0] = null;
                stablePolls[0] = 0;
                return false;
            }
        });
    }
}
