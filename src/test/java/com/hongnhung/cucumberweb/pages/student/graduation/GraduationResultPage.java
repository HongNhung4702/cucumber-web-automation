
package com.hongnhung.cucumberweb.pages.student.graduation;

import com.hongnhung.cucumberweb.config.ConfigManager;
import com.hongnhung.cucumberweb.driver.DriverManager;
import com.hongnhung.cucumberweb.models.graduation.GraduationResultRecord;
import com.hongnhung.cucumberweb.pages.BasePage;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public final class GraduationResultPage extends BasePage {

    private static final String EXPECTED_PROGRAM_CODE =
            "119DHCQK45";

    private static final By PROGRAM =
            By.id("study-program-select");

    private static final By PROGRAM_CODE =
            By.cssSelector("input[name='StudyProgramID']");

    private static final By OPEN_BUTTON =
            By.xpath(
                    "//button[normalize-space(.)="
                            + "'Kết quả xét tốt nghiệp']"
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
                    "Tên đợt",
                    "Đăng ký CTĐT",
                    "Ngày xét",
                    "Kết quả",
                    "ĐTB",
                    "Xếp loại",
                    "Ghi chú"
            );

    public GraduationResultPage(DriverManager driverManager) {
        super(driverManager);
    }

    // 1. Mo trang va hop thoai
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
                    "TC_19: Chuong trinh dao tao khong khop. "
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

                return table.isDisplayed()
                        && table.findElements(ROWS).size() == 2;

            } catch (StaleElementReferenceException ex) {
                return false;
            }
        });

        System.out.println(
                "[TC_19] Da mo Ket qua xet tot nghiep."
        );
    }

    // 2. Thong tin chuong trinh
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

    // 3. Tieu de cot
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

    // 4. Doc hai dong du lieu
    public List<GraduationResultRecord> records() {

        return waitDriver().until(d -> {

            try {
                WebElement table = d.findElement(TABLE);

                if (!table.isDisplayed()) {
                    return null;
                }

                List<WebElement> rows =
                        table.findElements(ROWS);

                if (rows.size() != 2) {
                    return null;
                }

                List<GraduationResultRecord> result =
                        new ArrayList<>();

                for (WebElement row : rows) {

                    List<WebElement> cells =
                            row.findElements(CELLS);

                    if (cells.size() != 7) {
                        throw new IllegalStateException(
                                "TC_19: So cot khong hop le. "
                                        + "Actual=" + cells.size()
                        );
                    }

                    result.add(new GraduationResultRecord(
                            clean(cells.get(0).getText()),
                            clean(cells.get(1).getText()),
                            clean(cells.get(2).getText()),
                            clean(cells.get(3).getText()),
                            clean(cells.get(4).getText()),
                            clean(cells.get(5).getText()),
                            clean(cells.get(6).getText())
                    ));
                }

                return List.copyOf(result);

            } catch (StaleElementReferenceException ex) {
                return null;
            }
        });
    }

    // 5. Chuan hoa khoang trang
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
