
package com.hongnhung.cucumberweb.pages.student.progress;

import com.hongnhung.cucumberweb.config.ConfigManager;
import com.hongnhung.cucumberweb.driver.DriverManager;
import com.hongnhung.cucumberweb.models.progress.StudyProgressRecord;
import com.hongnhung.cucumberweb.pages.BasePage;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public final class StudyProgressPage extends BasePage {

    private static final String EXPECTED_PROGRAM_CODE =
            "119DHCQK45";

    private static final By PROGRAM =
            By.id("study-program-select");

    private static final By PROGRAM_CODE =
            By.cssSelector("input[name='StudyProgramID']");

    private static final By OPEN_BUTTON =
            By.xpath(
                    "//button[normalize-space(.)='Kiểm tra tiến độ']"
            );

    private static final By DIALOG =
            By.cssSelector("div[role='dialog']");

    private static final By DIALOG_TITLE =
            By.cssSelector(
                    "div[role='dialog'] "
                            + "#customized-dialog-title h6"
            );

    private static final By TABLE =
            By.cssSelector(
                    "div[role='dialog'] "
                            + "table[aria-label='a dense table']"
            );

    private static final By HEADERS =
            By.cssSelector("thead th");

    private static final By ROWS =
            By.cssSelector("tbody tr");

    private static final By CELLS =
            By.cssSelector("td");

    private static final List<String> EXPECTED_HEADERS =
            List.of(
                    "HỌC KỲ THEO KẾ HOẠCH",
                    "STC CTĐT YÊU CẦU TÍCH LŨY "
                            + "THEO KẾ HOẠCH ĐÀO TẠO CHUẨN",
                    "STC SV TÍCH LŨY",
                    "STC NỢ ĐỌNG"
            );

    public StudyProgressPage(DriverManager driverManager) {
        super(driverManager);
    }

    public void open() {

        String baseUrl =
                ConfigManager.get("base.url")
                        .replaceAll("/+$", "");

        driver().navigate().to(
                baseUrl + "/student/overview"
        );

        waitUntilVisible(PROGRAM);

        waitDriver().until(d -> {
            try {
                String code = d.findElement(PROGRAM_CODE)
                        .getDomProperty("value");

                return code != null && !code.isBlank();

            } catch (StaleElementReferenceException ex) {
                return false;
            }
        });

        if (!EXPECTED_PROGRAM_CODE.equals(selectedProgramCode())) {
            throw new IllegalStateException(
                    "TC_20: Sai chuong trinh dao tao. "
                            + "Expected=" + EXPECTED_PROGRAM_CODE
                            + ", Actual=" + selectedProgramCode()
            );
        }

        click(OPEN_BUTTON);

        waitUntilVisible(DIALOG);
        waitUntilVisible(TABLE);

        waitDriver().until(d -> {
            try {
                WebElement table = d.findElement(TABLE);

                if (!table.isDisplayed()) {
                    return false;
                }

                List<WebElement> rows =
                        table.findElements(ROWS);

                if (rows.size() != 11) {
                    return false;
                }

                List<WebElement> totalCells =
                        rows.get(9).findElements(CELLS);

                List<WebElement> noteCells =
                        rows.get(10).findElements(CELLS);

                return totalCells.size() == 4
                        && noteCells.size() == 2;

            } catch (StaleElementReferenceException ex) {
                return false;
            }
        });

        System.out.println(
                "[TC_20] Da mo hop thoai Kiem tra tien do hoc tap."
        );
    }

    public String selectedProgram() {
        return clean(waitUntilVisible(PROGRAM).getText());
    }

    public String selectedProgramCode() {
        return driver().findElement(PROGRAM_CODE)
                .getDomProperty("value");
    }

    public String dialogTitle() {
        return clean(waitUntilVisible(DIALOG_TITLE).getText());
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

    public List<StudyProgressRecord> records() {

        return waitDriver().until(d -> {
            try {
                WebElement table = d.findElement(TABLE);

                if (!table.isDisplayed()) {
                    return null;
                }

                List<WebElement> rows =
                        table.findElements(ROWS);

                if (rows.size() != 11) {
                    return null;
                }

                List<StudyProgressRecord> result =
                        new ArrayList<>();

                for (int i = 0; i < rows.size(); i++) {

                    WebElement row = rows.get(i);

                    List<WebElement> cells =
                            row.findElements(CELLS);

                    // 9 dong hoc ky
                    if (i < 9) {

                        if (cells.size() != 4) {
                            throw new IllegalStateException(
                                    "TC_20: Dong hoc ky "
                                            + (i + 1)
                                            + " khong co 4 o."
                            );
                        }

                        result.add(new StudyProgressRecord(
                                "SEMESTER",
                                clean(cells.get(0).getText()),
                                clean(cells.get(1).getText()),
                                clean(cells.get(2).getText()),
                                clean(cells.get(3).getText()),
                                ""
                        ));

                        // Dong tong cong
                    } else if (i == 9) {

                        if (cells.size() != 4) {
                            throw new IllegalStateException(
                                    "TC_20: Dong Tong cong "
                                            + "khong co 4 o."
                            );
                        }

                        result.add(new StudyProgressRecord(
                                "TOTAL",
                                clean(cells.get(0).getText()),
                                clean(cells.get(1).getText()),
                                clean(cells.get(2).getText()),
                                clean(cells.get(3).getText()),
                                ""
                        ));

                        // Dong ghi chu co colspan=3
                    } else {

                        if (cells.size() != 2) {
                            throw new IllegalStateException(
                                    "TC_20: Dong Ghi chu "
                                            + "khong co 2 o."
                            );
                        }

                        String colspan =
                                cells.get(1).getAttribute("colspan");

                        if (!"3".equals(colspan)) {
                            throw new IllegalStateException(
                                    "TC_20: Sai colspan "
                                            + "dong Ghi chu. Actual="
                                            + colspan
                            );
                        }

                        result.add(new StudyProgressRecord(
                                "NOTE",
                                clean(cells.get(0).getText()),
                                "",
                                "",
                                "",
                                clean(cells.get(1).getText())
                        ));
                    }
                }

                return List.copyOf(result);

            } catch (StaleElementReferenceException ex) {
                return null;
            }
        });
    }

    private String clean(String value) {

        if (value == null) {
            return "";
        }

        return value.replace('\u00A0', ' ')
                .replace('\u202F', ' ')
                .strip()
                .replaceAll("\\s+", " ");
    }
}
