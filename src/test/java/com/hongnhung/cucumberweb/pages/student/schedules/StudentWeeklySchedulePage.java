
package com.hongnhung.cucumberweb.pages.student.schedules;

import com.hongnhung.cucumberweb.config.ConfigManager;
import com.hongnhung.cucumberweb.driver.DriverManager;
import com.hongnhung.cucumberweb.models.schedules.WeeklyScheduleRecord;
import com.hongnhung.cucumberweb.pages.BasePage;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StudentWeeklySchedulePage extends BasePage {

    public static final String YEAR = "2024-2025";
    public static final String TERM = "Học kỳ 1";
    public static final String WEEK =
            "18/11/2024-24/11/2024";

    private static final By STUDENT_TAB =
            By.xpath(
                    "//button[@role='tab' and "
                            + "normalize-space(.)='Thời khóa biểu sinh viên']"
            );

    private static final By YEAR_SELECT =
            By.id("mui-component-select-YearStudy");

    private static final By TERM_SELECT =
            By.id("mui-component-select-TermID");

    private static final By WEEK_SELECT =
            By.id("demo-simple-select-helper");

    private static final By YEAR_VALUE =
            By.cssSelector("input[name='YearStudy']");

    private static final By TERM_VALUE =
            By.cssSelector("input[name='TermID']");

    private static final By WEEK_VALUE =
            By.cssSelector("#ScheduleComponent input[name='Tuan']");

    private static final By TABLE =
            By.cssSelector("#ScheduleComponent table");

    private static final By HEADERS =
            By.cssSelector("thead th");

    private static final By ROWS =
            By.cssSelector("tbody tr");

    private static final By CELLS =
            By.cssSelector("td");

    // Nhận diện đủ 5 trường của từng lượt học.
    private static final Pattern LESSON_PATTERN =
            Pattern.compile(
                    "-Môn:\\s*(.*?)\\s*"
                            + "-Nhóm:\\s*(.*?)\\s*"
                            + "-Lớp:\\s*(.*?)\\s*"
                            + "-Tiết:\\s*(.*?)\\s*"
                            + "-GV:\\s*(.*?)"
                            + "(?=\\s*-Môn:|$)",
                    Pattern.DOTALL
            );

    private static final List<String> DAYS =
            List.of(
                    "Thứ 2",
                    "Thứ 3",
                    "Thứ 4",
                    "Thứ 5",
                    "Thứ 6",
                    "Thứ 7",
                    "Chủ nhật"
            );

    private static final List<String> SESSIONS =
            List.of("Sáng", "Chiều", "Tối");

    public record Snapshot(
            List<WeeklyScheduleRecord> lessons,
            List<String> emptyCells,
            int occupiedCells
    ) {
    }

    public StudentWeeklySchedulePage(
            DriverManager driverManager
    ) {
        super(driverManager);
    }

    public void open() {

        String baseUrl =
                ConfigManager.get("base.url")
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

        selectValue(YEAR_SELECT, YEAR);
        waitForInputValue(YEAR_VALUE, YEAR);

        selectValue(TERM_SELECT, TERM);
        waitForInputValue(TERM_VALUE, "HK01");

        selectValue(WEEK_SELECT, WEEK);
        waitForInputValue(WEEK_VALUE, "47");

        waitDriver().until(d -> {
            try {
                WebElement table = d.findElement(TABLE);

                if (!table.isDisplayed()) {
                    return false;
                }

                List<WebElement> rows =
                        table.findElements(ROWS);

                if (rows.size() != 3) {
                    return false;
                }

                List<WebElement> headers =
                        table.findElements(HEADERS);

                if (headers.size() != 8) {
                    return false;
                }

                // Xác nhận bảng thực sự thuộc tuần đã chọn.
                return normalize(headers.get(1).getText())
                        .contains("18/11/2024")
                        && normalize(headers.get(7).getText())
                        .contains("24/11/2024");

            } catch (StaleElementReferenceException ex) {
                return false;
            }
        });

        System.out.println(
                "[TC_22] Da mo lich tuan " + WEEK
        );
    }

    private void selectValue(
            By selector,
            String expectedText
    ) {

        String current =
                normalize(waitUntilVisible(selector).getText());

        if (normalize(expectedText).equals(current)) {
            return;
        }

        click(selector);

        By option = By.xpath(
                "//*[@role='option' and "
                        + "normalize-space(.)="
                        + xpathLiteral(expectedText) + "]"
        );

        waitUntilVisible(option);
        click(option);

        waitDriver().until(d -> {
            try {
                return normalize(
                        d.findElement(selector).getText()
                ).equals(normalize(expectedText));

            } catch (StaleElementReferenceException ex) {
                return false;
            }
        });
    }

    private void waitForInputValue(
            By input,
            String expected
    ) {

        waitDriver().until(d -> {
            try {
                String actual =
                        d.findElement(input)
                                .getDomProperty("value");

                return expected.equals(actual);

            } catch (StaleElementReferenceException ex) {
                return false;
            }
        });
    }

    private String xpathLiteral(String text) {

        if (!text.contains("'")) {
            return "'" + text + "'";
        }

        if (!text.contains("\"")) {
            return "\"" + text + "\"";
        }

        throw new IllegalArgumentException(
                "Gia tri XPath khong duoc ho tro: " + text
        );
    }

    public String selectedYear() {
        return normalize(
                waitUntilVisible(YEAR_SELECT).getText()
        );
    }

    public String selectedTerm() {
        return normalize(
                waitUntilVisible(TERM_SELECT).getText()
        );
    }

    public String selectedWeek() {
        return normalize(
                waitUntilVisible(WEEK_SELECT).getText()
        );
    }

    public String selectedWeekCode() {
        return driver()
                .findElement(WEEK_VALUE)
                .getDomProperty("value");
    }

    public boolean studentTabSelected() {
        return "true".equals(
                driver().findElement(STUDENT_TAB)
                        .getAttribute("aria-selected")
        );
    }

    public List<String> headers() {

        WebElement table = waitUntilVisible(TABLE);
        List<String> result = new ArrayList<>();

        for (WebElement header :
                table.findElements(HEADERS)) {

            result.add(normalize(header.getText()));
        }

        return List.copyOf(result);
    }

    public List<String> expectedHeaders() {

        List<String> result = new ArrayList<>();
        result.add("");

        LocalDate start =
                LocalDate.of(2024, 11, 18);

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (int i = 0; i < DAYS.size(); i++) {

            result.add(
                    DAYS.get(i) + " "
                            + start.plusDays(i).format(formatter)
            );
        }

        return List.copyOf(result);
    }

    public List<String> sessions() {

        WebElement table = waitUntilVisible(TABLE);
        List<String> result = new ArrayList<>();

        for (WebElement row : table.findElements(ROWS)) {

            List<WebElement> cells = row.findElements(CELLS);

            if (cells.size() != 8) {
                throw new IllegalStateException(
                        "TC_22: Moi dong phai co 8 o. "
                                + "Actual=" + cells.size()
                );
            }

            result.add(normalize(cells.get(0).getText()));
        }

        return List.copyOf(result);
    }

    public Snapshot snapshot() {

        return waitDriver().until(d -> {

            try {
                WebElement table = d.findElement(TABLE);

                if (!table.isDisplayed()) {
                    return null;
                }

                List<WebElement> rows =
                        table.findElements(ROWS);

                if (rows.size() != 3) {
                    return null;
                }

                List<WeeklyScheduleRecord> lessons =
                        new ArrayList<>();

                List<String> emptyCells =
                        new ArrayList<>();

                int occupiedCells = 0;

                for (int rowIndex = 0;
                     rowIndex < rows.size();
                     rowIndex++) {

                    List<WebElement> cells =
                            rows.get(rowIndex).findElements(CELLS);

                    if (cells.size() != 8) {
                        throw new IllegalStateException(
                                "TC_22: So o khong hop le tai buoi "
                                        + SESSIONS.get(rowIndex)
                        );
                    }

                    String session = SESSIONS.get(rowIndex);

                    if (!session.equals(
                            normalize(cells.get(0).getText()))) {
                        throw new IllegalStateException(
                                "TC_22: Sai thu tu buoi hoc."
                        );
                    }

                    for (int dayIndex = 0;
                         dayIndex < 7;
                         dayIndex++) {

                        WebElement cell =
                                cells.get(dayIndex + 1);

                        String day = DAYS.get(dayIndex);

                        String cellId =
                                day + " / " + session;

                        String content =
                                normalize(cell.getText());

                        List<WebElement> cards =
                                cell.findElements(
                                        By.cssSelector(".MuiCard-root")
                                );

                        if (content.isBlank()) {

                            if (!cards.isEmpty()) {
                                throw new IllegalStateException(
                                        "TC_22: O lich co card "
                                                + "nhung khong co noi dung: "
                                                + cellId
                                );
                            }

                            emptyCells.add(cellId);
                            continue;
                        }

                        if (cards.size() != 1) {
                            throw new IllegalStateException(
                                    "TC_22: O co lich khong co "
                                            + "dung 1 card: " + cellId
                            );
                        }

                        occupiedCells++;

                        List<WeeklyScheduleRecord> parsed =
                                parseLessons(
                                        day,
                                        session,
                                        normalize(cards.get(0).getText())
                                );

                        if (parsed.isEmpty()) {
                            throw new IllegalStateException(
                                    "TC_22: Khong doc duoc mon hoc "
                                            + "tai " + cellId
                            );
                        }

                        lessons.addAll(parsed);
                    }
                }

                return new Snapshot(
                        List.copyOf(lessons),
                        List.copyOf(emptyCells),
                        occupiedCells
                );

            } catch (StaleElementReferenceException ex) {
                return null;
            }
        });
    }

    private List<WeeklyScheduleRecord> parseLessons(
            String day,
            String session,
            String content
    ) {

        List<WeeklyScheduleRecord> result =
                new ArrayList<>();

        Matcher matcher =
                LESSON_PATTERN.matcher(content);

        int cursor = 0;

        while (matcher.find()) {

            // Khong duoc bo qua van ban khong nhan dien duoc.
            if (!content.substring(
                    cursor, matcher.start()).isBlank()) {
                throw new IllegalStateException(
                        "TC_22: Co noi dung lich khong phan tich "
                                + "duoc: "
                                + content.substring(cursor, matcher.start())
                );
            }

            result.add(new WeeklyScheduleRecord(
                    day,
                    session,
                    normalize(matcher.group(1)),
                    normalize(matcher.group(2)),
                    normalize(matcher.group(3)),
                    normalize(matcher.group(4)),
                    normalize(matcher.group(5))
            ));

            cursor = matcher.end();
        }

        if (!content.substring(cursor).isBlank()) {
            throw new IllegalStateException(
                    "TC_22: Con noi dung chua doc duoc: "
                            + content.substring(cursor)
            );
        }

        return result;
    }

    private String normalize(String text) {

        if (text == null) {
            return "";
        }

        return java.text.Normalizer.normalize(
                text.replace('\u00A0', ' ')
                        .replace('\u202F', ' ')
                        .strip()
                        .replaceAll("\\s+", " "),
                java.text.Normalizer.Form.NFC
        );
    }
}
