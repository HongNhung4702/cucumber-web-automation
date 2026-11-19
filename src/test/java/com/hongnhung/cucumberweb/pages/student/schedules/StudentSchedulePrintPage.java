
package com.hongnhung.cucumberweb.pages.student.schedules;

import com.hongnhung.cucumberweb.config.ConfigManager;
import com.hongnhung.cucumberweb.driver.DriverManager;
import com.hongnhung.cucumberweb.models.schedules.PrintScheduleRecord;
import com.hongnhung.cucumberweb.pages.BasePage;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public final class StudentSchedulePrintPage extends BasePage {

    private static final By STUDENT_TAB = By.xpath(
            "//button[@role='tab' and "
                    + "normalize-space(.)='Thời khóa biểu sinh viên']"
    );

    private static final By YEAR_SELECT =
            By.id("mui-component-select-YearStudy");

    private static final By TERM_SELECT =
            By.id("mui-component-select-TermID");

    private static final By YEAR_VALUE =
            By.cssSelector("input[name='YearStudy']");

    private static final By TERM_VALUE =
            By.cssSelector("input[name='TermID']");

    private static final By PRINT_BUTTON =
            By.xpath("//button[normalize-space(.)='In TKB']");

    private static final By DIALOG =
            By.cssSelector("div[role='dialog']");

    private static final By DIALOG_TITLE =
            By.cssSelector(
                    "div[role='dialog'] "
                            + "#customized-dialog-title h6"
            );

    private static final By PREVIEW_TITLE =
            By.cssSelector(
                    "div[role='dialog'] h5"
            );

    private static final By PRINT_ACTION =
            By.xpath(
                    "//div[@role='dialog']//button"
                            + "[normalize-space(.)='In thời khóa biểu']"
            );

    private static final By TABLE =
            By.cssSelector(
                    "div[role='dialog'] table"
            );

    private static final By HEADERS =
            By.cssSelector("thead th");

    private static final By ROWS =
            By.cssSelector("tbody tr");

    private static final By CELLS =
            By.cssSelector("td");

    private static final List<String> EXPECTED_HEADERS =
            List.of(
                    "STT",
                    "Mã LHP/ Tên LHP",
                    "Tên GV",
                    "STC",
                    "Mã lớp",
                    "Thứ",
                    "Tiết(giờ bắt đầu)",
                    "Phòng học",
                    "Tuần",
                    "Phòng/địa điểm"
            );

    public StudentSchedulePrintPage(
            DriverManager driverManager
    ) {
        super(driverManager);
    }

    public void open() {

        String baseUrl = ConfigManager.get("base.url")
                .replaceAll("/+$", "");

        driver().navigate().to(
                baseUrl + "/student/schedules"
        );

        waitUntilVisible(STUDENT_TAB);
        click(STUDENT_TAB);

        waitDriver().until(d -> {
            try {
                return "true".equals(
                        d.findElement(STUDENT_TAB)
                                .getAttribute("aria-selected")
                );
            } catch (StaleElementReferenceException ex) {
                return false;
            }
        });

        selectValue(YEAR_SELECT, "2022-2023");
        waitForValue(YEAR_VALUE, "2022-2023");

        selectValue(TERM_SELECT, "Học kỳ 2");
        waitForValue(TERM_VALUE, "HK02");

        // Chờ phần lịch của đúng năm và kỳ xuất hiện.
        waitDriver().until(d -> {
            try {
                return "2022-2023".equals(
                        d.findElement(YEAR_VALUE)
                                .getDomProperty("value")
                ) && "HK02".equals(
                        d.findElement(TERM_VALUE)
                                .getDomProperty("value")
                ) && d.findElement(PRINT_BUTTON).isDisplayed()
                        && d.findElement(
                        By.cssSelector("#ScheduleComponent table")
                ).isDisplayed();
            } catch (StaleElementReferenceException ex) {
                return false;
            }
        });

        click(PRINT_BUTTON);

        waitUntilVisible(DIALOG);
        waitUntilVisible(TABLE);

        waitDriver().until(d -> {
            try {
                WebElement dialog = d.findElement(DIALOG);
                WebElement table = dialog.findElement(
                        By.tagName("table")
                );

                return dialog.isDisplayed()
                        && table.isDisplayed()
                        && table.findElements(ROWS).size() == 16
                        && table.findElements(HEADERS).size() == 10
                        && !dialog.findElements(
                        By.xpath(
                                ".//button"
                                        + "[normalize-space(.)="
                                        + "'In thời khóa biểu']"
                        )
                ).isEmpty();

            } catch (StaleElementReferenceException ex) {
                return false;
            }
        });

        System.out.println(
                "[TC_26] Da mo ban xem truoc thoi khoa bieu."
        );
    }

    private void selectValue(By selector, String text) {

        if (clean(waitUntilVisible(selector).getText())
                .equals(text)) {
            return;
        }

        click(selector);

        By option = By.xpath(
                "//*[@role='option' and "
                        + "normalize-space(.)='" + text + "']"
        );

        click(option);

        waitDriver().until(d -> {
            try {
                return text.equals(
                        clean(d.findElement(selector).getText())
                );
            } catch (StaleElementReferenceException ex) {
                return false;
            }
        });
    }

    private void waitForValue(By input, String expected) {

        waitDriver().until(d -> {
            try {
                return expected.equals(
                        d.findElement(input)
                                .getDomProperty("value")
                );
            } catch (StaleElementReferenceException ex) {
                return false;
            }
        });
    }

    public String selectedYear() {
        return clean(
                waitUntilVisible(YEAR_SELECT).getText()
        );
    }

    public String selectedTerm() {
        return clean(
                waitUntilVisible(TERM_SELECT).getText()
        );
    }

    public boolean studentTabSelected() {
        return "true".equals(
                driver().findElement(STUDENT_TAB)
                        .getAttribute("aria-selected")
        );
    }

    public String dialogTitle() {
        return clean(
                waitUntilVisible(DIALOG_TITLE).getText()
        );
    }

    public String previewTitle() {
        return clean(
                waitUntilVisible(PREVIEW_TITLE).getText()
        );
    }

    public boolean printActionVisible() {
        return waitUntilVisible(PRINT_ACTION).isDisplayed()
                && driver().findElement(PRINT_ACTION).isEnabled();
    }

    public List<String> expectedHeaders() {
        return EXPECTED_HEADERS;
    }

    public List<String> headers() {

        WebElement table = waitUntilVisible(TABLE);
        List<String> result = new ArrayList<>();

        for (WebElement header : table.findElements(HEADERS)) {
            result.add(clean(header.getText()));
        }

        return List.copyOf(result);
    }

    public List<PrintScheduleRecord> records() {

        return waitDriver().until(d -> {
            try {
                WebElement table = d.findElement(TABLE);

                List<WebElement> rows = table.findElements(ROWS);

                if (rows.size() != 16) {
                    return null;
                }

                List<PrintScheduleRecord> result =
                        new ArrayList<>();

                // 5 trường được gộp bằng rowspan.
                List<String> previousGroup = null;
                int remainingRows = 0;

                for (int i = 0; i < rows.size(); i++) {

                    List<WebElement> cells =
                            rows.get(i).findElements(CELLS);

                    List<String> values = new ArrayList<>();

                    if (cells.size() == 10) {

                        if (remainingRows != 0) {
                            throw new IllegalStateException(
                                    "TC_26: Nhom rowspan "
                                            + "truoc chua ket thuc tai dong "
                                            + (i + 1)
                            );
                        }

                        int span = Integer.parseInt(
                                cells.get(0).getAttribute("rowspan")
                                        == null
                                        ? "1"
                                        : cells.get(0)
                                        .getAttribute("rowspan")
                        );

                        if (span < 1) {
                            throw new IllegalStateException(
                                    "TC_26: rowspan khong hop le."
                            );
                        }

                        for (int j = 0; j < 5; j++) {

                            String rowSpan =
                                    cells.get(j)
                                            .getAttribute("rowspan");

                            int currentSpan =
                                    rowSpan == null
                                            ? 1
                                            : Integer.parseInt(rowSpan);

                            if (currentSpan != span) {
                                throw new IllegalStateException(
                                        "TC_26: rowspan khong "
                                                + "dong nhat tai dong " + (i + 1)
                                );
                            }

                            values.add(
                                    clean(cells.get(j).getText())
                            );
                        }

                        previousGroup = List.copyOf(values);
                        remainingRows = span - 1;

                        for (int j = 5; j < 10; j++) {
                            values.add(
                                    clean(cells.get(j).getText())
                            );
                        }

                    } else if (cells.size() == 5) {

                        if (previousGroup == null
                                || remainingRows <= 0) {
                            throw new IllegalStateException(
                                    "TC_26: Dong rowspan "
                                            + "khong co nhom truoc."
                            );
                        }

                        values.addAll(previousGroup);

                        for (WebElement cell : cells) {
                            values.add(clean(cell.getText()));
                        }

                        remainingRows--;

                    } else {
                        throw new IllegalStateException(
                                "TC_26: Dong " + (i + 1)
                                        + " co " + cells.size()
                                        + " o, khong hop le."
                        );
                    }

                    result.add(new PrintScheduleRecord(
                            values.get(0),
                            values.get(1),
                            values.get(2),
                            values.get(3),
                            values.get(4),
                            values.get(5),
                            values.get(6),
                            values.get(7),
                            values.get(8),
                            values.get(9)
                    ));
                }

                if (remainingRows != 0) {
                    throw new IllegalStateException(
                            "TC_26: Nhom rowspan cuoi "
                                    + "khong ket thuc dung."
                    );
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
