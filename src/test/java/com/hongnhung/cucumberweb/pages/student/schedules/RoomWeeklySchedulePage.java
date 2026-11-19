
package com.hongnhung.cucumberweb.pages.student.schedules;

import com.hongnhung.cucumberweb.config.ConfigManager;
import com.hongnhung.cucumberweb.driver.DriverManager;
import com.hongnhung.cucumberweb.models.schedules.RoomScheduleRecord;
import com.hongnhung.cucumberweb.pages.BasePage;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RoomWeeklySchedulePage extends BasePage {

    public static final String YEAR = "2024-2025";
    public static final String TERM = "Học kỳ 1";
    public static final String WEEK =
            "09/12/2024-15/12/2024";

    private static final By ROOM_TAB =
            By.xpath(
                    "//button[@role='tab' and "
                            + "normalize-space(.)='Thời khóa biểu theo phòng']"
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

    private static final By LESSON_CARD =
            By.cssSelector("div.DetailSchedule");

    private static final List<String> ROOMS =
            List.of(
                    "4T.11.2",
                    "4T.11.3",
                    "A1.106",
                    "A1.407",
                    "A1.410",
                    "A2.305",
                    "A3.207"
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

    public record Snapshot(
            List<RoomScheduleRecord> lessons,
            List<String> emptyCells,
            int occupiedCells
    ) {
    }

    public RoomWeeklySchedulePage(
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

        waitUntilVisible(ROOM_TAB);
        click(ROOM_TAB);

        waitDriver().until(d -> {
            try {
                return "true".equals(
                        d.findElement(ROOM_TAB)
                                .getAttribute("aria-selected")
                ) && !d.findElements(YEAR_SELECT).isEmpty()
                        && !d.findElements(TERM_SELECT).isEmpty();
            } catch (StaleElementReferenceException ex) {
                return false;
            }
        });

        selectValue(YEAR_SELECT, YEAR);
        waitForInputValue(YEAR_VALUE, YEAR);

        selectValue(TERM_SELECT, TERM);
        waitForInputValue(TERM_VALUE, "HK01");

        selectValue(WEEK_SELECT, WEEK);
        waitForInputValue(WEEK_VALUE, "50");

        waitDriver().until(d -> {
            try {
                WebElement table = d.findElement(TABLE);

                if (!table.isDisplayed()) {
                    return false;
                }

                List<WebElement> headers =
                        table.findElements(HEADERS);

                List<WebElement> rows =
                        table.findElements(ROWS);

                if (headers.size() != 8
                        || rows.size() != 7) {
                    return false;
                }

                return normalize(headers.get(1).getText())
                        .contains("09/12/2024")
                        && normalize(headers.get(7).getText())
                        .contains("15/12/2024");

            } catch (StaleElementReferenceException ex) {
                return false;
            }
        });

        System.out.println(
                "[TC_23] Da mo lich phong tuan " + WEEK
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
                "//*[@role='option' and normalize-space(.)="
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
                return expected.equals(
                        d.findElement(input)
                                .getDomProperty("value")
                );
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
                "Khong ho tro chuoi XPath: " + text
        );
    }

    public boolean roomTabSelected() {
        return "true".equals(
                driver().findElement(ROOM_TAB)
                        .getAttribute("aria-selected")
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
        return driver().findElement(WEEK_VALUE)
                .getDomProperty("value");
    }

    public List<String> expectedHeaders() {

        List<String> result = new ArrayList<>();
        result.add("Phòng");

        LocalDate monday =
                LocalDate.of(2024, 12, 9);

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (int i = 0; i < DAYS.size(); i++) {
            result.add(
                    DAYS.get(i) + " "
                            + monday.plusDays(i).format(formatter)
            );
        }

        return List.copyOf(result);
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

    public List<String> expectedRooms() {
        return ROOMS;
    }

    public List<String> rooms() {

        WebElement table = waitUntilVisible(TABLE);
        List<String> result = new ArrayList<>();

        for (WebElement row : table.findElements(ROWS)) {

            List<WebElement> cells =
                    row.findElements(CELLS);

            if (cells.size() != 8) {
                throw new IllegalStateException(
                        "TC_23: Mot dong phong khong co 8 o."
                );
            }

            result.add(
                    normalize(cells.get(0).getText())
            );
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

                if (rows.size() != 7) {
                    return null;
                }

                List<RoomScheduleRecord> lessons =
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
                                "TC_23: Sai so cot tai phong "
                                        + (rowIndex + 1)
                        );
                    }

                    String room =
                            normalize(cells.get(0).getText());

                    if (!ROOMS.get(rowIndex).equals(room)) {
                        throw new IllegalStateException(
                                "TC_23: Sai ten/thu tu phong. "
                                        + "Expected=" + ROOMS.get(rowIndex)
                                        + ", Actual=" + room
                        );
                    }

                    for (int dayIndex = 0;
                         dayIndex < 7;
                         dayIndex++) {

                        WebElement cell =
                                cells.get(dayIndex + 1);

                        String day = DAYS.get(dayIndex);

                        String cellId =
                                room + " / " + day;

                        String content =
                                normalize(cell.getText());

                        List<WebElement> cards =
                                cell.findElements(LESSON_CARD);

                        if (content.isBlank()) {

                            if (!cards.isEmpty()) {
                                throw new IllegalStateException(
                                        "TC_23: O trong co card: "
                                                + cellId
                                );
                            }

                            emptyCells.add(cellId);
                            continue;
                        }

                        if (cards.size() != 1) {
                            throw new IllegalStateException(
                                    "TC_23: O co lich khong co "
                                            + "dung 1 card: " + cellId
                            );
                        }

                        occupiedCells++;

                        List<RoomScheduleRecord> parsed =
                                parseLessons(
                                        room,
                                        day,
                                        normalize(cards.get(0).getText())
                                );

                        if (parsed.isEmpty()) {
                            throw new IllegalStateException(
                                    "TC_23: Khong doc duoc lich tai "
                                            + cellId
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

    private List<RoomScheduleRecord> parseLessons(
            String room,
            String day,
            String content
    ) {

        List<RoomScheduleRecord> result =
                new ArrayList<>();

        Matcher matcher =
                LESSON_PATTERN.matcher(content);

        int cursor = 0;

        while (matcher.find()) {

            if (!content.substring(
                    cursor, matcher.start()).isBlank()) {

                throw new IllegalStateException(
                        "TC_23: Van ban chua nhan dien: "
                                + content.substring(
                                cursor, matcher.start()
                        )
                );
            }

            result.add(new RoomScheduleRecord(
                    room,
                    day,
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
                    "TC_23: Noi dung chua doc duoc: "
                            + content.substring(cursor)
            );
        }

        return result;
    }

    private String normalize(String text) {

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
