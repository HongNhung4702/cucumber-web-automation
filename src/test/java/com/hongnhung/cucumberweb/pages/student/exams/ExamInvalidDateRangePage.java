
package com.hongnhung.cucumberweb.pages.student.exams;

import com.hongnhung.cucumberweb.config.ConfigManager;
import com.hongnhung.cucumberweb.driver.DriverManager;
import com.hongnhung.cucumberweb.pages.BasePage;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import java.util.List;

public final class ExamInvalidDateRangePage extends BasePage {

    public static final String START_DATE = "14/10/2024";
    public static final String END_DATE = "09/09/2023";

    private static final By TIME_TAB = By.xpath(
            "//button[@role='tab' and "
                    + "normalize-space(.)='THỜI GIAN']"
    );

    private static final By DATE_AREA =
            By.id("ExamDetailToTime");

    private static final By START_INPUT = By.xpath(
            "//*[@id='ExamDetailToTime']"
                    + "//label[normalize-space(.)='Từ ngày']"
                    + "/following-sibling::div//input"
    );

    private static final By END_INPUT = By.xpath(
            "//*[@id='ExamDetailToTime']"
                    + "//label[normalize-space(.)='Đến ngày']"
                    + "/following-sibling::div//input"
    );

    private static final By NOT_YET_TABLE = By.xpath(
            "//*[@id='ExamDetailToTime']"
                    + "//*[normalize-space(text())='Lịch chưa thi']"
                    + "/following::table[1]"
    );

    private static final By ALL_TABLE = By.xpath(
            "//*[@id='ExamDetailToTime']"
                    + "//*[normalize-space(text())='Tất cả']"
                    + "/following::table[1]"
    );

    private static final By HEADERS =
            By.cssSelector("thead th");

    private static final By ROWS =
            By.cssSelector("tbody tr");

    private static final By CELLS =
            By.cssSelector("td");

    // Chỉ kiểm tra các thông báo lỗi hiển thị.
    private static final By VISIBLE_ERROR_ALERTS =
            By.cssSelector(
                    "#ExamDetailToTime .MuiAlert-standardError, "
                            + "#ExamDetailToTime .MuiAlert-filledError, "
                            + "#ExamDetailToTime [role='alert']"
            );

    public record TableState(
            int headerCount,
            int bodyRowCount,
            int messageCellCount,
            String message,
            String colspan,
            int dataRecordCount
    ) {
    }

    public ExamInvalidDateRangePage(
            DriverManager driverManager
    ) {
        super(driverManager);
    }

    public void openAndFilter() {

        String baseUrl = ConfigManager.get("base.url")
                .replaceAll("/+$", "");

        driver().navigate().to(
                baseUrl + "/student/exam"
        );

        waitUntilVisible(TIME_TAB);

        if (!timeTabSelected()) {
            click(TIME_TAB);
        }

        waitUntilVisible(DATE_AREA);
        waitUntilVisible(START_INPUT);
        waitUntilVisible(END_INPUT);

        setDate(START_INPUT, START_DATE);
        setDate(END_INPUT, END_DATE);

        waitForEmptyResults();

        System.out.println(
                "[TC_32] Khoang ngay: "
                        + START_DATE + " -> " + END_DATE
        );
    }

    private void setDate(By locator, String expected) {

        WebElement input = waitUntilVisible(locator);

        if (expected.equals(input.getDomProperty("value"))) {
            return;
        }

        input.click();
        input.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        input.sendKeys(expected);
        input.sendKeys(Keys.TAB);

        waitDriver().until(d -> {
            try {
                return expected.equals(
                        d.findElement(locator)
                                .getDomProperty("value")
                );
            } catch (StaleElementReferenceException ex) {
                return false;
            }
        });
    }

    private void waitForEmptyResults() {

        int[] stablePolls = {0};

        waitDriver().until(d -> {
            try {
                if (!timeTabSelected()
                        || !START_DATE.equals(selectedStartDate())
                        || !END_DATE.equals(selectedEndDate())) {

                    stablePolls[0] = 0;
                    return false;
                }

                TableState notYet =
                        readTable(NOT_YET_TABLE);

                TableState all =
                        readTable(ALL_TABLE);

                boolean ready = isEmptyTable(notYet)
                        && isEmptyTable(all);

                if (ready) {
                    stablePolls[0]++;
                } else {
                    stablePolls[0] = 0;
                }

                // Chờ trạng thái kết quả ổn định.
                return stablePolls[0] >= 3;

            } catch (StaleElementReferenceException ex) {
                stablePolls[0] = 0;
                return false;
            }
        });
    }

    public boolean timeTabSelected() {

        return "true".equals(
                driver().findElement(TIME_TAB)
                        .getAttribute("aria-selected")
        );
    }

    public String selectedStartDate() {

        return driver().findElement(START_INPUT)
                .getDomProperty("value");
    }

    public String selectedEndDate() {

        return driver().findElement(END_INPUT)
                .getDomProperty("value");
    }

    public TableState notYetExamState() {
        return readTable(NOT_YET_TABLE);
    }

    public TableState allExamState() {
        return readTable(ALL_TABLE);
    }

    public int visibleErrorAlertCount() {

        int count = 0;

        for (WebElement element :
                driver().findElements(VISIBLE_ERROR_ALERTS)) {

            if (element.isDisplayed()) {
                count++;
            }
        }

        return count;
    }

    private boolean isEmptyTable(TableState state) {

        return state.headerCount() == 9
                && state.bodyRowCount() == 1
                && state.messageCellCount() == 1
                && "Chưa có lịch thi".equals(state.message())
                && "11".equals(state.colspan())
                && state.dataRecordCount() == 0;
    }

    private TableState readTable(By locator) {

        WebElement table = driver().findElement(locator);

        if (!table.isDisplayed()) {
            throw new IllegalStateException(
                    "TC_32: Bang lich thi khong hien thi."
            );
        }

        int headerCount =
                table.findElements(HEADERS).size();

        List<WebElement> rows =
                table.findElements(ROWS);

        int dataCount = 0;
        int messageCellCount = 0;

        String message = "";
        String colspan = "";

        for (WebElement row : rows) {

            List<WebElement> cells =
                    row.findElements(CELLS);

            if (cells.size() == 1
                    && "Chưa có lịch thi".equals(
                    clean(cells.get(0).getText()))) {

                messageCellCount++;

                message = clean(
                        cells.get(0).getText()
                );

                colspan = cells.get(0)
                        .getAttribute("colspan");

            } else {
                // Bat ky dong nao khac deu khong phai
                // dong thong bao rong da xac minh.
                dataCount++;
            }
        }

        return new TableState(
                headerCount,
                rows.size(),
                messageCellCount,
                message,
                colspan,
                dataCount
        );
    }

    private String clean(String text) {

        if (text == null) {
            return "";
        }

        return text.replace('\u00A0', ' ')
                .replace('\u202F', ' ')
                .strip()
                .replaceAll("\\s+", " ");
    }
}
