
package com.hongnhung.cucumberweb.pages.student.graduation;

import com.hongnhung.cucumberweb.config.ConfigManager;
import com.hongnhung.cucumberweb.driver.DriverManager;
import com.hongnhung.cucumberweb.models.graduation.GraduationConditionRecord;
import com.hongnhung.cucumberweb.pages.BasePage;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public final class GraduationConditionPage extends BasePage {

    private static final String EXPECTED_PROGRAM_CODE =
            "119DHCQK45";

    private static final By PROGRAM =
            By.id("study-program-select");

    private static final By PROGRAM_CODE =
            By.cssSelector("input[name='StudyProgramID']");

    private static final By OPEN_BUTTON =
            By.xpath(
                    "//button[normalize-space(.)='Điều kiện tốt nghiệp']"
            );

    private static final By DIALOG =
            By.cssSelector("div[role='dialog']");

    private static final By DIALOG_TITLE =
            By.cssSelector("#customized-dialog-title h6");

    private static final By TABLE =
            By.cssSelector(
                    "div[role='dialog'] table[aria-label='a dense table']"
            );

    private static final By HEADERS =
            By.cssSelector("thead th");

    private static final By ROWS =
            By.cssSelector("tbody tr");

    private static final By CELLS =
            By.cssSelector("td");

    private static final By CHECK_ICON =
            By.cssSelector("svg[style*='green']");

    private static final List<String> EXPECTED_HEADERS =
            List.of(
                    "Loại chuẩn đầu ra",
                    "Điều kiện đầu ra của CTĐT",
                    "Ghi chú",
                    "Chứng chỉ sinh viên",
                    "Kết quả"
            );

    public GraduationConditionPage(DriverManager driverManager) {
        super(driverManager);
    }

    public void open() {

        String baseUrl =
                ConfigManager.get("base.url")
                        .replaceAll("/+$", "");

        driver().navigate().to(baseUrl + "/student/overview");

        waitUntilVisible(PROGRAM);

        waitDriver().until(d -> {
            String value = d.findElement(PROGRAM_CODE)
                    .getDomProperty("value");

            return value != null && !value.isBlank();
        });

        String code = selectedProgramCode();

        if (!EXPECTED_PROGRAM_CODE.equals(code)) {
            throw new IllegalStateException(
                    "TC_18: Sai chuong trinh dao tao. "
                            + "Expected=" + EXPECTED_PROGRAM_CODE
                            + ", Actual=" + code
            );
        }

        click(OPEN_BUTTON);

        waitUntilVisible(DIALOG);
        waitUntilVisible(TABLE);

        waitDriver().until(d -> {
            try {
                WebElement table = d.findElement(TABLE);
                return table.isDisplayed()
                        && table.findElements(ROWS).size() == 7;
            } catch (StaleElementReferenceException ex) {
                return false;
            }
        });

        System.out.println(
                "[TC_18] Da mo hop thoai Dieu kien tot nghiep."
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

    public List<GraduationConditionRecord> records() {

        return waitDriver().until(d -> {
            try {
                WebElement table = d.findElement(TABLE);

                if (!table.isDisplayed()) {
                    return null;
                }

                List<WebElement> rows = table.findElements(ROWS);

                if (rows.size() != 7) {
                    return null;
                }

                List<GraduationConditionRecord> result =
                        new ArrayList<>();

                for (WebElement row : rows) {

                    List<WebElement> cells =
                            row.findElements(CELLS);

                    if (cells.size() != 5) {
                        throw new IllegalStateException(
                                "TC_18: Sai so cot. Actual="
                                        + cells.size()
                        );
                    }

                    String status = readStatus(cells.get(4));

                    result.add(new GraduationConditionRecord(
                            clean(cells.get(0).getText()),
                            clean(cells.get(1).getText()),
                            clean(cells.get(2).getText()),
                            clean(cells.get(3).getText()),
                            status
                    ));
                }

                return List.copyOf(result);

            } catch (StaleElementReferenceException ex) {
                return null;
            }
        });
    }

    private String readStatus(WebElement cell) {

        String text = clean(cell.getText());

        List<WebElement> icons = cell.findElements(CHECK_ICON);

        if (icons.isEmpty()) {
            // Phan biet o trong voi bieu tuong khong xac dinh.
            if (!cell.findElements(By.cssSelector("svg")).isEmpty()) {
                return "UNKNOWN_ICON";
            }
            return text;
        }

        if (icons.size() != 1) {
            return "MULTIPLE_ICONS";
        }

        WebElement svg = icons.get(0);

        if (!svg.isDisplayed()) {
            return "HIDDEN_ICON";
        }

        String color = svg.getCssValue("color");

        if ("rgb(0, 128, 0)".equals(color)
                || "rgba(0, 128, 0, 1)".equals(color)) {
            return "CHECK_GREEN";
        }

        return "OTHER_ICON_COLOR:" + color;
    }

    private String clean(String value) {

        if (value == null) {
            return "";
        }

        return value.replace('\u00A0', ' ')
                .strip()
                .replaceAll("\\s+", " ");
    }
}
