
package com.hongnhung.cucumberweb.pages.lecturers;

import com.hongnhung.cucumberweb.config.ConfigManager;
import com.hongnhung.cucumberweb.driver.DriverManager;
import com.hongnhung.cucumberweb.pages.BasePage;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import java.util.List;

public final class LecturerResetPage extends BasePage {

    private static final By YEAR = field("Năm học");
    private static final By TERM = field("Học kỳ");
    private static final By FACULTY = field("Khoa");
    private static final By TEACHER = field("Mã GV / Họ tên");

    private static final By FILTER = By.xpath(
            "//*[@id='Schedules']//button[normalize-space(.)='Lọc']"
    );

    private static final By RESET = By.xpath(
            "//*[@id='Schedules']//button[normalize-space(.)='Làm mới']"
    );

    private static final By TABLE = By.cssSelector(
            "#Schedules table.schedule-table"
    );

    private static By field(String label) {
        return By.xpath(
                "//*[@id='Schedules']//label[normalize-space(.)='"
                        + label + "']/following-sibling::div//input"
        );
    }

    public record FilterState(
            String year,
            String term,
            String faculty,
            String teacher
    ) {}

    public LecturerResetPage(DriverManager manager) {
        super(manager);
    }

    public void open() {
        String baseUrl = ConfigManager.get("base.url")
                .replaceAll("/+$", "");

        driver().navigate().to(
                baseUrl + "/ThoiKhoaBieuGiangVien"
        );

        waitUntilVisible(YEAR);
    }

    public FilterState filters() {
        return new FilterState(
                value(YEAR),
                value(TERM),
                value(FACULTY),
                value(TEACHER)
        );
    }

    private String value(By locator) {
        return driver().findElement(locator)
                .getDomProperty("value");
    }

    public void searchTeacher() {

        choose(FACULTY, "Khoa Công nghệ thông tin");

        WebElement input = waitUntilVisible(TEACHER);
        input.click();
        input.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        input.sendKeys("Phạm Trần Thiện");

        waitDriver().until(d ->
                "Phạm Trần Thiện".equals(value(TEACHER))
        );

        click(FILTER);

        waitDriver().until(d -> {
            try {
                WebElement table = d.findElement(TABLE);

                List<WebElement> rows = table.findElements(
                        By.cssSelector("tbody > tr")
                );

                return !rows.isEmpty()
                        && !isEmptyRow(rows)
                        && table.isDisplayed();

            } catch (StaleElementReferenceException ex) {
                return false;
            }
        });
    }

    private void choose(By locator, String expected) {

        WebElement input = waitUntilVisible(locator);

        if (expected.equals(value(locator))) {
            return;
        }

        input.click();
        input.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        input.sendKeys(expected);

        By option = By.xpath(
                "//*[@role='option' and normalize-space(.)='"
                        + expected + "']"
        );

        waitUntilVisible(option);
        click(option);

        waitDriver().until(d -> {
            try {
                return expected.equals(value(locator));
            } catch (StaleElementReferenceException ex) {
                return false;
            }
        });
    }

    public void reset() {

        click(RESET);

        waitDriver().until(d -> {
            try {
                return filters().equals(defaultFilters())
                        && emptyResult();

            } catch (StaleElementReferenceException ex) {
                return false;
            }
        });
    }

    public FilterState defaultFilters() {
        return new FilterState(
                "2026-2027",
                "Học kỳ 1",
                "Trường đại học Quy Nhơn",
                ""
        );
    }

    public boolean emptyResult() {

        List<WebElement> rows = driver()
                .findElement(TABLE)
                .findElements(By.cssSelector("tbody > tr"));

        return isEmptyRow(rows);
    }

    private boolean isEmptyRow(List<WebElement> rows) {

        if (rows.size() != 1) {
            return false;
        }

        List<WebElement> cells = rows.get(0)
                .findElements(By.cssSelector("td"));

        return cells.size() == 1
                && "13".equals(
                cells.get(0).getAttribute("colspan")
        )
                && "Chưa có lịch học".equals(
                cells.get(0).getText().strip()
        );
    }
}
