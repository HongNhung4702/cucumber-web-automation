
package com.hongnhung.cucumberweb.steps.lecturers;

import com.hongnhung.cucumberweb.assertions.lecturers.LecturerScheduleComparator;
import com.hongnhung.cucumberweb.models.lecturers.LecturerScheduleRecord;
import com.hongnhung.cucumberweb.pages.publicpages.LecturerTimetablePage;
import com.hongnhung.cucumberweb.pages.lecturers.LecturerResetPage;
import com.hongnhung.cucumberweb.testdata.lecturers.LecturerScheduleBaselineReader;
import com.hongnhung.cucumberweb.driver.DriverManager;

import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public final class LecturerSearchAgainSteps {

    private static final String YEAR = "2026-2027";
    private static final String TERM = "Học kỳ 1";
    private static final String FACULTY = "Khoa Công nghệ thông tin";
    private static final String TEACHER = "Phạm Trần Thiện";

    private static final By RESET_BUTTON = By.xpath(
            "//*[@id='Schedules']//button[normalize-space(.)='Làm mới']"
    );

    private final LecturerTimetablePage page;
    private final LecturerResetPage resetPage;
    private final LecturerScheduleBaselineReader reader;
    private final LecturerScheduleComparator comparator;
    private final DriverManager manager;

    private List<LecturerScheduleRecord> firstResult;

    public LecturerSearchAgainSteps(
            LecturerTimetablePage page,
            LecturerScheduleBaselineReader reader,
            DriverManager manager
    ) {
        this.page = page;
        this.reader = reader;
        this.manager = manager;
        this.resetPage = new LecturerResetPage(manager);
        this.comparator = new LecturerScheduleComparator();
    }

    @When("người dùng tra cứu giảng viên lần đầu TC_41")
    public void firstSearch() {

        page.open();

        page.selectFilters(
                YEAR, TERM, FACULTY, TEACHER
        );

        page.clickFilter(
                YEAR, TERM, FACULTY, TEACHER
        );

        firstResult = page.readRecords();

        assertFalse(
                firstResult.isEmpty(),
                "TC_41: Lần đầu không có dữ liệu."
        );

        LecturerScheduleComparator.Result result =
                comparator.compare(
                        reader.readRecords(),
                        firstResult
                );

        assertTrue(
                result.passed(),
                "TC_41: Lần đầu sai Baseline.\n"
                        + result.details()
        );

        System.out.println(
                "[TC_41] Lần 1: "
                        + firstResult.size() + " dòng MATCH."
        );
    }

    @When("người dùng làm mới điều kiện TC_41")
    public void resetFilters() {

        // Đưa nút Làm mới vào giữa màn hình
        // để tránh thanh header che khi click.
        WebElement button = manager.getDriver()
                .findElement(RESET_BUTTON);

        ((JavascriptExecutor) manager.getDriver())
                .executeScript(
                        "arguments[0].scrollIntoView("
                                + "{block:'center',"
                                + "behavior:'instant'});",
                        button
                );

        WebDriverWait wait = new WebDriverWait(
                manager.getDriver(),
                Duration.ofSeconds(20)
        );

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        RESET_BUTTON
                )
        ).click();

        wait.until(d ->
                resetPage.defaultFilters().equals(
                        resetPage.filters()
                ) && resetPage.emptyResult()
        );

        assertEquals(
                resetPage.defaultFilters(),
                resetPage.filters(),
                "TC_41: Bộ lọc chưa trở về mặc định."
        );

        assertTrue(
                resetPage.emptyResult(),
                "TC_41: Bảng chưa trở về trạng thái rỗng."
        );

        System.out.println(
                "[TC_41] Làm mới: MATCH."
        );
    }

    @Then("tra cứu lại TC_41 cho kết quả giống lần đầu và baseline")
    public void secondSearch() {

        page.selectFilters(
                YEAR, TERM, FACULTY, TEACHER
        );

        page.clickFilter(
                YEAR, TERM, FACULTY, TEACHER
        );

        assertTrue(
                page.filtersMatch(
                        YEAR, TERM, FACULTY, TEACHER
                ),
                "TC_41: Bộ lọc lần hai không đúng."
        );

        List<LecturerScheduleRecord> secondResult =
                page.readRecords();

        List<LecturerScheduleRecord> expected =
                reader.readRecords();

        LecturerScheduleComparator.Result baselineResult =
                comparator.compare(
                        expected,
                        secondResult
                );

        assertTrue(
                baselineResult.passed(),
                "TC_41: Lần hai sai Baseline.\n"
                        + baselineResult.details()
        );

        LecturerScheduleComparator.Result repeatResult =
                comparator.compare(
                        firstResult,
                        secondResult
                );

        assertTrue(
                repeatResult.passed(),
                "TC_41: Lần hai khác lần đầu.\n"
                        + repeatResult.details()
        );

        assertEquals(
                firstResult.size(),
                secondResult.size(),
                "TC_41: Số dòng thay đổi."
        );

        System.out.println(
                "[TC_41] Lần 2: "
                        + secondResult.size() + " dòng MATCH."
        );

        System.out.println(
                "[TC_41] Lọc -> Làm mới -> Lọc lại: PASS."
        );
    }
}
