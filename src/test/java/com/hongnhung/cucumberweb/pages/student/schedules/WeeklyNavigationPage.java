
package com.hongnhung.cucumberweb.pages.student.schedules;

import com.hongnhung.cucumberweb.driver.DriverManager;
import com.hongnhung.cucumberweb.pages.BasePage;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import java.util.List;

public final class WeeklyNavigationPage extends BasePage {

    public static final String WEEK_A =
            "18/11/2024-24/11/2024";

    public static final String WEEK_B =
            "25/11/2024-01/12/2024";

    private static final By WEEK_SELECT =
            By.id("demo-simple-select-helper");

    private static final By WEEK_CODE =
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
            By.cssSelector(
                    "#ScheduleComponent table tbody tr"
            );

    // Nút tuần trước: xác định bằng SVG đã khảo sát.
    private static final By PREVIOUS_BUTTON =
            By.xpath(
                    "//*[@id='ScheduleComponent']//button"
                            + "[.//*[local-name()='path' and "
                            + "starts-with(@d,'M6 6h2v12H6')]]"
            );

    // Nút tuần tiếp theo: xác định bằng SVG thực tế.
    private static final By NEXT_BUTTON =
            By.xpath(
                    "//*[@id='ScheduleComponent']//button"
                            + "[.//*[local-name()='path' and "
                            + "starts-with(@d,'m6 18 8.5-6')]]"
            );

    private final StudentWeeklySchedulePage weeklyPage;

    public WeeklyNavigationPage(
            DriverManager driverManager,
            StudentWeeklySchedulePage weeklyPage
    ) {
        super(driverManager);
        this.weeklyPage = weeklyPage;
    }

    public void openInitialWeek() {

        // Tái sử dụng thao tác mở tuần A đã PASS ở TC_22.
        weeklyPage.open();

        waitForWeek(WEEK_A, "47",
                "18/11/2024", "24/11/2024");
    }

    public void moveNext() {

        ensureWeek(WEEK_A, "47");

        click(NEXT_BUTTON);

        waitForWeek(WEEK_B, "48",
                "25/11/2024", "01/12/2024");

        System.out.println(
                "[TC_24] Chuyen sang tuan B: " + WEEK_B
        );
    }

    public void movePrevious() {

        ensureWeek(WEEK_B, "48");

        click(PREVIOUS_BUTTON);

        waitForWeek(WEEK_A, "47",
                "18/11/2024", "24/11/2024");

        System.out.println(
                "[TC_24] Quay lai tuan A: " + WEEK_A
        );
    }

    public String selectedWeek() {

        return waitUntilVisible(WEEK_SELECT)
                .getText()
                .strip()
                .replaceAll("\\s+", " ");
    }

    public String selectedWeekCode() {

        return driver().findElement(WEEK_CODE)
                .getDomProperty("value");
    }

    public List<String> headers() {
        return weeklyPage.headers();
    }

    public StudentWeeklySchedulePage.Snapshot snapshot() {
        return weeklyPage.snapshot();
    }

    private void ensureWeek(
            String expectedWeek,
            String expectedCode
    ) {

        if (!expectedWeek.equals(selectedWeek())
                || !expectedCode.equals(selectedWeekCode())) {

            throw new IllegalStateException(
                    "TC_24: Tuan truoc khi dieu huong "
                            + "khong dung. Week=" + selectedWeek()
                            + ", Code=" + selectedWeekCode()
            );
        }
    }

    private void waitForWeek(
            String week,
            String code,
            String firstDate,
            String lastDate
    ) {

        waitDriver().until(d -> {

            try {
                WebElement weekElement =
                        d.findElement(WEEK_SELECT);

                String actualWeek =
                        weekElement.getText()
                                .strip()
                                .replaceAll("\\s+", " ");

                String actualCode =
                        d.findElement(WEEK_CODE)
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

                if (!headers.get(1).getText()
                        .contains(firstDate)
                        || !headers.get(7).getText()
                        .contains(lastDate)) {
                    return false;
                }

                List<WebElement> rows =
                        d.findElements(ROWS);

                if (rows.size() != 3) {
                    return false;
                }

                for (WebElement row : rows) {

                    if (row.findElements(
                            By.cssSelector("td")
                    ).size() != 8) {
                        return false;
                    }
                }

                // Đợi đủ lịch đã xác minh từ HTML.
                return table.findElements(
                        By.cssSelector(
                                "tbody td .MuiCard-root"
                        )
                ).size() == 10;

            } catch (StaleElementReferenceException ex) {
                return false;
            }
        });
    }
}
