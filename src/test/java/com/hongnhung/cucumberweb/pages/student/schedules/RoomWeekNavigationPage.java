
package com.hongnhung.cucumberweb.pages.student.schedules;

import com.hongnhung.cucumberweb.driver.DriverManager;
import com.hongnhung.cucumberweb.models.schedules.RoomScheduleRecord;
import com.hongnhung.cucumberweb.pages.BasePage;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RoomWeekNavigationPage extends BasePage {

    public static final String WEEK_A =
            "09/12/2024-15/12/2024";

    public static final String WEEK_B =
            "16/12/2024-22/12/2024";

    private static final By WEEK_SELECT =
            By.id("demo-simple-select-helper");

    private static final By WEEK_INPUT =
            By.cssSelector(
                    "#ScheduleComponent input[name='Tuan']"
            );

    private static final By TABLE =
            By.cssSelector("#ScheduleComponent table");

    private static final By HEADERS =
            By.cssSelector(
                    "#ScheduleComponent table thead th"
            );

    private static final By ROWS =
            By.cssSelector("tbody tr");

    private static final By CELLS =
            By.cssSelector("td");

    private static final By CARD =
            By.cssSelector("div.DetailSchedule");

    private static final By NEXT_BUTTON =
            By.xpath(
                    "//*[@id='ScheduleComponent']//button"
                            + "[.//*[local-name()='path' and "
                            + "starts-with(@d,'m6 18 8.5-6')]]"
            );

    private static final By PREVIOUS_BUTTON =
            By.xpath(
                    "//*[@id='ScheduleComponent']//button"
                            + "[.//*[local-name()='path' and "
                            + "starts-with(@d,'M6 6h2v12H6')]]"
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

    private static final List<String> DAYS =
            List.of(
                    "Thứ 2", "Thứ 3", "Thứ 4",
                    "Thứ 5", "Thứ 6", "Thứ 7",
                    "Chủ nhật"
            );

    private static final List<String> ROOMS_B =
            List.of(
                    "4T.11.3",
                    "A1.407",
                    "A1.410",
                    "A2.305"
            );

    public record Snapshot(
            List<String> rooms,
            List<RoomScheduleRecord> lessons,
            List<String> emptyCells,
            int occupiedCells
    ) {
    }

    public RoomWeekNavigationPage(
            DriverManager driverManager
    ) {
        super(driverManager);
    }

    public void nextWeek() {

        ensureWeek(WEEK_A, "50");

        click(NEXT_BUTTON);

        waitForWeek(
                WEEK_B,
                "51",
                "16/12/2024",
                "22/12/2024",
                4,
                5
        );
    }

    public void previousWeek() {

        ensureWeek(WEEK_B, "51");

        click(PREVIOUS_BUTTON);

        waitForWeek(
                WEEK_A,
                "50",
                "09/12/2024",
                "15/12/2024",
                7,
                8
        );
    }

    public String selectedWeek() {
        return clean(
                waitUntilVisible(WEEK_SELECT).getText()
        );
    }

    public String selectedWeekCode() {
        return driver().findElement(WEEK_INPUT)
                .getDomProperty("value");
    }

    public List<String> headers() {

        WebElement table = waitUntilVisible(TABLE);
        List<String> result = new ArrayList<>();

        for (WebElement header : table.findElements(HEADERS)) {
            result.add(clean(header.getText()));
        }

        return List.copyOf(result);
    }

    public Snapshot snapshotNextWeek() {

        return waitDriver().until(d -> {

            try {
                WebElement table = d.findElement(TABLE);

                if (!table.isDisplayed()) {
                    return null;
                }

                List<WebElement> rows =
                        table.findElements(ROWS);

                if (rows.size() != 4) {
                    return null;
                }

                List<String> rooms = new ArrayList<>();
                List<RoomScheduleRecord> lessons =
                        new ArrayList<>();
                List<String> emptyCells =
                        new ArrayList<>();

                int occupiedCells = 0;

                for (WebElement row : rows) {

                    List<WebElement> cells =
                            row.findElements(CELLS);

                    if (cells.size() != 8) {
                        throw new IllegalStateException(
                                "TC_25: Dong phong khong co 8 o."
                        );
                    }

                    String room =
                            clean(cells.get(0).getText());

                    if (room.isEmpty()) {
                        throw new IllegalStateException(
                                "TC_25: Ten phong trong."
                        );
                    }

                    rooms.add(room);

                    for (int dayIndex = 0;
                         dayIndex < DAYS.size();
                         dayIndex++) {

                        String day = DAYS.get(dayIndex);

                        WebElement cell =
                                cells.get(dayIndex + 1);

                        String content =
                                clean(cell.getText());

                        List<WebElement> cards =
                                cell.findElements(CARD);

                        String cellKey =
                                room + " / " + day;

                        if (content.isEmpty()) {

                            if (!cards.isEmpty()) {
                                throw new IllegalStateException(
                                        "TC_25: O trong co card: "
                                                + cellKey
                                );
                            }

                            emptyCells.add(cellKey);
                            continue;
                        }

                        if (cards.isEmpty()) {
                            throw new IllegalStateException(
                                    "TC_25: Co noi dung nhung "
                                            + "khong co card: " + cellKey
                            );
                        }

                        occupiedCells++;

                        for (WebElement card : cards) {

                            lessons.addAll(
                                    parseLessons(
                                            room,
                                            day,
                                            clean(card.getText())
                                    )
                            );
                        }
                    }
                }

                return new Snapshot(
                        List.copyOf(rooms),
                        List.copyOf(lessons),
                        List.copyOf(emptyCells),
                        occupiedCells
                );

            } catch (StaleElementReferenceException ex) {
                return null;
            }
        });
    }

    public List<String> expectedRoomsNextWeek() {
        return ROOMS_B;
    }

    private void ensureWeek(
            String expectedWeek,
            String expectedCode
    ) {

        if (!expectedWeek.equals(selectedWeek())
                || !expectedCode.equals(selectedWeekCode())) {

            throw new IllegalStateException(
                    "TC_25: Sai tuan truoc khi chuyen. "
                            + selectedWeek()
                            + ", code=" + selectedWeekCode()
            );
        }
    }

    private void waitForWeek(
            String week,
            String code,
            String firstDate,
            String lastDate,
            int expectedRooms,
            int expectedCards
    ) {

        waitDriver().until(d -> {

            try {
                String actualWeek =
                        clean(
                                d.findElement(WEEK_SELECT)
                                        .getText()
                        );

                String actualCode =
                        d.findElement(WEEK_INPUT)
                                .getDomProperty("value");

                if (!week.equals(actualWeek)
                        || !code.equals(actualCode)) {
                    return false;
                }

                WebElement table =
                        d.findElement(TABLE);

                if (!table.isDisplayed()) {
                    return false;
                }

                List<WebElement> headers =
                        d.findElements(HEADERS);

                if (headers.size() != 8) {
                    return false;
                }

                if (!clean(headers.get(1).getText())
                        .contains(firstDate)
                        || !clean(headers.get(7).getText())
                        .contains(lastDate)) {
                    return false;
                }

                List<WebElement> rows =
                        table.findElements(ROWS);

                if (rows.size() != expectedRooms) {
                    return false;
                }

                for (WebElement row : rows) {
                    if (row.findElements(CELLS).size() != 8) {
                        return false;
                    }
                }

                return table.findElements(CARD).size()
                        == expectedCards;

            } catch (StaleElementReferenceException ex) {
                return false;
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
                        "TC_25: Van ban khong nhan dien: "
                                + content.substring(
                                cursor, matcher.start()
                        )
                );
            }

            result.add(new RoomScheduleRecord(
                    room,
                    day,
                    clean(matcher.group(1)),
                    clean(matcher.group(2)),
                    clean(matcher.group(3)),
                    clean(matcher.group(4)),
                    clean(matcher.group(5))
            ));

            cursor = matcher.end();
        }

        if (!content.substring(cursor).isBlank()) {
            throw new IllegalStateException(
                    "TC_25: Noi dung chua doc duoc: "
                            + content.substring(cursor)
            );
        }

        if (result.isEmpty()) {
            throw new IllegalStateException(
                    "TC_25: Khong phan tich duoc "
                            + "luot hoc tai " + room + " / " + day
            );
        }

        return result;
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
