
package com.hongnhung.cucumberweb.pages.publicpages;

import com.hongnhung.cucumberweb.driver.DriverManager;
import com.hongnhung.cucumberweb.models.lecturers.LecturerScheduleRecord;
import com.hongnhung.cucumberweb.pages.BasePage;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public final class LecturerTimetablePage extends BasePage {

    private static final String URL =
            "https://daotao.qnu.edu.vn/ThoiKhoaBieuGiangVien";

    // Ba combobox theo thứ tự DOM đã khảo sát:
    // Năm học, Học kỳ, Khoa.
    // Không sử dụng các ID MUI động như :rt7:.
    private static final By YEAR =
            By.xpath("(//input[@role='combobox'])[1]");

    private static final By TERM =
            By.xpath("(//input[@role='combobox'])[2]");

    private static final By FACULTY =
            By.xpath("(//input[@role='combobox'])[3]");

    private static final By LECTURER =
            By.xpath(
                    "//label[contains(normalize-space(.),"
                            + "'Mã GV')]/following::input[1]"
            );

    private static final By FILTER_BUTTON =
            By.xpath(
                    "//button[contains(normalize-space(.),'Lọc')]"
            );

    private static final By OPTIONS =
            By.cssSelector("[role='option']");

    private static final By TABLES =
            By.cssSelector("table");

    private static final List<String> TOP_HEADERS = List.of(
            "Mã LHP",
            "Tên học phần",
            "Số tiết",
            "Số TC",
            "Mã lớp",
            "Tên CBGV",
            "Sỉ số",
            "Thứ",
            "Tiết",
            "Phòng",
            "Tuần học"
    );

    private static final List<String> SUB_HEADERS =
            List.of("LT", "TH", "TL");

    public LecturerTimetablePage(
            DriverManager driverManager
    ) {
        super(driverManager);
    }

    private WebDriverWait waitFor() {
        WebDriverWait wait = new WebDriverWait(
                driver(),
                Duration.ofSeconds(20)
        );

        wait.pollingEvery(Duration.ofMillis(350));
        wait.ignoring(StaleElementReferenceException.class);

        return wait;
    }

    // =========================================
    // 1. MỞ TRANG TRA CỨU
    // =========================================

    public void open() {

        driver().get(URL);

        waitFor().until(d ->
                d.findElements(
                        By.cssSelector("input[role='combobox']")
                ).size() >= 3
        );

        waitFor().until(d ->
                d.findElements(FILTER_BUTTON)
                        .stream()
                        .anyMatch(WebElement::isDisplayed)
        );

        System.out.println(
                "[TC_38] Đã mở trang tra cứu giảng viên."
        );
    }

    // =========================================
    // 2. CHỌN BỘ LỌC
    // =========================================

    public void selectFilters(
            String year,
            String term,
            String faculty,
            String lecturer
    ) {

        for (int attempt = 1; attempt <= 3; attempt++) {

            System.out.println(
                    "[TC_38] Chọn bộ lọc lần "
                            + attempt + "/3"
            );

            try {

                chooseCombo(YEAR, year);

                chooseCombo(TERM, term);

                chooseCombo(FACULTY, faculty);

                enterLecturer(lecturer);

                waitFor().until(d ->
                        year.equals(value(YEAR))
                                && term.equals(value(TERM))
                                && faculty.equals(value(FACULTY))
                                && lecturer.equals(value(LECTURER))
                );

                System.out.println(
                        "[TC_38] Đã chọn: "
                                + year + " | "
                                + term + " | "
                                + faculty + " | "
                                + lecturer
                );

                return;

            } catch (TimeoutException
                     | IllegalStateException ex) {

                System.out.println(
                        "[TC_38] Chưa đồng bộ bộ lọc: "
                                + ex.getMessage()
                );

                if (attempt == 3) {
                    throw new AssertionError(
                            "Không chọn được đầy đủ "
                                    + "bộ lọc TC_38.",
                            ex
                    );
                }
            }
        }
    }

    private void chooseCombo(
            By locator,
            String expectedValue
    ) {

        if (expectedValue.equals(value(locator))) {
            return;
        }

        WebElement input = waitFor().until(d -> {
            List<WebElement> elements =
                    d.findElements(locator);

            if (elements.isEmpty()) {
                return null;
            }

            WebElement element = elements.get(0);

            return element.isDisplayed()
                    && element.isEnabled()
                    ? element
                    : null;
        });

        scrollTo(input);
        input.click();

        WebElement option = waitFor().until(d -> {

            for (WebElement item :
                    d.findElements(OPTIONS)) {

                try {

                    if (item.isDisplayed()
                            && expectedValue.equals(
                            clean(item.getText()))) {

                        return item;
                    }

                } catch (StaleElementReferenceException ignored) {
                    // MUI có thể render lại option.
                }
            }

            return null;
        });

        option.click();

        waitFor().until(d ->
                expectedValue.equals(value(locator))
        );

        System.out.println(
                "[TC_38] Đã chọn: " + expectedValue
        );
    }

    private void enterLecturer(String lecturer) {

        WebElement input = waitFor().until(d -> {
            List<WebElement> elements =
                    d.findElements(LECTURER);

            if (elements.isEmpty()) {
                return null;
            }

            WebElement element = elements.get(0);

            return element.isDisplayed()
                    && element.isEnabled()
                    ? element
                    : null;
        });

        if (lecturer.equals(value(LECTURER))) {
            return;
        }

        scrollTo(input);

        input.click();

        input.sendKeys(Keys.chord(
                Keys.CONTROL, "a"
        ));

        input.sendKeys(lecturer);

        waitFor().until(d ->
                lecturer.equals(value(LECTURER))
        );

        System.out.println(
                "[TC_38] Đã nhập giảng viên: "
                        + lecturer
        );
    }

    private String value(By locator) {

        try {
            List<WebElement> elements =
                    driver().findElements(locator);

            if (elements.isEmpty()) {
                return "";
            }

            String result =
                    elements.get(0).getAttribute("value");

            return clean(result);

        } catch (StaleElementReferenceException ex) {
            return "";
        }
    }

    public String selectedYear() {
        return value(YEAR);
    }

    public String selectedTerm() {
        return value(TERM);
    }

    public String selectedFaculty() {
        return value(FACULTY);
    }

    public String selectedLecturer() {
        return value(LECTURER);
    }

    // =========================================
    // 3. NHẤN LỌC VÀ ĐỢI KẾT QUẢ
    // =========================================

    public void clickFilter(
            String year,
            String term,
            String faculty,
            String lecturer
    ) {

        if (!filtersMatch(
                year, term, faculty, lecturer)) {

            throw new AssertionError(
                    "Bộ lọc chưa đúng trước khi nhấn Lọc."
            );
        }

        WebElement button = waitFor().until(d -> {

            for (WebElement item :
                    d.findElements(FILTER_BUTTON)) {

                if (item.isDisplayed()
                        && item.isEnabled()) {
                    return item;
                }
            }

            return null;
        });

        scrollTo(button);
        button.click();

        System.out.println(
                "[TC_38] Đã nhấn Lọc."
        );

        String code = termCode(term);

        waitFor().until(d ->
                filtersMatch(year, term, faculty, lecturer)
                        && headingMatches(
                        year, code, faculty
                )
        );

        System.out.println(
                "[TC_38] Đã xác nhận tiêu đề kết quả."
        );

        waitForTable();

        System.out.println(
                "[TC_38] Bảng kết quả đã tải."
        );
    }

    public boolean filtersMatch(
            String year,
            String term,
            String faculty,
            String lecturer
    ) {

        return year.equals(selectedYear())
                && term.equals(selectedTerm())
                && faculty.equals(selectedFaculty())
                && lecturer.equals(selectedLecturer());
    }

    private String termCode(String term) {

        return switch (term) {
            case "Học kỳ 1" -> "HK01";
            case "Học kỳ 2" -> "HK02";
            case "Học kỳ 3" -> "HK03";
            default -> throw new IllegalArgumentException(
                    "Học kỳ không hợp lệ: " + term
            );
        };
    }

    // =========================================
    // 4. XÁC NHẬN TIÊU ĐỀ KẾT QUẢ
    // =========================================

    public boolean headingMatches(
            String year,
            String termCode,
            String faculty
    ) {

        Object text = ((JavascriptExecutor) driver())
                .executeScript(
                        "return document.body.innerText;"
                );

        if (text == null) {
            return false;
        }

        String pageText = clean(text.toString());

        String regex =
                Pattern.quote(faculty)
                        + "\\s*-\\s*Học\\s*kỳ\\s*:\\s*"
                        + Pattern.quote(termCode)
                        + "\\s*-\\s*Năm\\s*học\\s*:\\s*"
                        + Pattern.quote(year);

        return Pattern.compile(regex)
                .matcher(pageText)
                .find();
    }

    // =========================================
    // 5. KIỂM TRA CẤU TRÚC BẢNG
    // =========================================

    public List<String> topHeaders() {

        WebElement table = resultTable();

        List<WebElement> headerRows =
                table.findElements(
                        By.cssSelector("thead tr")
                );

        if (headerRows.size() != 2) {
            throw new IllegalStateException(
                    "Bảng không có đúng 2 hàng tiêu đề."
            );
        }

        return headerRows.get(0)
                .findElements(By.cssSelector("th"))
                .stream()
                .map(e -> clean(e.getText()))
                .toList();
    }

    public List<String> subHeaders() {

        WebElement table = resultTable();

        List<WebElement> headerRows =
                table.findElements(
                        By.cssSelector("thead tr")
                );

        if (headerRows.size() != 2) {
            throw new IllegalStateException(
                    "Bảng không có đúng 2 hàng tiêu đề."
            );
        }

        return headerRows.get(1)
                .findElements(By.cssSelector("th"))
                .stream()
                .map(e -> clean(e.getText()))
                .toList();
    }

    public List<String> expectedTopHeaders() {
        return TOP_HEADERS;
    }

    public List<String> expectedSubHeaders() {
        return SUB_HEADERS;
    }

    private WebElement resultTable() {

        for (WebElement table :
                driver().findElements(TABLES)) {

            List<String> headers =
                    table.findElements(
                                    By.cssSelector("thead th")
                            ).stream()
                            .map(e -> clean(e.getText()))
                            .toList();

            if (headers.contains("Mã LHP")
                    && headers.contains("Tên CBGV")
                    && headers.contains("Tuần học")) {
                return table;
            }
        }

        throw new IllegalStateException(
                "Không tìm thấy bảng tra cứu giảng viên."
        );
    }

    private void waitForTable() {

        waitFor().until(d -> {

            try {

                WebElement table = resultTable();

                List<WebElement> rows =
                        table.findElements(
                                By.cssSelector("tbody tr")
                        );

                if (rows.isEmpty()) {
                    return false;
                }

                for (WebElement row : rows) {

                    int count =
                            row.findElements(
                                    By.cssSelector("td")
                            ).size();

                    if (count != 13) {
                        return false;
                    }
                }

                return true;

            } catch (StaleElementReferenceException
                     | IllegalStateException ex) {
                return false;
            }
        });
    }

    // =========================================
    // 6. ĐỌC 13 TRƯỜNG DỮ LIỆU
    // =========================================

    public List<LecturerScheduleRecord> readRecords() {

        waitForTable();

        WebElement table = resultTable();

        scrollTo(table);

        List<LecturerScheduleRecord> records =
                new ArrayList<>();

        List<WebElement> rows =
                table.findElements(
                        By.cssSelector("tbody tr")
                );

        for (int index = 0; index < rows.size(); index++) {

            List<WebElement> cells =
                    rows.get(index).findElements(
                            By.cssSelector("td")
                    );

            if (cells.size() != 13) {
                throw new IllegalStateException(
                        "Dòng " + (index + 1)
                                + " có " + cells.size()
                                + " cột, kỳ vọng 13."
                );
            }

            records.add(new LecturerScheduleRecord(
                    cell(cells, 0),
                    cell(cells, 1),
                    cell(cells, 2),
                    cell(cells, 3),
                    cell(cells, 4),
                    cell(cells, 5),
                    cell(cells, 6),
                    cell(cells, 7),
                    cell(cells, 8),
                    cell(cells, 9),
                    cell(cells, 10),
                    cell(cells, 11),
                    cell(cells, 12)
            ));
        }

        System.out.println(
                "[TC_38] Actual: "
                        + records.size()
                        + " dòng, "
                        + countCourses(records)
                        + " mã LHP."
        );

        return List.copyOf(records);
    }

    private String cell(
            List<WebElement> cells,
            int index
    ) {
        return clean(cells.get(index).getText());
    }

    private int countCourses(
            List<LecturerScheduleRecord> records
    ) {
        return (int) records.stream()
                .map(LecturerScheduleRecord::maLHP)
                .distinct()
                .count();
    }

    private void scrollTo(WebElement element) {

        ((JavascriptExecutor) driver())
                .executeScript(
                        "arguments[0].scrollIntoView("
                                + "{block:'center',"
                                + "behavior:'instant'});",
                        element
                );
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
