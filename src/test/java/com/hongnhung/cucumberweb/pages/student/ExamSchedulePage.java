
package com.hongnhung.cucumberweb.pages.student;

import com.hongnhung.cucumberweb.config.ConfigManager;
import com.hongnhung.cucumberweb.driver.DriverManager;
import com.hongnhung.cucumberweb.models.exams.ExamRecord;
import com.hongnhung.cucumberweb.pages.BasePage;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ExamSchedulePage extends BasePage {

    private static final String PATH = "/student/exam";

    // Locator xác nhận từ DOM thực tế.
    private static final By YEAR_SEMESTER_TAB = By.xpath(
            "//*[@role='tab' and "
                    + "(contains(normalize-space(.),'Năm học') or "
                    + "contains(normalize-space(.),'NĂM HỌC'))]"
    );

    private static final By ALL_TABLE = By.xpath(
            "(//*[normalize-space(text())='Tất cả']"
                    + "/following::table[1])[1]"
    );

    private static final By HEADER_CELLS =
            By.cssSelector("thead th");

    private static final By BODY_ROWS =
            By.cssSelector("tbody tr");

    private static final By BODY_CELLS =
            By.cssSelector("td");

    private static final Pattern CLASS_AND_NAME =
            Pattern.compile("^(\\d+)\\s*-\\s*(.+)$");

    public ExamSchedulePage(DriverManager driverManager) {
        super(driverManager);
    }

    // =========================================
    // 1. MỞ TRANG LỊCH THI
    // =========================================

    public void open() {

        String baseUrl = ConfigManager.get("base.url")
                .replaceAll("/+$", "");

        navigateTo(baseUrl + PATH);

        if (!waitUntilUrlContains(PATH)) {
            throw new AssertionError(
                    "Không mở được trang Lịch thi: "
                            + getCurrentUrl()
            );
        }
    }

    // =========================================
    // 2. CHỌN VÀ XÁC THỰC TAB
    // =========================================

    public void selectYearSemesterTab() {

        WebElement tab = waitUntilClickable(
                YEAR_SEMESTER_TAB
        );

        if (!"true".equalsIgnoreCase(
                tab.getAttribute("aria-selected"))) {

            tab.click();
        }

        waitDriver().until(d ->
                isYearSemesterTabSelected()
        );

        System.out.println(
                "[TC_28] Tab NĂM HỌC-HỌC KỲ: SELECTED."
        );
    }

    public boolean isYearSemesterTabSelected() {

        try {

            for (WebElement tab :
                    driver().findElements(YEAR_SEMESTER_TAB)) {

                if (tab.isDisplayed()
                        && "true".equalsIgnoreCase(
                        tab.getAttribute("aria-selected")
                )) {

                    return true;
                }
            }

            return false;

        } catch (StaleElementReferenceException ex) {
            return false;
        }
    }

    // =========================================
    // 3. BỘ LỌC NĂM HỌC / HỌC KỲ
    // =========================================

    private By filter(String label) {

        return By.xpath(
                "//label[normalize-space(.)='" + label
                        + "']/following::*[@role='combobox'][1]"
        );
    }

    private WebElement dropdown(String label) {
        return waitUntilVisible(filter(label));
    }

    public String selectedValue(String label) {

        WebElement control = dropdown(label);

        if ("select".equalsIgnoreCase(
                control.getTagName())) {

            return clean(
                    new Select(control)
                            .getFirstSelectedOption()
                            .getText()
            );
        }

        return clean(control.getText());
    }

    public void choose(String label, String expected) {

        if (expected.equals(selectedValue(label))) {
            return;
        }

        WebElement control = dropdown(label);

        if ("select".equalsIgnoreCase(
                control.getTagName())) {

            new Select(control)
                    .selectByVisibleText(expected);

        } else {

            control.click();

            By option = By.xpath(
                    "//*[@role='option' and normalize-space(.)='"
                            + expected + "']"
            );

            waitUntilClickable(option).click();
        }

        waitDriver().until(
                d -> expected.equals(selectedValue(label))
        );
    }

    // =========================================
    // 4. ĐỌC TIÊU ĐỀ BẢNG TẤT CẢ
    // =========================================

    public List<String> headers() {

        WebElement table = waitUntilVisible(ALL_TABLE);

        List<String> result = new ArrayList<>();

        for (WebElement th :
                table.findElements(HEADER_CELLS)) {

            result.add(clean(th.getText()));
        }

        return result;
    }

    // =========================================
    // 5. ĐỌC TOÀN BỘ DỮ LIỆU LỊCH THI
    // =========================================

    public List<ExamRecord> records() {

        awaitStableRows();

        WebElement table = waitUntilVisible(ALL_TABLE);

        List<ExamRecord> result = new ArrayList<>();

        for (WebElement row :
                table.findElements(BODY_ROWS)) {

            List<WebElement> cells =
                    row.findElements(BODY_CELLS);

            if (cells.isEmpty()) {
                continue;
            }

            if (cells.size() != 10) {
                throw new AssertionError(
                        "Bảng Tất cả có dòng "
                                + cells.size()
                                + " cột; cần đúng 10 cột hiển thị."
                );
            }

            String firstCell =
                    clean(cells.get(0).getText());

            Matcher matcher =
                    CLASS_AND_NAME.matcher(firstCell);

            if (!matcher.matches()) {
                throw new AssertionError(
                        "Không tách được mã/tên học phần: "
                                + firstCell
                );
            }

            result.add(new ExamRecord(
                    clean(matcher.group(1)),
                    clean(matcher.group(2)),
                    clean(cells.get(1).getText()),
                    clean(cells.get(2).getText()),
                    clean(cells.get(3).getText()),
                    clean(cells.get(4).getText()),
                    clean(cells.get(5).getText()),
                    clean(cells.get(6).getText()),
                    clean(cells.get(7).getText()),
                    clean(cells.get(8).getText()),
                    clean(cells.get(9).getText())
            ));
        }

        return List.copyOf(result);
    }

    // =========================================
    // 6. ĐỢI BẢNG ỔN ĐỊNH
    // =========================================

    private void awaitStableRows() {

        String[] previousSnapshot = {null};
        int[] stablePolls = {0};

        try {

            waitDriver().until(driver -> {

                try {

                    WebElement table =
                            driver.findElement(ALL_TABLE);

                    if (!table.isDisplayed()) {
                        return false;
                    }

                    int count = 0;

                    for (WebElement row :
                            table.findElements(BODY_ROWS)) {

                        if (!row.findElements(BODY_CELLS)
                                .isEmpty()) {
                            count++;
                        }
                    }

                    String snapshot =
                            count + "|" + table.getText();

                    stablePolls[0] =
                            count > 0
                                    && snapshot.equals(
                                    previousSnapshot[0]
                            )
                                    ? stablePolls[0] + 1
                                    : 0;

                    previousSnapshot[0] = snapshot;

                    return count > 0
                            && stablePolls[0] >= 2;

                } catch (StaleElementReferenceException ex) {

                    stablePolls[0] = 0;
                    return false;
                }
            });

        } catch (org.openqa.selenium.TimeoutException ex) {

            throw new AssertionError(
                    "Không xác nhận được dữ liệu bảng Tất cả đã tải. "
                            + "Cần kiểm tra trạng thái rỗng hoặc lỗi tải, "
                            + "không coi là PASS.",
                    ex
            );
        }
    }

    private String clean(String text) {

        return text == null
                ? ""
                : text.strip().replaceAll("\\s+", " ");
    }
}
