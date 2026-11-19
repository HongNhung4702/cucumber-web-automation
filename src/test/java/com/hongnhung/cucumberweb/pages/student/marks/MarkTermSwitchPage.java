
package com.hongnhung.cucumberweb.pages.student.marks;

import com.hongnhung.cucumberweb.config.ConfigManager;
import com.hongnhung.cucumberweb.driver.DriverManager;
import com.hongnhung.cucumberweb.pages.BasePage;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.text.Normalizer;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public final class MarkTermSwitchPage extends BasePage {

    private static final By PROGRAM =
            By.id("demo-simple-select-helper");

    private static final By PROGRAM_CODE =
            By.cssSelector("input[name='StudyProgramID']");

    private static final By YEAR =
            By.id("mui-component-select-YearStudy");

    private static final By YEAR_CODE =
            By.cssSelector("input[name='YearStudy']");

    private static final By TERM =
            By.id("mui-component-select-TermID");

    private static final By TERM_CODE =
            By.cssSelector("input[name='TermID']");

    private static final By STUDENT_RADIO =
            By.cssSelector("input[type='radio'][value='SV']");

    private static final By STUDENT_LABEL =
            By.xpath(
                    "//label[.//input[@type='radio' "
                            + "and @value='SV']]"
            );

    private static final By MARK_TABLE =
            By.cssSelector("table[aria-label='a dense table']");

    private static final By MUI_BACKDROP =
            By.cssSelector(".MuiModal-backdrop");

    private static final By MUI_LISTBOX =
            By.cssSelector("[role='listbox']");

    public record MarkRow(
            String tt,
            String ma,
            String ten,
            String tc,
            String d10,
            String d4,
            String chu,
            String ketQua
    ) {
    }

    public record Snapshot(
            String heading,
            List<MarkRow> rows,
            List<String> summaryLeft,
            List<String> summaryRight
    ) {
    }

    public MarkTermSwitchPage(DriverManager manager) {
        super(manager);
    }

    public void open() {

        String baseUrl = ConfigManager.get("base.url")
                .replaceAll("/+$", "");

        driver().navigate().to(
                baseUrl + "/student/marks"
        );

        waitUntilVisible(PROGRAM);
        waitUntilVisible(STUDENT_LABEL);

        if (!"Kỹ thuật phần mềm".equals(
                clean(driver().findElement(PROGRAM).getText()))) {

            choose(PROGRAM, "Kỹ thuật phần mềm");
        }

        waitDriver().until(d -> {
            try {
                return "119DHCQK45".equals(
                        d.findElement(PROGRAM_CODE)
                                .getDomProperty("value")
                );
            } catch (StaleElementReferenceException
                     | NoSuchElementException ex) {
                return false;
            }
        });

        if (!driver().findElement(STUDENT_RADIO).isSelected()) {
            click(STUDENT_LABEL);
        }

        waitDriver().until(d ->
                d.findElement(STUDENT_RADIO).isSelected()
        );
    }

    public void select(String year, String term) {

        for (int attempt = 1; attempt <= 3; attempt++) {

            System.out.println(
                    "[TC_36] Đồng bộ bộ lọc lần "
                            + attempt + "/3"
            );

            try {
                choose(YEAR, year);

                waitForYear(year);

                choose(TERM, term);

                waitForFilters(year, term);

                System.out.println(
                        "[TC_36] Bộ lọc: "
                                + year + " | " + term
                                + " MATCH."
                );

                return;

            } catch (TimeoutException
                     | StaleElementReferenceException
                     | ElementClickInterceptedException ex) {

                System.out.println(
                        "[TC_36] Chưa đồng bộ: "
                                + ex.getClass().getSimpleName()
                );

                if (attempt == 3) {
                    throw new IllegalStateException(
                            "TC_36: Không thể chọn bộ lọc "
                                    + year + " | " + term,
                            ex
                    );
                }

                closeMuiMenu();
            }
        }
    }

    private void choose(By locator, String value) {

        By option = By.xpath(
                "//*[@role='option' and normalize-space(.)='"
                        + value + "']"
        );

        for (int attempt = 1; attempt <= 3; attempt++) {

            try {
                // Quan trọng: không click khi menu MUI cũ
                // vẫn đang đóng hoặc còn Backdrop.
                waitForMuiMenuClosed();

                WebElement dropdown =
                        waitUntilVisible(locator);

                if (value.equals(clean(dropdown.getText()))) {
                    return;
                }

                ((JavascriptExecutor) driver())
                        .executeScript(
                                "arguments[0].scrollIntoView("
                                        + "{block:'center'});",
                                dropdown
                        );

                waitUntilClickable(locator).click();

                WebDriverWait optionWait =
                        new WebDriverWait(
                                driver(),
                                Duration.ofSeconds(15)
                        );

                WebElement selectedOption =
                        optionWait.until(
                                ExpectedConditions
                                        .elementToBeClickable(option)
                        );

                selectedOption.click();

                // Chờ giá trị được áp dụng.
                waitDriver().until(d -> {
                    try {
                        return value.equals(
                                clean(
                                        d.findElement(locator)
                                                .getText()
                                )
                        );
                    } catch (StaleElementReferenceException
                             | NoSuchElementException ex) {
                        return false;
                    }
                });

                // Quan trọng: chờ menu và Backdrop đóng
                // trước khi chuyển sang bộ lọc tiếp theo.
                waitForMuiMenuClosed();

                return;

            } catch (TimeoutException
                     | StaleElementReferenceException
                     | ElementClickInterceptedException ex) {

                System.out.println(
                        "[TC_36] Chọn '" + value
                                + "' lần " + attempt
                                + " chưa thành công: "
                                + ex.getClass().getSimpleName()
                );

                closeMuiMenu();

                if (attempt == 3) {
                    throw ex;
                }
            }
        }
    }

    private void waitForMuiMenuClosed() {

        WebDriverWait wait = new WebDriverWait(
                driver(),
                Duration.ofSeconds(15)
        );

        wait.until(
                ExpectedConditions.invisibilityOfElementLocated(
                        MUI_LISTBOX
                )
        );

        wait.until(
                ExpectedConditions.invisibilityOfElementLocated(
                        MUI_BACKDROP
                )
        );
    }

    private void closeMuiMenu() {

        try {
            // Gửi ESC đến phần tử đang nhận bàn phím,
            // không gửi vào combobox bị Backdrop che.
            new Actions(driver())
                    .sendKeys(Keys.ESCAPE)
                    .perform();

            waitForMuiMenuClosed();

        } catch (TimeoutException ex) {
            System.out.println(
                    "[TC_36] Menu MUI chưa đóng hoàn toàn."
            );
        } catch (RuntimeException ex) {
            System.out.println(
                    "[TC_36] Không thể đóng menu MUI: "
                            + ex.getClass().getSimpleName()
            );
        }
    }

    private void waitForYear(String year) {

        new WebDriverWait(
                driver(),
                Duration.ofSeconds(20)
        ).until(d -> {
            try {
                return year.equals(selectedYear())
                        && year.equals(
                        d.findElement(YEAR_CODE)
                                .getDomProperty("value")
                );
            } catch (StaleElementReferenceException
                     | NoSuchElementException ex) {
                return false;
            }
        });
    }

    private void waitForFilters(String year, String term) {

        new WebDriverWait(
                driver(),
                Duration.ofSeconds(20)
        ).until(d -> {
            try {
                return year.equals(selectedYear())
                        && term.equals(selectedTerm())
                        && year.equals(
                        d.findElement(YEAR_CODE)
                                .getDomProperty("value")
                )
                        && termCode(term).equals(
                        d.findElement(TERM_CODE)
                                .getDomProperty("value")
                );
            } catch (StaleElementReferenceException
                     | NoSuchElementException ex) {
                return false;
            }
        });
    }

    private String termCode(String term) {

        return switch (term) {
            case "Học kỳ 1" -> "HK01";
            case "Học kỳ 2" -> "HK02";
            default -> throw new IllegalArgumentException(
                    "TC_36: Hoc ky khong ho tro: " + term
            );
        };
    }

    public String selectedYear() {
        return clean(
                driver().findElement(YEAR).getText()
        );
    }

    public String selectedTerm() {
        return clean(
                driver().findElement(TERM).getText()
        );
    }

    public Snapshot awaitSnapshot(
            String expectedHeading,
            int expectedRows
    ) {

        final String[] previous = {null};
        final int[] stable = {0};

        return waitDriver().until(d -> {
            try {
                Snapshot snapshot = readSnapshot();

                if (!expectedHeading.equals(snapshot.heading())
                        || snapshot.rows().size() != expectedRows
                        || snapshot.summaryLeft().size() != 4
                        || snapshot.summaryRight().size() != 3) {

                    previous[0] = null;
                    stable[0] = 0;
                    return null;
                }

                String fingerprint = snapshot.toString();

                if (fingerprint.equals(previous[0])) {
                    stable[0]++;
                } else {
                    stable[0] = 0;
                }

                previous[0] = fingerprint;

                return stable[0] >= 2
                        ? snapshot
                        : null;

            } catch (StaleElementReferenceException
                     | NoSuchElementException ex) {

                previous[0] = null;
                stable[0] = 0;

                return null;
            }
        });
    }

    public Snapshot readSnapshot() {

        WebElement table =
                driver().findElement(MARK_TABLE);

        List<MarkRow> records =
                new ArrayList<>();

        List<String> left =
                new ArrayList<>();

        List<String> right =
                new ArrayList<>();

        String heading = "";

        for (WebElement tr :
                table.findElements(By.xpath("./tbody/tr"))) {

            List<WebElement> td =
                    tr.findElements(By.xpath("./td"));

            // 1. Tiêu đề học kỳ hoặc dòng chi tiết thu gọn.
            if (td.size() == 1) {

                WebElement cell = td.get(0);
                String value = clean(cell.getText());

                if (value.startsWith("Năm học:")) {

                    heading = value;

                } else if (value.isBlank()
                        && "10".equals(
                        cell.getAttribute("colspan")
                )) {

                    // Dòng chi tiết học phần đang đóng.

                } else {

                    throw new IllegalStateException(
                            "TC_36: Dong mot o khong xac dinh: "
                                    + value
                    );
                }

                continue;
            }

            // 2. Dòng tổng kết học kỳ.
            if (td.size() == 2) {

                if (!"3".equals(
                        td.get(0).getAttribute("colspan")
                ) || !"6".equals(
                        td.get(1).getAttribute("colspan")
                )) {

                    throw new IllegalStateException(
                            "TC_36: Cau truc dong tong ket "
                                    + "khong hop le."
                    );
                }

                left = paragraphs(td.get(0));
                right = paragraphs(td.get(1));

                continue;
            }

            // 3. Dòng học phần chưa có điểm, dạng rút gọn.
            if (td.size() == 5) {

                WebElement mergedCell = td.get(4);

                if (!"4".equals(
                        mergedCell.getAttribute("colspan")
                )) {

                    throw new IllegalStateException(
                            "TC_36: Dong 5 o khong co colspan=4."
                    );
                }

                if (!clean(mergedCell.getText()).isBlank()) {

                    throw new IllegalStateException(
                            "TC_36: O diem gop khong trong tai ma "
                                    + clean(td.get(1).getText())
                    );
                }

                records.add(new MarkRow(
                        clean(td.get(0).getText()),
                        clean(td.get(1).getText()),
                        clean(td.get(2).getText()),
                        clean(td.get(3).getText()),
                        "",
                        "",
                        "",
                        "CHUA_CO"
                ));

                continue;
            }

            // 4. Dòng học phần đầy đủ 9 ô.
            if (td.size() == 9) {

                String status = readStatus(td.get(7));

                records.add(new MarkRow(
                        clean(td.get(0).getText()),
                        clean(td.get(1).getText()),
                        clean(td.get(2).getText()),
                        clean(td.get(3).getText()),
                        clean(td.get(4).getText()),
                        clean(td.get(5).getText()),
                        clean(td.get(6).getText()),
                        status
                ));

                continue;
            }

            throw new IllegalStateException(
                    "TC_36: Dong bang co "
                            + td.size() + " o, chua duoc ho tro."
            );
        }

        return new Snapshot(
                heading,
                List.copyOf(records),
                List.copyOf(left),
                List.copyOf(right)
        );
    }

    private String readStatus(WebElement cell) {

        List<WebElement> icons =
                cell.findElements(By.cssSelector("svg"));

        if (icons.isEmpty()) {

            if (!clean(cell.getText()).isBlank()) {

                throw new IllegalStateException(
                        "TC_36: O ket qua co noi dung "
                                + "nhung khong co icon."
                );
            }

            return "CHUA_CO";
        }

        if (icons.size() != 1) {

            throw new IllegalStateException(
                    "TC_36: So icon ket qua khong hop le."
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
                "TC_36: Mau icon ket qua khong xac dinh: "
                        + style
        );
    }

    private List<String> paragraphs(WebElement cell) {

        List<String> result = new ArrayList<>();

        for (WebElement p :
                cell.findElements(By.xpath("./strong/p"))) {

            result.add(clean(p.getText()));
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
