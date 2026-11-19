
package com.hongnhung.cucumberweb.pages.student.marks;

import com.hongnhung.cucumberweb.config.ConfigManager;
import com.hongnhung.cucumberweb.driver.DriverManager;
import com.hongnhung.cucumberweb.models.marks.ProgramMarkRecord;
import com.hongnhung.cucumberweb.pages.BasePage;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public final class ProgramMarksPage extends BasePage {

    private static final By PROGRAM_SELECT =
            By.id("demo-simple-select-helper");

    private static final By PROGRAM_CODE =
            By.cssSelector("input[name='StudyProgramID']");

    private static final By PROGRAM_RADIO =
            By.cssSelector(
                    "input[type='radio'][value='CTDT']"
            );

    // Nhấn nhãn thay vì nhấn/chờ input radio ẩn của MUI.
    private static final By PROGRAM_MODE_LABEL =
            By.xpath(
                    "//label[.//input[@type='radio' "
                            + "and @value='CTDT']]"
            );

    private static final By MARKS_TABLE =
            By.xpath(
                    "//table[.//thead//th"
                            + "[normalize-space(.)='MÃ HP']]"
            );

    private static final By BODY_ROWS =
            By.cssSelector("tbody > tr");

    private static final By CELLS =
            By.cssSelector("td");

    public record Snapshot(
            List<ProgramMarkRecord> records,
            List<String> semesterHeadings,
            List<String> groupHeadings,
            int ignoredDetailRows
    ) {
    }

    public ProgramMarksPage(DriverManager driverManager) {
        super(driverManager);
    }

    public void open() {

        String baseUrl = ConfigManager.get("base.url")
                .replaceAll("/+$", "");

        driver().navigate().to(
                baseUrl + "/student/marks"
        );

        // Chờ combobox và nhãn radio hiển thị.
        waitUntilVisible(PROGRAM_SELECT);
        waitUntilVisible(PROGRAM_MODE_LABEL);

        // Chọn đúng chương trình đào tạo.
        if (!"Kỹ thuật phần mềm".equals(selectedProgram())) {

            click(PROGRAM_SELECT);

            By option = By.xpath(
                    "//*[@role='option' and "
                            + "normalize-space(.)='Kỹ thuật phần mềm']"
            );

            waitUntilVisible(option);
            click(option);
        }

        waitDriver().until(d -> {
            try {
                return "119DHCQK45".equals(
                        d.findElement(PROGRAM_CODE)
                                .getDomProperty("value")
                );
            } catch (StaleElementReferenceException ex) {
                return false;
            }
        });

        // Input radio có thể bị ẩn bởi MUI.
        // Chỉ dùng isSelected() để đọc trạng thái.
        if (!programModeSelected()) {
            click(PROGRAM_MODE_LABEL);
        }

        // Xác nhận React đã chuyển sang chế độ CTDT.
        waitDriver().until(d -> {
            try {
                return d.findElement(PROGRAM_RADIO)
                        .isSelected();
            } catch (StaleElementReferenceException ex) {
                return false;
            }
        });

        // Chờ bảng CTDT tải đủ 82 học phần.
        waitDriver().until(d -> {
            try {
                WebElement table =
                        d.findElement(MARKS_TABLE);

                return programModeSelected()
                        && table.isDisplayed()
                        && countCourseRows(table) == 82;

            } catch (StaleElementReferenceException ex) {
                return false;
            }
        });

        System.out.println(
                "[TC_34] Da mo bang diem CTDT: 82 hoc phan."
        );
    }

    private int countCourseRows(WebElement table) {

        int count = 0;

        for (WebElement row :
                table.findElements(BODY_ROWS)) {

            List<WebElement> cells =
                    row.findElements(CELLS);

            if (cells.size() == 9
                    && cells.get(0).getText()
                    .strip().matches("\\d+")) {
                count++;
            }
        }

        return count;
    }

    public String selectedProgram() {

        return clean(
                waitUntilVisible(PROGRAM_SELECT).getText()
        );
    }

    public String selectedProgramCode() {

        return driver().findElement(PROGRAM_CODE)
                .getDomProperty("value");
    }

    public boolean programModeSelected() {

        return driver().findElement(PROGRAM_RADIO)
                .isSelected();
    }

    public Snapshot snapshot() {

        return waitDriver().until(d -> {

            try {
                WebElement table = d.findElement(MARKS_TABLE);

                List<ProgramMarkRecord> records =
                        new ArrayList<>();

                List<String> semesters =
                        new ArrayList<>();

                List<String> groups =
                        new ArrayList<>();

                String semester = "";
                String category = "";
                String group = "";

                int ignoredDetails = 0;

                for (WebElement row :
                        table.findElements(BODY_ROWS)) {

                    List<WebElement> cells =
                            row.findElements(CELLS);

                    if (cells.size() == 1) {

                        WebElement cell = cells.get(0);
                        String text = clean(cell.getText());

                        if (text.startsWith("Học kỳ ")) {

                            semester = text;
                            category = "";
                            group = "";

                            semesters.add(text);

                        } else if (
                                text.startsWith("Bắt buộc")
                                        || text.startsWith("Tự chọn")
                        ) {

                            category = text;
                            group = "";

                            groups.add(
                                    semester + " | " + category
                            );

                        } else if (text.startsWith("Nhóm ")) {

                            group = text;

                            groups.add(
                                    semester + " | "
                                            + category + " | " + group
                            );

                        } else if (text.isBlank()
                                && "10".equals(
                                cell.getAttribute("colspan")
                        )) {

                            ignoredDetails++;

                        } else {

                            throw new IllegalStateException(
                                    "TC_34: Dong nhom khong xac dinh: "
                                            + text
                            );
                        }

                        continue;
                    }

                    if (cells.size() != 9) {

                        throw new IllegalStateException(
                                "TC_34: So o khong hop le: "
                                        + cells.size()
                        );
                    }

                    String ordinal =
                            clean(cells.get(0).getText());

                    if (!ordinal.matches("\\d+")) {

                        throw new IllegalStateException(
                                "TC_34: TT khong hop le: "
                                        + ordinal
                        );
                    }

                    if (semester.isBlank()
                            || category.isBlank()) {

                        throw new IllegalStateException(
                                "TC_34: Hoc phan thieu "
                                        + "hoc ky hoac loai nhom."
                        );
                    }

                    records.add(new ProgramMarkRecord(
                            semester,
                            category,
                            group,
                            ordinal,
                            clean(cells.get(1).getText()),
                            clean(cells.get(2).getText()),
                            clean(cells.get(3).getText()),
                            clean(cells.get(4).getText()),
                            clean(cells.get(5).getText()),
                            clean(cells.get(6).getText()),
                            readResult(cells.get(7))
                    ));

                    // Kiểm tra nút xem chi tiết học phần.
                    if (cells.get(8).findElements(
                            By.cssSelector(
                                    "button[aria-label='expand row']"
                            )
                    ).size() != 1) {

                        throw new IllegalStateException(
                                "TC_34: Thieu nut chi tiet "
                                        + "tai TT " + ordinal
                        );
                    }
                }

                if (records.size() != 82
                        || semesters.size() != 9) {
                    return null;
                }

                return new Snapshot(
                        List.copyOf(records),
                        List.copyOf(semesters),
                        List.copyOf(groups),
                        ignoredDetails
                );

            } catch (StaleElementReferenceException ex) {
                return null;
            }
        });
    }

    private String readResult(WebElement resultCell) {

        List<WebElement> icons =
                resultCell.findElements(
                        By.cssSelector("svg")
                );

        if (icons.isEmpty()) {

            if (!clean(resultCell.getText()).isBlank()) {

                throw new IllegalStateException(
                        "TC_34: O ket qua co van ban "
                                + "nhung khong co icon."
                );
            }

            return "CHUA_CO";
        }

        if (icons.size() != 1) {

            throw new IllegalStateException(
                    "TC_34: So icon ket qua khong hop le."
            );
        }

        String style =
                icons.get(0).getAttribute("style");

        if (style != null && style.contains("green")) {
            return "DAT";
        }

        if (style != null && style.contains("red")) {
            return "KHONG_DAT";
        }

        throw new IllegalStateException(
                "TC_34: Mau icon khong xac dinh: "
                        + style
        );
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
