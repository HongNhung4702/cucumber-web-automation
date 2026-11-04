
package com.hongnhung.cucumberweb.pages.student;

import com.hongnhung.cucumberweb.driver.DriverManager;
import com.hongnhung.cucumberweb.models.schedules.ScheduleRecord;
import com.hongnhung.cucumberweb.pages.BasePage;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public final class SchedulesPage extends BasePage {

    private static final String PAGE_URL =
            "https://daotao.qnu.edu.vn/student/schedules";

    private static final By YEAR_SELECT =
            By.id("mui-component-select-YearStudy");

    private static final By TERM_SELECT =
            By.id("mui-component-select-TermID");

    private static final By OPTIONS =
            By.cssSelector("[role='option']");

    private static final By LISTBOX =
            By.cssSelector("[role='listbox']");

    private static final By TABLES =
            By.cssSelector("table");

    private static final List<String> EXPECTED_HEADERS =
            List.of(
                    "STT",
                    "Mã lớp học phần/Tên học phần",
                    "Tên giáo viên",
                    "Số TC",
                    "Thứ",
                    "Tiết(giờ bắt đầu)",
                    "Phòng học",
                    "Tuần",
                    "Từ ngày - đến ngày",
                    "Phòng/địa điểm",
                    "Địa chỉ"
            );

    public SchedulesPage(DriverManager driverManager) {
        super(driverManager);
    }

    private WebDriverWait waitFor() {
        return new WebDriverWait(
                driver(),
                Duration.ofSeconds(15)
        );
    }

    // =========================================
    // 1. MỞ TRANG
    // =========================================

    public void open() {

        driver().get(PAGE_URL);

        waitForSelect(YEAR_SELECT);
        waitForSelect(TERM_SELECT);

        System.out.println(
                "[TC_21] Đã mở trang thời khóa biểu."
        );
    }

    // =========================================
    // 2. CHỌN BỘ LỌC
    // =========================================

    public void search(String year, String term) {

        String code = termCode(term);

        for (int attempt = 1; attempt <= 3; attempt++) {

            System.out.println(
                    "[TC_21] Đồng bộ bộ lọc lần "
                            + attempt + "/3"
            );

            try {

                closeOpenMenu();

                // Chọn năm học.
                choose(YEAR_SELECT, year);

                // Đợi bộ lọc học kỳ được render lại.
                waitForSelect(TERM_SELECT);

                if (!year.equals(
                        selectedIfPresent(YEAR_SELECT))) {

                    System.out.println(
                            "[TC_21] Năm học bị reset."
                    );

                    continue;
                }

                // Chọn học kỳ.
                choose(TERM_SELECT, term);

                // Xác nhận hai bộ lọc.
                waitFor().until(d ->
                        year.equals(
                                selectedIfPresent(YEAR_SELECT))
                                &&
                                term.equals(
                                        selectedIfPresent(TERM_SELECT))
                );

                System.out.println(
                        "[TC_21] Bộ lọc đã đúng: "
                                + year + " | " + term
                );

                // Xác nhận tiêu đề bằng văn bản
                // đang hiển thị trên giao diện.
                waitFor().until(d ->
                        hasResultHeading(year, code)
                );

                System.out.println(
                        "[TC_21] Tiêu đề đã đúng: "
                                + year + " | " + code
                );

                // Đợi bảng có dữ liệu.
                waitForTable();

                System.out.println(
                        "[TC_21] Bảng thời khóa biểu đã tải."
                );

                // Đưa bảng vào vùng nhìn thấy.
                scrollToTable();

                // Kiểm tra bộ lọc sau khi tải dữ liệu.
                if (!year.equals(
                        selectedIfPresent(YEAR_SELECT))
                        ||
                        !term.equals(
                                selectedIfPresent(TERM_SELECT))
                        ||
                        !hasResultHeading(year, code)) {

                    throw new IllegalStateException(
                            "Bộ lọc thay đổi sau khi tải bảng."
                    );
                }

                System.out.println(
                        "[TC_21] Chọn thành công: "
                                + year + " | " + term
                );

                return;

            } catch (TimeoutException
                     | NoSuchElementException
                     | StaleElementReferenceException
                     | IllegalStateException ex) {

                System.out.println(
                        "[TC_21] Lần "
                                + attempt
                                + " chưa thành công: "
                                + ex.getClass()
                                .getSimpleName()
                );

                System.out.println(
                        "[TC_21] Trạng thái hiện tại: "
                                + selectedIfPresent(YEAR_SELECT)
                                + " | "
                                + selectedIfPresent(TERM_SELECT)
                                + " | Tiêu đề đúng="
                                + hasResultHeading(year, code)
                );

                if (attempt == 3) {
                    throw new AssertionError(
                            "Không tải được TC_21 với "
                                    + year + " | " + term,
                            ex
                    );
                }
            }
        }
    }

    private WebElement waitForSelect(By locator) {

        return waitFor().until(d -> {

            try {

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

            } catch (StaleElementReferenceException ex) {
                return null;
            }
        });
    }

    private void choose(
            By locator,
            String expectedValue
    ) {

        waitForSelect(locator);

        String current =
                selectedIfPresent(locator);

        System.out.println(
                "[TC_21] Hiện tại: " + current
                        + " | Cần chọn: " + expectedValue
        );

        if (expectedValue.equals(current)) {
            return;
        }

        closeOpenMenu();

        WebElement select = waitForSelect(locator);

        ((JavascriptExecutor) driver())
                .executeScript(
                        "arguments[0].scrollIntoView("
                                + "{block:'center'});",
                        select
                );

        waitFor().until(
                ExpectedConditions.elementToBeClickable(
                        locator
                )
        ).click();

        WebElement option = waitFor().until(d -> {

            try {

                for (WebElement item :
                        d.findElements(OPTIONS)) {

                    if (item.isDisplayed()
                            && expectedValue.equals(
                            clean(item.getText()))) {

                        return item;
                    }
                }

                return null;

            } catch (StaleElementReferenceException ex) {
                return null;
            }
        });

        option.click();

        waitFor().until(d ->
                expectedValue.equals(
                        selectedIfPresent(locator))
        );

        System.out.println(
                "[TC_21] Đã chọn: " + expectedValue
        );
    }

    private String selectedIfPresent(By locator) {

        try {

            List<WebElement> elements =
                    driver().findElements(locator);

            if (elements.isEmpty()) {
                return "";
            }

            WebElement element = elements.get(0);

            return element.isDisplayed()
                    ? clean(element.getText())
                    : "";

        } catch (StaleElementReferenceException ex) {
            return "";
        }
    }

    public String selectedYear() {
        return selectedIfPresent(YEAR_SELECT);
    }

    public String selectedTerm() {
        return selectedIfPresent(TERM_SELECT);
    }

    private void closeOpenMenu() {

        try {

            for (WebElement menu :
                    driver().findElements(LISTBOX)) {

                if (menu.isDisplayed()) {

                    driver().switchTo()
                            .activeElement()
                            .sendKeys(Keys.ESCAPE);

                    waitFor().until(d ->
                            d.findElements(LISTBOX)
                                    .stream()
                                    .noneMatch(
                                            WebElement::isDisplayed)
                    );

                    return;
                }
            }

        } catch (StaleElementReferenceException
                 | TimeoutException ex) {

            System.out.println(
                    "[TC_21] Menu đang cập nhật."
            );
        }
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
    // 3. XÁC NHẬN TIÊU ĐỀ
    // =========================================

    private boolean hasResultHeading(
            String year,
            String code
    ) {

        Object value =
                ((JavascriptExecutor) driver())
                        .executeScript(
                                "return document.body.innerText;"
                        );

        if (value == null) {
            return false;
        }

        String visibleText = value.toString()
                .replace('\u00A0', ' ')
                .replace('\u202F', ' ')
                .replaceAll("\\s+", " ");

        // Không phụ thuộc tiêu đề có được
        // chia thành nhiều thẻ HTML hay không.
        String expression =
                "Năm\\s*học\\s*:\\s*"
                        + Pattern.quote(year)
                        + "\\s*-\\s*Học\\s*kỳ\\s*:\\s*"
                        + Pattern.quote(code);

        return Pattern.compile(expression)
                .matcher(visibleText)
                .find();
    }

    // =========================================
    // 4. ĐỌC TIÊU ĐỀ CỘT
    // =========================================

    public List<String> expectedHeaders() {
        return EXPECTED_HEADERS;
    }

    public List<String> tableHeaders() {

        List<String> headers = new ArrayList<>();

        for (WebElement th :
                scheduleTable().findElements(
                        By.cssSelector("thead th")
                )) {

            headers.add(clean(th.getText()));
        }

        return List.copyOf(headers);
    }

    public boolean headersMatch() {
        return EXPECTED_HEADERS.equals(tableHeaders());
    }

    private WebElement scheduleTable() {

        for (WebElement table :
                driver().findElements(TABLES)) {

            List<String> headers =
                    table.findElements(
                                    By.cssSelector("thead th")
                            ).stream()
                            .map(e -> clean(e.getText()))
                            .toList();

            if (headers.contains(
                    "Mã lớp học phần/Tên học phần")
                    && headers.contains(
                    "Tiết(giờ bắt đầu)")
                    && headers.contains(
                    "Từ ngày - đến ngày")) {

                return table;
            }
        }

        throw new IllegalStateException(
                "Không tìm thấy bảng thời khóa biểu."
        );
    }

    private void waitForTable() {

        waitFor().until(d -> {

            try {

                WebElement table = scheduleTable();

                if (!EXPECTED_HEADERS.equals(
                        table.findElements(
                                        By.cssSelector("thead th")
                                ).stream()
                                .map(e -> clean(e.getText()))
                                .toList())) {

                    return false;
                }

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

                    if (count != 7 && count != 11) {
                        return false;
                    }
                }

                return true;

            } catch (StaleElementReferenceException
                     | NoSuchElementException
                     | IllegalStateException ex) {

                return false;
            }
        });
    }

    // =========================================
    // 5. CUỘN ĐẾN BẢNG DỮ LIỆU
    // =========================================

    private void scrollToTable() {

        WebElement table = scheduleTable();

        ((JavascriptExecutor) driver())
                .executeScript(
                        "arguments[0].scrollIntoView("
                                + "{behavior:'instant',"
                                + "block:'start'});",
                        table
                );

        System.out.println(
                "[TC_21] Đã cuộn đến bảng thời khóa biểu."
        );
    }

    // =========================================
    // 6. ĐỌC BẢNG CÓ ROWSPAN
    // =========================================

    public List<ScheduleRecord> readRecords() {

        waitForTable();

        List<ScheduleRecord> records =
                new ArrayList<>();

        List<WebElement> rows =
                scheduleTable().findElements(
                        By.cssSelector("tbody tr")
                );

        String stt = null;
        String code = null;
        String name = null;
        String teacher = null;
        String credits = null;

        int remainingRows = 0;

        for (int index = 0; index < rows.size(); index++) {

            List<WebElement> cells =
                    rows.get(index).findElements(
                            By.cssSelector("td")
                    );

            int offset;

            if (cells.size() == 11) {

                if (remainingRows != 0) {
                    throw new IllegalStateException(
                            "Nhóm rowspan trước chưa kết thúc."
                    );
                }

                stt = clean(cells.get(0).getText());

                String combined =
                        clean(cells.get(1).getText());

                String[] parts =
                        combined.split("\\s*-\\s*", 2);

                if (parts.length != 2
                        || !parts[0].matches("\\d+")
                        || parts[1].isBlank()) {

                    throw new IllegalStateException(
                            "Sai định dạng mã/tên học phần: "
                                    + combined
                    );
                }

                code = parts[0];
                name = parts[1];

                teacher = clean(cells.get(2).getText());
                credits = clean(cells.get(3).getText());

                int span = rowSpan(cells.get(0));

                if (span != rowSpan(cells.get(1))
                        || span != rowSpan(cells.get(2))
                        || span != rowSpan(cells.get(3))) {

                    throw new IllegalStateException(
                            "rowspan không đồng nhất."
                    );
                }

                remainingRows = span;
                offset = 4;

            } else if (cells.size() == 7) {

                if (remainingRows <= 0 || code == null) {
                    throw new IllegalStateException(
                            "Dòng lịch học không có "
                                    + "thông tin học phần."
                    );
                }

                offset = 0;

            } else {

                throw new IllegalStateException(
                        "Hàng " + (index + 1)
                                + " có "
                                + cells.size()
                                + " ô."
                );
            }

            if (remainingRows <= 0) {
                throw new IllegalStateException(
                        "rowspan không hợp lệ."
                );
            }

            records.add(new ScheduleRecord(
                    stt,
                    code,
                    name,
                    teacher,
                    credits,
                    clean(cells.get(offset).getText()),
                    clean(cells.get(offset + 1).getText()),
                    clean(cells.get(offset + 2).getText()),
                    clean(cells.get(offset + 3).getText()),
                    clean(cells.get(offset + 4).getText()),
                    clean(cells.get(offset + 5).getText()),
                    clean(cells.get(offset + 6).getText())
            ));

            remainingRows--;
        }

        if (remainingRows != 0) {
            throw new IllegalStateException(
                    "Bảng kết thúc giữa nhóm rowspan."
            );
        }

        System.out.println(
                "[TC_21] Actual: "
                        + countCourses(records)
                        + " học phần, "
                        + records.size()
                        + " dòng lịch học."
        );

        return List.copyOf(records);
    }

    private int rowSpan(WebElement cell) {

        String value =
                cell.getAttribute("rowspan");

        if (value == null || value.isBlank()) {
            return 1;
        }

        return Integer.parseInt(value);
    }

    private int countCourses(
            List<ScheduleRecord> records
    ) {

        Set<String> codes =
                new LinkedHashSet<>();

        for (ScheduleRecord record : records) {
            codes.add(record.maLopHocPhan());
        }

        return codes.size();
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
