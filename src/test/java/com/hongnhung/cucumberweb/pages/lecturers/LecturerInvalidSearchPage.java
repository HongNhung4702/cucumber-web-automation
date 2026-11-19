
package com.hongnhung.cucumberweb.pages.lecturers;

import com.hongnhung.cucumberweb.config.ConfigManager;
import com.hongnhung.cucumberweb.driver.DriverManager;
import com.hongnhung.cucumberweb.pages.BasePage;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import java.util.List;

public final class LecturerInvalidSearchPage extends BasePage {

    private static final String YEAR = "2026-2027";
    private static final String TERM = "Học kỳ 1";
    private static final String FACULTY = "Khoa Công nghệ thông tin";
    private static final String KEYWORD = "@$#ff";

    private static final By YEAR_INPUT = By.xpath(
            "//*[@id='Schedules']//label[normalize-space(.)='Năm học']"
                    + "/following-sibling::div//input"
    );

    private static final By TERM_INPUT = By.xpath(
            "//*[@id='Schedules']//label[normalize-space(.)='Học kỳ']"
                    + "/following-sibling::div//input"
    );

    private static final By FACULTY_INPUT = By.xpath(
            "//*[@id='Schedules']//label[normalize-space(.)='Khoa']"
                    + "/following-sibling::div//input"
    );

    private static final By KEYWORD_INPUT = By.xpath(
            "//*[@id='Schedules']//label[normalize-space(.)='Mã GV / Họ tên']"
                    + "/following-sibling::div//input"
    );

    private static final By FILTER_BUTTON = By.xpath(
            "//*[@id='Schedules']//button[normalize-space(.)='Lọc']"
    );

    private static final By RESULT_TABLE =
            By.cssSelector("#Schedules table.schedule-table");

    private static final By ERROR_ALERTS =
            By.cssSelector(
                    "#Schedules [role='alert'], "
                            + "#Schedules .MuiAlert-standardError, "
                            + "#Schedules .MuiAlert-filledError"
            );

    public LecturerInvalidSearchPage(DriverManager manager) {
        super(manager);
    }

    public void search() {

        String baseUrl = ConfigManager.get("base.url")
                .replaceAll("/+$", "");

        driver().navigate().to(
                baseUrl + "/ThoiKhoaBieuGiangVien"
        );

        waitUntilVisible(YEAR_INPUT);

        selectAutocomplete(YEAR_INPUT, YEAR);
        selectAutocomplete(TERM_INPUT, TERM);
        selectAutocomplete(FACULTY_INPUT, FACULTY);

        WebElement keyword = waitUntilVisible(KEYWORD_INPUT);

        keyword.click();
        keyword.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        keyword.sendKeys(KEYWORD);

        waitDriver().until(d -> filtersMatch());

        click(FILTER_BUTTON);

        waitDriver().until(d -> {
            try {
                return filtersMatch()
                        && emptyMessageVisible();
            } catch (NoSuchElementException
                     | StaleElementReferenceException ex) {
                return false;
            }
        });
    }

    private void selectAutocomplete(By locator, String value) {

        WebElement input = waitUntilVisible(locator);

        if (value.equals(input.getDomProperty("value"))) {
            return;
        }

        input.click();
        input.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        input.sendKeys(value);

        By option = By.xpath(
                "//*[@role='option' and normalize-space(.)='"
                        + value + "']"
        );

        waitUntilVisible(option);
        click(option);

        waitDriver().until(d -> {
            try {
                return value.equals(
                        d.findElement(locator)
                                .getDomProperty("value")
                );
            } catch (StaleElementReferenceException ex) {
                return false;
            }
        });
    }

    public boolean filtersMatch() {

        return YEAR.equals(value(YEAR_INPUT))
                && TERM.equals(value(TERM_INPUT))
                && FACULTY.equals(value(FACULTY_INPUT))
                && KEYWORD.equals(value(KEYWORD_INPUT));
    }

    private String value(By locator) {

        return driver().findElement(locator)
                .getDomProperty("value");
    }

    private boolean emptyMessageVisible() {

        WebElement table = driver().findElement(RESULT_TABLE);

        if (!table.isDisplayed()) {
            return false;
        }

        List<WebElement> rows = table.findElements(
                By.cssSelector("tbody > tr")
        );

        if (rows.size() != 1) {
            return false;
        }

        List<WebElement> cells = rows.get(0).findElements(
                By.cssSelector("td")
        );

        return cells.size() == 1
                && "13".equals(
                cells.get(0).getAttribute("colspan")
        )
                && "Chưa có lịch học".equals(
                cells.get(0).getText().strip()
        );
    }

    public boolean hasEmptyResult() {
        return emptyMessageVisible();
    }

    public int dataRowCount() {

        WebElement table = driver().findElement(RESULT_TABLE);

        int count = 0;

        for (WebElement row : table.findElements(
                By.cssSelector("tbody > tr")
        )) {
            List<WebElement> cells = row.findElements(
                    By.cssSelector("td")
            );

            if (cells.size() == 1
                    && "13".equals(
                    cells.get(0).getAttribute("colspan")
            )
                    && "Chưa có lịch học".equals(
                    cells.get(0).getText().strip()
            )) {
                continue;
            }

            count++;
        }

        return count;
    }

    public int visibleErrorCount() {

        int count = 0;

        for (WebElement element :
                driver().findElements(ERROR_ALERTS)) {

            if (element.isDisplayed()) {
                count++;
            }
        }

        return count;
    }
}
