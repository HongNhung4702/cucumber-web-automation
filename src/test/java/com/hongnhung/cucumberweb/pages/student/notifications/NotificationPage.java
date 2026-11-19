
package com.hongnhung.cucumberweb.pages.student.notifications;

import com.hongnhung.cucumberweb.config.ConfigManager;
import com.hongnhung.cucumberweb.driver.DriverManager;
import com.hongnhung.cucumberweb.models.notifications.NotificationRecord;
import com.hongnhung.cucumberweb.pages.BasePage;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public final class NotificationPage extends BasePage {

    private static final By PERSONAL_TAB =
            By.xpath(
                    "//button[@role='tab' "
                            + "and normalize-space(.)='Thông báo cá nhân']"
            );

    private static final By SEARCH_INPUT =
            By.cssSelector(
                    "#ThongBao input[placeholder='Tìm kiếm thông báo']"
            );

    private static final By TABLE =
            By.xpath(
                    "//table["
                            + ".//thead//th[normalize-space(.)='Tiêu đề']"
                            + " and "
                            + ".//thead//th[normalize-space(.)='Người gửi']"
                            + " and "
                            + ".//thead//th[normalize-space(.)='Thời gian gửi']"
                            + "]"
            );

    private static final By HEADERS =
            By.cssSelector("thead th");

    private static final By ROWS =
            By.cssSelector("tbody tr");

    private static final By CELLS =
            By.cssSelector("td");

    private static final List<String> EXPECTED_HEADERS =
            List.of(
                    "Tiêu đề",
                    "Người gửi",
                    "Thời gian gửi"
            );

    private static final String EMPTY_TEXT = "...";

    public NotificationPage(DriverManager driverManager) {
        super(driverManager);
    }

    // =========================================
    // 1. MO THONG BAO CA NHAN
    // =========================================

    public void open() {

        String baseUrl =
                ConfigManager.get("base.url")
                        .replaceAll("/+$", "");

        driver().navigate().to(
                baseUrl + "/student/index"
        );

        click(PERSONAL_TAB);

        waitDriver().until(webDriver -> {

            WebElement tab =
                    webDriver.findElement(PERSONAL_TAB);

            return "true".equals(
                    tab.getDomAttribute("aria-selected")
            );
        });

        waitUntilVisible(TABLE);

        System.out.println(
                "[Notifications] Da mo Thong bao ca nhan."
        );
    }

    // =========================================
    // 2. KIEM TRA HEADER
    // =========================================

    public List<String> expectedHeaders() {
        return EXPECTED_HEADERS;
    }

    public List<String> headers() {

        WebElement table = waitUntilVisible(TABLE);

        List<String> result = new ArrayList<>();

        for (WebElement header :
                table.findElements(HEADERS)) {

            result.add(clean(header.getText()));
        }

        return List.copyOf(result);
    }

    // =========================================
    // 3. DOC DANH SACH THONG BAO
    // =========================================

    public List<NotificationRecord> records() {

        return waitDriver().until(webDriver -> {

            try {
                return readSnapshot();

            } catch (StaleElementReferenceException ex) {
                return null;
            }
        });
    }

    // =========================================
    // 4. TIM KIEM THONG BAO - TC_11
    // =========================================

    public void search(String keyword) {

        if (keyword == null || keyword.isBlank()) {
            throw new IllegalArgumentException(
                    "Tu khoa tim kiem khong duoc rong."
            );
        }

        List<NotificationRecord> before = records();

        type(SEARCH_INPUT, keyword);

        waitDriver().until(webDriver -> {

            WebElement input =
                    webDriver.findElement(SEARCH_INPUT);

            return keyword.equals(
                    input.getDomProperty("value")
            );
        });

        waitDriver().until(webDriver -> {

            try {

                List<NotificationRecord> current =
                        readSnapshot();

                if (current == null) {
                    return false;
                }

                return !current.equals(before);

            } catch (StaleElementReferenceException ex) {
                return false;
            }
        });

        System.out.println(
                "[Notifications] Tu khoa: " + keyword
        );
    }

    public String searchKeyword() {

        return waitUntilVisible(SEARCH_INPUT)
                .getDomProperty("value");
    }

    // =========================================
    // 5. TRANG THAI RONG - TC_12
    // =========================================

    public boolean waitForEmptyState() {

        return waitDriver().until(webDriver -> {

            try {

                WebElement table =
                        webDriver.findElement(TABLE);

                if (!table.isDisplayed()) {
                    return false;
                }

                List<WebElement> rows =
                        table.findElements(ROWS);

                if (rows.size() != 1) {
                    return false;
                }

                List<WebElement> cells =
                        rows.get(0).findElements(CELLS);

                if (cells.size() != 1) {
                    return false;
                }

                WebElement cell = cells.get(0);

                return EMPTY_TEXT.equals(clean(cell.getText()))
                        && "8".equals(
                        cell.getDomAttribute("colspan")
                );

            } catch (StaleElementReferenceException ex) {
                return false;
            }
        });
    }

    // =========================================
    // 6. THU THAP DU LIEU BANG
    // =========================================

    private List<NotificationRecord> readSnapshot() {

        WebElement table =
                driver().findElement(TABLE);

        if (!table.isDisplayed()) {
            return null;
        }

        List<WebElement> rows =
                table.findElements(ROWS);

        if (rows.isEmpty()) {
            return null;
        }

        // Dong trang thai rong cua website:
        // <tr><td colspan="8">...</td></tr>
        if (rows.size() == 1) {

            List<WebElement> firstRowCells =
                    rows.get(0).findElements(CELLS);

            if (firstRowCells.size() == 1) {

                WebElement cell = firstRowCells.get(0);

                if (EMPTY_TEXT.equals(clean(cell.getText()))
                        && "8".equals(
                        cell.getDomAttribute("colspan")
                )) {

                    return List.of();
                }
            }
        }

        List<NotificationRecord> result =
                new ArrayList<>();

        for (WebElement row : rows) {

            List<WebElement> cells =
                    row.findElements(CELLS);

            if (cells.size() != 3) {

                throw new IllegalStateException(
                        "Bang thong bao co dong "
                                + cells.size()
                                + " cot, mong doi 3."
                );
            }

            String title =
                    clean(cells.get(0).getText());

            String sender =
                    clean(cells.get(1).getText());

            String date =
                    clean(cells.get(2).getText());

            if (title.isEmpty()
                    || sender.isEmpty()
                    || date.isEmpty()) {

                throw new IllegalStateException(
                        "Co dong thong bao thieu du lieu."
                );
            }

            result.add(
                    new NotificationRecord(
                            title,
                            sender,
                            date
                    )
            );
        }

        return List.copyOf(result);
    }

    // =========================================
    // 7. CHUAN HOA KHOANG TRANG
    // =========================================

    private String clean(String value) {

        return value == null
                ? ""
                : value.replace('\u00A0', ' ')
                .strip()
                .replaceAll("\\s+", " ");
    }
}
