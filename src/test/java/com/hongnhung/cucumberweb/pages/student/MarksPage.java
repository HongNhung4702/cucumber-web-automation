
package com.hongnhung.cucumberweb.pages.student;

import com.hongnhung.cucumberweb.config.ConfigManager;
import com.hongnhung.cucumberweb.driver.DriverManager;
import com.hongnhung.cucumberweb.models.marks.MarkRecord;
import com.hongnhung.cucumberweb.pages.BasePage;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.text.Normalizer;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MarksPage extends BasePage {

    private static final String PATH = "/student/marks";

    // ID thực tế đã xác minh từ DOM.
    private static final By PROGRAM_SELECT =
            By.id("demo-simple-select-helper");

    private static final By YEAR_SELECT =
            By.id("mui-component-select-YearStudy");

    private static final By SEMESTER_SELECT =
            By.id("mui-component-select-TermID");

    private static final By OPTIONS =
            By.cssSelector("[role='option']");

    private static final By STUDENT_MODE_LABEL = By.xpath(
            "//label[contains(normalize-space(.),"
                    + "'Xem điểm sinh viên')"
                    + " and .//input[@type='radio']]"
    );

    private static final By STUDENT_MODE_RADIO = By.xpath(
            "//label[contains(normalize-space(.),"
                    + "'Xem điểm sinh viên')]"
                    + "//input[@type='radio']"
    );

    private static final By TABLES = By.cssSelector(
            "table, [role='table'], [role='grid']"
    );

    private static final By HTML_ROWS =
            By.cssSelector("tbody > tr");

    private static final By HTML_CELLS =
            By.xpath("./td");

    private static final Pattern RGB = Pattern.compile(
            "rgba?\\(\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)"
    );

    private String selectedYear;
    private String selectedSemester;

    public MarksPage(DriverManager driverManager) {
        super(driverManager);
    }

    private WebDriverWait waitFor(int seconds) {
        return new WebDriverWait(
                driver(),
                Duration.ofSeconds(seconds)
        );
    }

    // =========================================
    // 1. MỞ TRANG XEM ĐIỂM
    // =========================================

    public void open() {

        String baseUrl = ConfigManager.get("base.url")
                .replaceAll("/+$", "");

        navigateTo(baseUrl + PATH);

        if (!waitUntilUrlContains(PATH)) {
            throw new AssertionError(
                    "Không mở được trang Xem điểm: "
                            + getCurrentUrl()
            );
        }

        waitUntilVisible(PROGRAM_SELECT);
        waitUntilVisible(YEAR_SELECT);
        waitUntilVisible(SEMESTER_SELECT);
        waitUntilVisible(STUDENT_MODE_LABEL);
    }

    // =========================================
    // 2. XỬ LÝ BỘ LỌC
    // =========================================

    private By filterLocator(String label) {

        return switch (label) {

            case "Chương trình đào tạo" -> PROGRAM_SELECT;

            case "Năm học" -> YEAR_SELECT;

            case "Học kỳ" -> SEMESTER_SELECT;

            default -> throw new IllegalArgumentException(
                    "Bộ lọc không hỗ trợ: " + label
            );
        };
    }

    public String selectedValue(String label) {

        return clean(
                waitUntilVisible(
                        filterLocator(label)
                ).getText()
        );
    }

    private void ensureOpen(By locator) {

        WebElement control = waitUntilVisible(locator);

        if (!"true".equalsIgnoreCase(
                control.getAttribute("aria-expanded"))) {

            control.click();
        }
    }

    private void dismissMenu() {

        boolean menuVisible = false;

        for (WebElement menu : driver().findElements(
                By.cssSelector("[role='listbox']")
        )) {

            if (menu.isDisplayed()) {
                menuVisible = true;
                break;
            }
        }

        if (menuVisible) {

            new Actions(driver())
                    .sendKeys(Keys.ESCAPE)
                    .perform();
        }
    }

    private WebElement visibleOption(String expected) {

        for (WebElement option :
                driver().findElements(OPTIONS)) {

            try {

                if (option.isDisplayed()
                        && option.isEnabled()
                        && clean(option.getText())
                        .equals(clean(expected))) {

                    return option;
                }

            } catch (StaleElementReferenceException ignored) {
            }
        }

        return null;
    }

    private List<String> visibleOptions() {

        List<String> names = new ArrayList<>();

        for (WebElement option :
                driver().findElements(OPTIONS)) {

            try {

                if (option.isDisplayed()) {
                    names.add(clean(option.getText()));
                }

            } catch (StaleElementReferenceException ignored) {
            }
        }

        return names;
    }

    // =========================================
    // 3. CHỌN GIÁ TRỊ DROPDOWN
    // =========================================

    public void choose(String label, String expected) {

        System.out.println(
                "[TC_33] " + label
                        + " | Hiện tại: " + selectedValue(label)
                        + " | Cần chọn: " + expected
        );

        if (clean(expected).equals(selectedValue(label))) {
            return;
        }

        By locator = filterLocator(label);

        WebDriverWait wait = waitFor(18);

        try {

            WebElement option = wait.until(driver -> {

                try {
                    ensureOpen(locator);
                    return visibleOption(expected);

                } catch (StaleElementReferenceException ex) {
                    return null;
                }
            });

            option.click();

        } catch (TimeoutException ex) {

            throw new AssertionError(
                    "Không tìm thấy [" + expected
                            + "] trong bộ lọc [" + label
                            + "]. Options=" + visibleOptions()
                            + "; Năm học="
                            + selectedValue("Năm học"),
                    ex
            );
        }

        try {

            wait.until(driver -> {

                try {
                    return clean(expected).equals(
                            selectedValue(label)
                    );

                } catch (StaleElementReferenceException ex) {
                    return false;
                }
            });

        } catch (TimeoutException ex) {

            throw new AssertionError(
                    "Bộ lọc " + label
                            + " chưa cập nhật hoặc đã reset."
                            + " Expected=" + expected
                            + ", Actual=" + selectedValue(label),
                    ex
            );
        }

        System.out.println(
                "[TC_33] Đã chọn " + label
                        + ": " + selectedValue(label)
        );
    }

    // =========================================
    // 4. CHỌN CHẾ ĐỘ XEM ĐIỂM
    // =========================================

    public void selectStudentMode(String mode) {

        if (!"Xem điểm sinh viên".equals(mode)) {

            throw new IllegalArgumentException(
                    "TC_33 chỉ hỗ trợ Xem điểm sinh viên."
            );
        }

        if (!isStudentModeSelected()) {

            waitUntilVisible(STUDENT_MODE_LABEL).click();
        }

        waitDriver().until(
                driver -> isStudentModeSelected()
        );
    }

    public boolean isStudentModeSelected() {

        return driver()
                .findElement(STUDENT_MODE_RADIO)
                .isSelected();
    }

    // =========================================
    // 5. CHỜ DANH SÁCH HỌC KỲ
    // =========================================

    private String awaitSemesterOption(
            String year,
            String semester
    ) {

        try {

            return waitFor(15).until(driver -> {

                try {

                    if (!year.equals(
                            selectedValue("Năm học"))) {

                        return "YEAR_RESET";
                    }

                    ensureOpen(SEMESTER_SELECT);

                    return visibleOption(semester) != null
                            ? "READY"
                            : null;

                } catch (StaleElementReferenceException ex) {
                    return null;
                }
            });

        } catch (TimeoutException ex) {

            return "NO_SEMESTER_OPTION";
        }
    }

    // =========================================
    // 6. ĐỒNG BỘ NĂM HỌC VÀ HỌC KỲ
    // =========================================

    private void chooseYearAndSemester(
            String year,
            String semester
    ) {

        String lastReason = "";

        for (int attempt = 1; attempt <= 3; attempt++) {

            System.out.println(
                    "[TC_33] Đồng bộ năm học/học kỳ - lần "
                            + attempt + "/3"
            );

            dismissMenu();

            try {

                choose("Năm học", year);

                String state =
                        awaitSemesterOption(year, semester);

                System.out.println(
                        "[TC_33] Trạng thái tải học kỳ: "
                                + state
                                + "; Năm học="
                                + selectedValue("Năm học")
                                + "; Options=" + visibleOptions()
                );

                if ("READY".equals(state)) {

                    choose("Học kỳ", semester);

                    if (year.equals(
                            selectedValue("Năm học"))
                            && semester.equals(
                            selectedValue("Học kỳ"))) {

                        dismissMenu();
                        return;
                    }
                }

                lastReason = state
                        + "; year="
                        + selectedValue("Năm học")
                        + "; term="
                        + selectedValue("Học kỳ");

            } catch (AssertionError error) {

                lastReason = error.getMessage();
            }

            dismissMenu();

            if (attempt < 3) {

                System.out.println(
                        "[TC_33] Chưa đồng bộ: "
                                + lastReason
                                + ". Thử chọn lại."
                );

                if (year.equals(
                        selectedValue("Năm học"))) {

                    choose("Năm học", "-- Tất cả --");
                }
            }
        }

        throw new AssertionError(
                "Không chọn được " + semester
                        + " sau 3 lần."
                        + " Trạng thái cuối: " + lastReason
        );
    }

    // =========================================
    // 7. TRA CỨU BẢNG ĐIỂM
    // =========================================

    public void search(
            String mode,
            String program,
            String year,
            String semester
    ) {

        open();

        selectStudentMode(mode);

        choose("Chương trình đào tạo", program);

        chooseYearAndSemester(year, semester);

        selectedYear = year;
        selectedSemester = semester;

        waitForTableLoaded();
    }

    // =========================================
    // 8. TÌM BẢNG ĐIỂM
    // =========================================

    private WebElement findMarksTable() {

        for (WebElement candidate :
                driver().findElements(TABLES)) {

            try {

                if (!candidate.isDisplayed()) {
                    continue;
                }

                String text = clean(candidate.getText());

                if (text.contains("Mã học phần")
                        && text.contains("Điểm 10")
                        && text.contains("Điểm chữ")) {

                    return candidate;
                }

            } catch (StaleElementReferenceException ignored) {
            }
        }

        return null;
    }

    private WebElement requireMarksTable() {

        WebElement table = findMarksTable();

        if (table == null) {

            throw new AssertionError(
                    "Không tìm thấy bảng điểm."
                            + " URL=" + getCurrentUrl()
            );
        }

        return table;
    }

    // =========================================
    // 9. LẤY CÁC HÀNG TRỰC TIẾP CỦA BẢNG
    // =========================================

    private List<WebElement> tableRows(WebElement table) {

        List<WebElement> rows =
                table.findElements(HTML_ROWS);

        if (rows.isEmpty()) {

            rows = table.findElements(
                    By.cssSelector("[role='row']")
            );
        }

        return rows;
    }

    private List<WebElement> rowCells(WebElement row) {

        List<WebElement> cells =
                row.findElements(HTML_CELLS);

        if (cells.isEmpty()) {

            cells = row.findElements(
                    By.xpath(
                            "./*[@role='cell' or @role='gridcell']"
                    )
            );
        }

        return cells;
    }

    private boolean isCourseRow(List<WebElement> cells) {

        return cells.size() == 9
                && clean(cells.get(0).getText())
                .matches("\\d+");
    }

    // =========================================
    // 10. NHẬN DIỆN HÀNG PHỤ RỖNG
    // =========================================

    private boolean isEmptyDetailRow(
            List<WebElement> cells,
            String rowText
    ) {

        if (cells.size() != 1 || !rowText.isBlank()) {
            return false;
        }

        WebElement cell = cells.get(0);

        String colspan = cell.getAttribute("colspan");

        // Hàng chi tiết của MUI thường có một ô
        // trải rộng nhiều cột và rỗng khi đóng.
        if (colspan == null || colspan.isBlank()) {
            return false;
        }

        try {

            return Integer.parseInt(colspan) > 1;

        } catch (NumberFormatException ex) {

            return false;
        }
    }

    private boolean isGroupOrSummaryRow(String text) {

        return text.contains("Năm học:")
                || text.contains("Tổng số tín chỉ")
                || text.contains("Số tín chỉ tích lũy");
    }

    // =========================================
    // 11. CHỜ BẢNG ĐIỂM ỔN ĐỊNH
    // =========================================

    private void waitForTableLoaded() {

        String semesterDigits =
                selectedSemester.replaceAll("\\D+", "");

        String semesterCode = String.format(
                "HK%02d",
                Integer.parseInt(semesterDigits)
        );

        String[] previous = {null};
        int[] stable = {0};

        try {

            waitFor(20).until(driver -> {

                try {

                    if (!selectedYear.equals(
                            selectedValue("Năm học"))
                            || !selectedSemester.equals(
                            selectedValue("Học kỳ"))) {

                        stable[0] = 0;
                        return false;
                    }

                    WebElement table = findMarksTable();

                    if (table == null) {
                        stable[0] = 0;
                        return false;
                    }

                    String text = clean(table.getText());

                    if (!text.contains(selectedYear)
                            || !text.contains(semesterCode)) {

                        stable[0] = 0;
                        return false;
                    }

                    int courseCount = 0;

                    for (WebElement row : tableRows(table)) {

                        if (isCourseRow(rowCells(row))) {
                            courseCount++;
                        }
                    }

                    String snapshot =
                            courseCount + "|" + text;

                    stable[0] =
                            courseCount > 0
                                    && snapshot.equals(previous[0])
                                    ? stable[0] + 1
                                    : 0;

                    previous[0] = snapshot;

                    return courseCount > 0
                            && stable[0] >= 2;

                } catch (StaleElementReferenceException ex) {

                    stable[0] = 0;
                    return false;
                }
            });

        } catch (TimeoutException ex) {

            throw new AssertionError(
                    "Bảng điểm chưa sẵn sàng."
                            + " Year=" + selectedValue("Năm học")
                            + ", Term=" + selectedValue("Học kỳ")
                            + ", FoundTable="
                            + (findMarksTable() != null),
                    ex
            );
        }
    }

    // =========================================
    // 12. ĐỌC TIÊU ĐỀ BẢNG
    // =========================================

    public List<String> headers() {

        WebElement table = requireMarksTable();

        List<WebElement> elements =
                table.findElements(
                        By.cssSelector(
                                "thead th, [role='columnheader']"
                        )
                );

        if (elements.isEmpty()) {

            elements = table.findElements(
                    By.cssSelector("thead td")
            );
        }

        List<String> result = new ArrayList<>();

        for (WebElement element : elements) {

            result.add(clean(element.getText()));
        }

        if (result.isEmpty()) {

            throw new AssertionError(
                    "Không đọc được tiêu đề bảng điểm."
            );
        }

        return List.copyOf(result);
    }

    // =========================================
    // 13. ĐỌC DANH SÁCH HỌC PHẦN
    // =========================================

    public List<MarkRecord> courseRecords() {

        waitForTableLoaded();

        WebElement table = requireMarksTable();

        List<MarkRecord> result = new ArrayList<>();

        for (WebElement row : tableRows(table)) {

            List<WebElement> cells = rowCells(row);

            if (cells.isEmpty()) {
                continue;
            }

            String rowText = clean(row.getText());

            if (isGroupOrSummaryRow(rowText)) {
                continue;
            }

            // Chỉ bỏ qua hàng phụ rỗng có colspan.
            // Không bỏ qua hàng học phần có dữ liệu.
            if (isEmptyDetailRow(cells, rowText)) {

                System.out.println(
                        "[TC_33] Bỏ qua hàng chi tiết rỗng."
                );

                continue;
            }

            if (cells.size() != 9) {

                throw new AssertionError(
                        "Hàng bảng điểm có "
                                + cells.size()
                                + " ô thay vì 9."
                                + " Nội dung=[" + rowText + "]"
                                + " HTML=[" + row.getAttribute(
                                "outerHTML") + "]"
                );
            }

            String stt = clean(cells.get(0).getText());

            if (!stt.matches("\\d+")) {

                throw new AssertionError(
                        "STT học phần không hợp lệ: " + stt
                );
            }

            result.add(new MarkRecord(
                    clean(cells.get(1).getText()),
                    clean(cells.get(2).getText()),
                    clean(cells.get(3).getText()),
                    clean(cells.get(4).getText()),
                    clean(cells.get(5).getText()),
                    clean(cells.get(6).getText()),
                    readResultIcon(cells.get(7))
            ));
        }

        System.out.println(
                "[TC_33] Số học phần thu thập Actual: "
                        + result.size()
        );

        if (result.isEmpty()) {
            throw new AssertionError(
                    "Không có học phần để đối chiếu."
            );
        }

        return List.copyOf(result);
    }

    // =========================================
    // 14. NHẬN DIỆN BIỂU TƯỢNG KẾT QUẢ
    // =========================================

    private String readResultIcon(WebElement cell) {

        for (WebElement icon :
                cell.findElements(
                        By.cssSelector(
                                "svg, i, [role='img']"
                        )
                )) {

            if (!icon.isDisplayed()) {
                continue;
            }

            if (isGreen(icon.getCssValue("color"))
                    || isGreen(icon.getCssValue("fill"))
                    || isGreen(icon.getCssValue("stroke"))) {

                return "dau_tich_xanh";
            }
        }

        throw new AssertionError(
                "Không nhận diện được dấu tích xanh"
                        + " tại cột Kết quả."
                        + " HTML=[" + cell.getAttribute(
                        "outerHTML") + "]"
        );
    }

    private boolean isGreen(String cssColor) {

        if (cssColor == null) {
            return false;
        }

        Matcher matcher = RGB.matcher(cssColor);

        if (!matcher.find()) {
            return false;
        }

        int red = Integer.parseInt(matcher.group(1));
        int green = Integer.parseInt(matcher.group(2));
        int blue = Integer.parseInt(matcher.group(3));

        return green >= 90
                && green > red + 30
                && green > blue + 30;
    }

    // =========================================
    // 15. ĐỌC CHỈ TIÊU TỔNG HỢP HỌC KỲ
    // =========================================

    public Map<String, String> semesterSummary() {

        waitForTableLoaded();

        String text = clean(
                requireMarksTable().getText()
        );

        Map<String, String> summary =
                new LinkedHashMap<>();

        summary.put(
                "tongSoTinChi",
                extractNumber(text, "Tổng số tín chỉ")
        );

        summary.put(
                "soTinChiDat",
                extractNumber(text, "Số tín chỉ đạt")
        );

        summary.put(
                "soTinChiKhongDat",
                extractNumber(text, "Số tín chỉ không đạt")
        );

        summary.put(
                "diemTrungBinhHocKyHe10",
                extractNumber(
                        text,
                        "Điểm trung bình học kỳ (Hệ 10)"
                )
        );

        summary.put(
                "diemTrungBinhHocKyHe4",
                extractNumber(
                        text,
                        "Điểm trung bình học kỳ (Hệ 4)"
                )
        );

        return Map.copyOf(summary);
    }

    private String extractNumber(
            String source,
            String label
    ) {

        Matcher matcher = Pattern.compile(
                Pattern.quote(label)
                        + "\\s*:\\s*([0-9]+(?:[.,][0-9]+)?)",
                Pattern.CASE_INSENSITIVE
                        | Pattern.UNICODE_CASE
        ).matcher(source);

        if (!matcher.find()) {

            throw new AssertionError(
                    "Không đọc được chỉ tiêu: " + label
            );
        }

        return matcher.group(1);
    }

    // =========================================
    // 16. CHUẨN HÓA CHUỖI
    // =========================================

    private String clean(String value) {

        if (value == null) {
            return "";
        }

        return Normalizer.normalize(
                value.replace('\u00A0', ' ')
                        .replace('\u202F', ' ')
                        .strip()
                        .replaceAll("\\s+", " "),
                Normalizer.Form.NFC
        );
    }
}
