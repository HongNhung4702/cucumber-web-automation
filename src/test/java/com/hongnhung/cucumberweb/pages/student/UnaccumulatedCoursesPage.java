
package com.hongnhung.cucumberweb.pages.student;

import com.hongnhung.cucumberweb.driver.DriverManager;
import com.hongnhung.cucumberweb.models.unaccumulated.UnaccumulatedCourseRecord;
import com.hongnhung.cucumberweb.pages.BasePage;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public final class UnaccumulatedCoursesPage extends BasePage {

    private static final String URL =
            "https://daotao.qnu.edu.vn/student/overview";

    private static final By PROGRAM =
            By.id("study-program-select");

    private static final By PROGRAM_CODE =
            By.cssSelector("input[name='StudyProgramID']");

    private static final By OPEN_BUTTON =
            By.xpath(
                    "//button[contains(normalize-space(.),"
                            + "'Học phần chưa tích lũy')]"
            );

    private static final By DIALOG =
            By.cssSelector("[role='dialog']");

    private static final By TABLE =
            By.cssSelector("table");

    private static final By OPTIONS =
            By.cssSelector("[role='option']");

    private static final String EXPECTED_PROGRAM_CODE =
            "119DHCQK45";

    private static final List<String> TOP_HEADERS = List.of(
            "TT",
            "MÃ HỌC PHẦN",
            "TÊN HỌC PHẦN",
            "",
            "SỐ TC",
            "SỐ TIẾT",
            "HỌC PHẦN TƯƠNG ĐƯƠNG",
            "HỌC PHẦN HỌC TRƯỚC",
            "HỌC PHẦN THAY THẾ"
    );

    private static final List<String> SUB_HEADERS =
            List.of("LT", "TH", "TL");

    public UnaccumulatedCoursesPage(
            DriverManager driverManager
    ) {
        super(driverManager);
    }

    private WebDriverWait waitFor() {

        WebDriverWait wait = new WebDriverWait(
                driver(),
                Duration.ofSeconds(20)
        );

        wait.pollingEvery(Duration.ofMillis(300));
        wait.ignoring(StaleElementReferenceException.class);

        return wait;
    }

    // =========================================
    // 1. MỞ TRANG
    // =========================================

    public void open() {

        driver().get(URL);

        waitFor().until(d ->
                d.findElements(PROGRAM).size() == 1
                        && d.findElements(PROGRAM_CODE).size() == 1
        );

        System.out.println(
                "[TC_17] Đã mở trang Tổng quan học tập."
        );
    }

    // =========================================
    // 2. CHỌN CHƯƠNG TRÌNH ĐÀO TẠO
    // =========================================

    public void selectProgram(String expectedProgram) {

        if (!expectedProgram.equals(selectedProgram())) {

            WebElement dropdown = waitFor().until(d -> {

                for (WebElement element :
                        d.findElements(PROGRAM)) {

                    if (element.isDisplayed()
                            && element.isEnabled()) {
                        return element;
                    }
                }

                return null;
            });

            scrollTo(dropdown);
            dropdown.click();

            WebElement option = waitFor().until(d -> {

                for (WebElement element :
                        d.findElements(OPTIONS)) {

                    try {

                        if (element.isDisplayed()
                                && expectedProgram.equals(
                                clean(element.getText())
                        )) {
                            return element;
                        }

                    } catch (StaleElementReferenceException ignored) {
                        // MUI có thể render lại danh sách.
                    }
                }

                return null;
            });

            option.click();
        }

        waitFor().until(d ->
                expectedProgram.equals(selectedProgram())
                        && EXPECTED_PROGRAM_CODE.equals(
                        selectedProgramCode()
                )
        );

        System.out.println(
                "[TC_17] Chương trình: "
                        + selectedProgram()
                        + " | Mã: "
                        + selectedProgramCode()
        );
    }

    public String selectedProgram() {

        try {

            List<WebElement> elements =
                    driver().findElements(PROGRAM);

            if (elements.isEmpty()) {
                return "";
            }

            return clean(elements.get(0).getText());

        } catch (StaleElementReferenceException ex) {
            return "";
        }
    }

    public String selectedProgramCode() {

        try {

            List<WebElement> elements =
                    driver().findElements(PROGRAM_CODE);

            if (elements.isEmpty()) {
                return "";
            }

            return clean(
                    elements.get(0).getAttribute("value")
            );

        } catch (StaleElementReferenceException ex) {
            return "";
        }
    }

    // =========================================
    // 3. THÔNG TIN SINH VIÊN
    // =========================================

    public String studentInformation() {

        By infoSection = By.xpath(
                "//*[normalize-space(text())='Thông tin cơ bản']"
                        + "/ancestor::*[contains(.,'Mã sinh viên')"
                        + " and contains(.,'Khóa học')][1]"
        );

        WebElement section = waitFor().until(d -> {

            for (WebElement element :
                    d.findElements(infoSection)) {

                if (element.isDisplayed()) {
                    return element;
                }
            }

            return null;
        });

        return clean(section.getText());
    }

    // =========================================
    // 4. MỞ HỘP THOẠI
    // =========================================

    public void openDialog() {

        WebElement button = waitFor().until(d -> {

            for (WebElement element :
                    d.findElements(OPEN_BUTTON)) {

                if (element.isDisplayed()
                        && element.isEnabled()) {
                    return element;
                }
            }

            return null;
        });

        scrollTo(button);
        button.click();

        waitFor().until(d ->
                d.findElements(DIALOG)
                        .stream()
                        .anyMatch(WebElement::isDisplayed)
        );

        waitForTable();

        System.out.println(
                "[TC_17] Đã mở hộp thoại "
                        + "Học phần chưa tích lũy."
        );
    }

    private WebElement dialog() {

        return waitFor().until(d -> {

            for (WebElement element :
                    d.findElements(DIALOG)) {

                if (element.isDisplayed()) {
                    return element;
                }
            }

            return null;
        });
    }

    public String dialogTitle() {

        WebElement currentDialog = dialog();

        List<WebElement> titles =
                currentDialog.findElements(
                        By.xpath(
                                ".//*[normalize-space(.)="
                                        + "'HỌC PHẦN CHƯA TÍCH LŨY']"
                        )
                );

        if (titles.isEmpty()) {
            return "";
        }

        return clean(titles.get(0).getText());
    }

    // =========================================
    // 5. BẢNG HỌC PHẦN
    // =========================================

    private WebElement resultTable() {

        WebElement currentDialog = dialog();

        for (WebElement table :
                currentDialog.findElements(TABLE)) {

            if (!table.findElements(
                    By.cssSelector("thead tr")
            ).isEmpty()) {
                return table;
            }
        }

        throw new IllegalStateException(
                "TC_17: Không tìm thấy bảng học phần."
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

                return rows.stream().anyMatch(row ->
                        row.findElements(
                                By.cssSelector("td")
                        ).size() == 11
                );

            } catch (IllegalStateException
                     | StaleElementReferenceException ex) {
                return false;
            }
        });
    }

    // =========================================
    // 6. KIỂM TRA HEADER
    // =========================================

    public List<String> expectedTopHeaders() {
        return TOP_HEADERS;
    }

    public List<String> expectedSubHeaders() {
        return SUB_HEADERS;
    }

    public List<String> topHeaders() {

        List<WebElement> headerRows =
                resultTable().findElements(
                        By.cssSelector("thead tr")
                );

        if (headerRows.size() != 2) {
            throw new IllegalStateException(
                    "TC_17: Bảng phải có 2 hàng tiêu đề."
            );
        }

        return headerRows.get(0)
                .findElements(By.cssSelector("th"))
                .stream()
                .map(e -> clean(e.getText()))
                .toList();
    }

    public List<String> subHeaders() {

        List<WebElement> headerRows =
                resultTable().findElements(
                        By.cssSelector("thead tr")
                );

        if (headerRows.size() != 2) {
            throw new IllegalStateException(
                    "TC_17: Bảng phải có 2 hàng tiêu đề."
            );
        }

        return headerRows.get(1)
                .findElements(By.cssSelector("th"))
                .stream()
                .map(e -> clean(e.getText()))
                .toList();
    }

    public boolean headerStructureMatches() {

        List<WebElement> rows =
                resultTable().findElements(
                        By.cssSelector("thead tr")
                );

        if (rows.size() != 2) {
            return false;
        }

        List<WebElement> first =
                rows.get(0).findElements(
                        By.cssSelector("th")
                );

        List<WebElement> second =
                rows.get(1).findElements(
                        By.cssSelector("th")
                );

        if (first.size() != 9 || second.size() != 3) {
            return false;
        }

        for (int i = 0; i < first.size(); i++) {

            WebElement th = first.get(i);

            int expectedColspan = i == 5 ? 3 : 1;
            int expectedRowspan = i == 5 ? 1 : 2;

            int actualColspan = Integer.parseInt(
                    th.getDomProperty("colSpan")
            );

            int actualRowspan = Integer.parseInt(
                    th.getDomProperty("rowSpan")
            );

            if (actualColspan != expectedColspan
                    || actualRowspan != expectedRowspan) {
                return false;
            }
        }

        return true;
    }

    // =========================================
    // 7. ĐỌC 9 TRƯỜNG CỦA MỖI HỌC PHẦN
    // =========================================

    public List<UnaccumulatedCourseRecord> readRecords() {

        waitForTable();

        List<WebElement> rows =
                resultTable().findElements(
                        By.cssSelector("tbody tr")
                );

        List<UnaccumulatedCourseRecord> result =
                new ArrayList<>();

        int groupRows = 0;

        for (WebElement row : rows) {

            List<WebElement> cells =
                    row.findElements(
                            By.cssSelector("td")
                    );

            if (cells.size() == 1
                    && "Bắt buộc".equals(
                    clean(cells.get(0).getText())
            )) {

                groupRows++;
                continue;
            }

            if (cells.size() != 11) {

                throw new IllegalStateException(
                        "TC_17: Sai cấu trúc dòng, số ô="
                                + cells.size()
                );
            }

            // 0 = TT
            // 1 = Mã học phần
            // 2 = Tên học phần
            // 3 = Liên kết Xem CTĐT có học
            // 4 = Số tín chỉ
            // 5,6,7 = LT,TH,TL
            // 8,9,10 = Tương đương, Học trước, Thay thế

            result.add(new UnaccumulatedCourseRecord(
                    cell(cells, 1),
                    cell(cells, 2),
                    cell(cells, 4),
                    cell(cells, 5),
                    cell(cells, 6),
                    cell(cells, 7),
                    cell(cells, 8),
                    cell(cells, 9),
                    cell(cells, 10)
            ));
        }

        System.out.println(
                "[TC_17] Hàng phân nhóm: "
                        + groupRows
                        + " | Actual: "
                        + result.size() + " học phần."
        );

        return List.copyOf(result);
    }

    private String cell(
            List<WebElement> cells,
            int index
    ) {
        return clean(cells.get(index).getText());
    }

    private void scrollTo(WebElement element) {

        ((JavascriptExecutor) driver())
                .executeScript(
                        "arguments[0].scrollIntoView("
                                + "{block:'center'});",
                        element
                );
    }

    private String clean(String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace('\u00A0', ' ')
                .replace('\u202F', ' ')
                .strip()
                .replaceAll("\\s+", " ");
    }
}
