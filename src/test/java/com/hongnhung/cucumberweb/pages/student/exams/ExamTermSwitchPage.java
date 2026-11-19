
package com.hongnhung.cucumberweb.pages.student.exams;

import com.hongnhung.cucumberweb.driver.DriverManager;
import com.hongnhung.cucumberweb.models.exams.ExamRecord;
import com.hongnhung.cucumberweb.pages.BasePage;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import java.util.List;

public final class ExamTermSwitchPage extends BasePage {

    public static final String YEAR = "2022-2023";
    public static final String INITIAL_TERM = "Học kỳ 1";
    public static final String TARGET_TERM = "Học kỳ 2";

    private static final By ALL_TABLE = By.xpath(
            "//*[@id='ExamDetailToYearAndTerm']"
                    + "//*[normalize-space(text())='Tất cả']"
                    + "/following::table[1]"
    );

    private static final By YEAR_INPUT =
            By.cssSelector(
                    "#ExamDetailToYearAndTerm input[name='YearStudy']"
            );

    private static final By TERM_INPUT =
            By.cssSelector(
                    "#ExamDetailToYearAndTerm input[name='TermID']"
            );

    private static final By BODY_ROWS =
            By.cssSelector("tbody tr");

    private static final By BODY_CELLS =
            By.cssSelector("td");

    private final ExamSchedulePage examPage;

    public ExamTermSwitchPage(
            DriverManager driverManager,
            ExamSchedulePage examPage
    ) {
        super(driverManager);
        this.examPage = examPage;
    }

    // Mo nam 2022-2023, HK1.
    public void openInitialTerm() {

        examPage.open();
        examPage.selectYearSemesterTab();

        examPage.choose("Năm học", YEAR);
        examPage.choose("Học kỳ", INITIAL_TERM);

        waitForState(INITIAL_TERM, "HK01", 5);

        System.out.println(
                "[TC_30] Da mo nam 2022-2023, HK1."
        );
    }

    // Chi thay doi hoc ky, khong tai lai trang.
    public void switchToTargetTerm() {

        assertSelected(INITIAL_TERM, "HK01");

        examPage.choose("Học kỳ", TARGET_TERM);

        waitForState(TARGET_TERM, "HK02", 4);

        System.out.println(
                "[TC_30] Da chuyen tu HK1 sang HK2."
        );
    }

    public String selectedYear() {
        return examPage.selectedValue("Năm học");
    }

    public String selectedTerm() {
        return examPage.selectedValue("Học kỳ");
    }

    public String selectedYearCode() {
        return driver().findElement(YEAR_INPUT)
                .getDomProperty("value");
    }

    public String selectedTermCode() {
        return driver().findElement(TERM_INPUT)
                .getDomProperty("value");
    }

    public boolean tabSelected() {
        return examPage.isYearSemesterTabSelected();
    }

    public List<String> headers() {
        return examPage.headers();
    }

    public List<ExamRecord> records() {
        return examPage.records();
    }

    private void assertSelected(
            String expectedTerm,
            String expectedTermCode
    ) {

        if (!YEAR.equals(selectedYear())
                || !YEAR.equals(selectedYearCode())
                || !expectedTerm.equals(selectedTerm())
                || !expectedTermCode.equals(selectedTermCode())) {

            throw new IllegalStateException(
                    "TC_30: Sai bo loc truoc khi chuyen hoc ky. "
                            + "Nam=" + selectedYear()
                            + ", Ky=" + selectedTerm()
                            + ", Ma ky=" + selectedTermCode()
            );
        }
    }

    private void waitForState(
            String expectedTerm,
            String expectedTermCode,
            int expectedRows
    ) {

        String[] previousSnapshot = {null};
        int[] stablePolls = {0};

        waitDriver().until(d -> {

            try {
                if (!examPage.isYearSemesterTabSelected()) {
                    return false;
                }

                if (!YEAR.equals(selectedYear())
                        || !YEAR.equals(selectedYearCode())
                        || !expectedTerm.equals(selectedTerm())
                        || !expectedTermCode.equals(
                        selectedTermCode())) {
                    return false;
                }

                WebElement table = d.findElement(ALL_TABLE);

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
