
package com.hongnhung.cucumberweb.pages.student.exams;

import com.hongnhung.cucumberweb.config.ConfigManager;
import com.hongnhung.cucumberweb.driver.DriverManager;
import com.hongnhung.cucumberweb.models.exams.ExamDateRangeRecord;
import com.hongnhung.cucumberweb.pages.BasePage;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public final class ExamDateRangePage extends BasePage {

    public static final String START_DATE = "14/10/2024";
    public static final String END_DATE = "09/09/2026";

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

    private static final By ALL_TABLE = By.xpath(
            "//*[@id='ExamDetailToTime']"
                    + "//*[normalize-space(text())='Tất cả']"
                    + "/following::table[1]"
    );

    private static final By NOT_YET_TABLE = By.xpath(
            "//*[@id='ExamDetailToTime']"
                    + "//*[normalize-space(text())='Lịch chưa thi']"
                    + "/following::table[1]"
    );

    private static final By HEADERS = By.cssSelector("thead th");
    private static final By ROWS = By.cssSelector("tbody tr");
    private static final By CELLS = By.cssSelector("td");

    private static final List<String> EXPECTED_HEADERS =
            List.of(
                    "Mã lớp học phần/Tên lớp học phần",
                    "Lần thi",
                    "Ngày thi",
                    "Giờ thi",
                    "Phòng thi",
                    "Địa điểm",
                    "Hình thức thi",
                    "Kỳ thi",
                    "Ghi chú"
            );

    public ExamDateRangePage(DriverManager driverManager) {
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

        // Nhập bằng bàn phím để React nhận sự kiện thay đổi.
        setDate(START_INPUT, START_DATE);
        setDate(END_INPUT, END_DATE);

        // Chờ bộ lọc và bảng kết quả ổn định.
        waitForExpectedTable();

        System.out.println(
                "[TC_31] Da chon khoang "
                        + START_DATE + " - " + END_DATE
        );
    }

    private void setDate(By locator, String expected) {

        WebElement input = waitUntilVisible(locator);

        String current = input.getDomProperty("value");

        if (expected.equals(current)) {
            return;
        }

        input.click();

        input.sendKeys(
                Keys.chord(Keys.CONTROL, "a")
        );

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

    private void waitForExpectedTable() {

        String[] previousSnapshot = {null};
        int[] stablePolls = {0};

        waitDriver().until(d -> {

            try {
                if (!timeTabSelected()) {
                    return false;
                }

                if (!START_DATE.equals(selectedStartDate())
                        || !END_DATE.equals(selectedEndDate())) {
                    return false;
                }

                WebElement table = d.findElement(ALL_TABLE);

                if (!table.isDisplayed()) {
                    return false;
                }

                List<WebElement> rows =
                        table.findElements(ROWS);

                if (rows.size() != 2) {
                    previousSnapshot[0] = null;
                    stablePolls[0] = 0;
                    return false;
                }

                if (table.findElements(HEADERS).size() != 9) {
                    return false;
                }

                for (WebElement row : rows) {
                    if (row.findElements(CELLS).size() != 9) {
                        return false;
                    }
                }

                // Kiểm tra dữ liệu đã xuất hiện thay vì chỉ
                // dựa vào giá trị của hai ô ngày.
                String snapshot = table.getText();

                if (!snapshot.contains("241115042206")
                        || !snapshot.contains("253105019601")) {
                    return false;
                }

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

    public List<String> expectedHeaders() {
        return EXPECTED_HEADERS;
    }

    public List<String> headers() {

        WebElement table = waitUntilVisible(ALL_TABLE);
        List<String> result = new ArrayList<>();

        for (WebElement header : table.findElements(HEADERS)) {
            result.add(clean(header.getText()));
        }

        return List.copyOf(result);
    }

    public int notYetExamCount() {

        WebElement table = waitUntilVisible(NOT_YET_TABLE);

        return table.findElements(ROWS).size();
    }

    public List<ExamDateRangeRecord> records() {

        return waitDriver().until(d -> {

            try {
                WebElement table = d.findElement(ALL_TABLE);

                List<WebElement> rows =
                        table.findElements(ROWS);

                if (rows.size() != 2) {
                    return null;
                }

                List<ExamDateRangeRecord> result =
                        new ArrayList<>();

                for (WebElement row : rows) {

                    List<WebElement> cells =
                            row.findElements(CELLS);

                    if (cells.size() != 9) {
                        throw new IllegalStateException(
                                "TC_31: Dong khong co 9 cot."
                        );
                    }

                    String firstColumn =
                            clean(cells.get(0).getText());

                    int separator = firstColumn.indexOf("-");

                    if (separator <= 0
                            || separator == firstColumn.length() - 1) {

                        throw new IllegalStateException(
                                "TC_31: Khong tach duoc "
                                        + "ma va ten hoc phan: "
                                        + firstColumn
                        );
                    }

                    String code =
                            firstColumn.substring(0, separator);

                    String name =
                            firstColumn.substring(separator + 1);

                    result.add(new ExamDateRangeRecord(
                            code,
                            name,
                            clean(cells.get(1).getText()),
                            clean(cells.get(2).getText()),
                            clean(cells.get(3).getText()),
                            clean(cells.get(4).getText()),
                            clean(cells.get(5).getText()),
                            clean(cells.get(6).getText()),
                            clean(cells.get(7).getText()),
                            clean(cells.get(8).getText())
                    ));
                }

                return List.copyOf(result);

            } catch (StaleElementReferenceException ex) {
                return null;
            }
        });
    }

    private String clean(String text) {

        if (text == null) {
            return "";
        }

        return Normalizer.normalize(
                text.replace('\u00A0', ' ')
                        .replace('\u202F', ' ')
                        .strip()
                        .replaceAll("\\s+", " "),
                Normalizer.Form.NFC
        );
    }
}
