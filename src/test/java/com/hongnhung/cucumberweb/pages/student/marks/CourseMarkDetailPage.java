
package com.hongnhung.cucumberweb.pages.student.marks;

import com.hongnhung.cucumberweb.config.ConfigManager;
import com.hongnhung.cucumberweb.driver.DriverManager;
import com.hongnhung.cucumberweb.pages.BasePage;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public final class CourseMarkDetailPage extends BasePage {

    public static final String COURSE_CODE = "1050074";
    public static final String COURSE_NAME = "Toán logic";

    private static final By MAIN_TABLE =
            By.cssSelector("table[aria-label='a dense table']");

    private static final By COURSE_ROW = By.xpath(
            ".//tbody/tr[count(td)=9 "
                    + "and td[2][normalize-space(.)='1050074']]"
    );

    private static final By SEMESTER_HEADING = By.xpath(
            "./preceding-sibling::tr"
                    + "[td[contains(normalize-space(.), 'Năm học:')]][1]"
    );

    private static final By EXPAND_BUTTON =
            By.cssSelector("button[aria-label='expand row']");

    private static final By DETAIL_ROW = By.xpath(
            "./following-sibling::tr[1]"
    );

    private static final By DETAIL_TABLE =
            By.cssSelector("table[aria-label='purchases']");

    private static final By DETAIL_HEADING =
            By.xpath(
                    ".//strong[contains(normalize-space(.),"
                            + "'Chi tiết học phần:')]"
            );

    public record Summary(
            String code,
            String name,
            String credits,
            String grade10,
            String grade4,
            String letter,
            String result
    ) {
    }

    public record Detail(
            String stt,
            String tenThanhPhan,
            String diemLan1,
            String diemLan2
    ) {
    }

    public CourseMarkDetailPage(DriverManager driverManager) {
        super(driverManager);
    }

    public void open() {

        String baseUrl = ConfigManager.get("base.url")
                .replaceAll("/+$", "");

        driver().navigate().to(
                baseUrl + "/student/marks"
        );

        waitUntilVisible(MAIN_TABLE);

        waitDriver().until(d -> {
            try {
                return findCourseRow().isDisplayed();
            } catch (org.openqa.selenium.NoSuchElementException
                     | StaleElementReferenceException ex) {
                return false;
            }
        });
    }

    private WebElement findCourseRow() {
        return driver()
                .findElement(MAIN_TABLE)
                .findElement(COURSE_ROW);
    }

    private WebElement findDetailRow() {
        return findCourseRow().findElement(DETAIL_ROW);
    }

    public String semesterHeading() {

        return clean(
                findCourseRow()
                        .findElement(SEMESTER_HEADING)
                        .getText()
        );
    }

    public Summary summary() {

        WebElement row = findCourseRow();

        List<WebElement> cells =
                row.findElements(By.xpath("./td"));

        if (cells.size() != 9) {
            throw new IllegalStateException(
                    "TC_35: Dong hoc phan phai co 9 o."
            );
        }

        String result = "CHUA_CO";

        List<WebElement> icons =
                cells.get(7).findElements(By.cssSelector("svg"));

        if (icons.size() == 1) {

            String style = icons.get(0).getAttribute("style");

            if (style != null && style.contains("green")) {
                result = "DAT";
            } else if (style != null
                    && style.contains("red")) {
                result = "KHONG_DAT";
            } else {
                throw new IllegalStateException(
                        "TC_35: Mau icon ket qua khong xac dinh."
                );
            }

        } else if (!icons.isEmpty()) {
            throw new IllegalStateException(
                    "TC_35: So icon ket qua khong hop le."
            );
        }

        return new Summary(
                clean(cells.get(1).getText()),
                clean(cells.get(2).getText()),
                clean(cells.get(3).getText()),
                clean(cells.get(4).getText()),
                clean(cells.get(5).getText()),
                clean(cells.get(6).getText()),
                result
        );
    }

    public void expand() {

        WebElement row = findCourseRow();

        WebElement button =
                row.findElement(EXPAND_BUTTON);

        button.click();

        waitDriver().until(d -> {
            try {
                WebElement detailRow = findDetailRow();

                List<WebElement> tables =
                        detailRow.findElements(DETAIL_TABLE);

                return tables.size() == 1
                        && tables.get(0).isDisplayed()
                        && tables.get(0)
                        .findElements(
                                By.cssSelector("tbody > tr")
                        ).size() == 2;

            } catch (StaleElementReferenceException ex) {
                return false;
            }
        });
    }

    public String detailHeading() {

        return clean(
                findDetailRow()
                        .findElement(DETAIL_HEADING)
                        .getText()
        );
    }

    public List<String> detailHeaders() {

        WebElement table =
                findDetailRow().findElement(DETAIL_TABLE);

        List<String> headers = new ArrayList<>();

        for (WebElement th : table.findElements(
                By.cssSelector("thead th"))) {
            headers.add(clean(th.getText()));
        }

        return List.copyOf(headers);
    }

    public List<Detail> details() {

        WebElement table =
                findDetailRow().findElement(DETAIL_TABLE);

        List<Detail> result = new ArrayList<>();

        for (WebElement row : table.findElements(
                By.cssSelector("tbody > tr"))) {

            List<WebElement> cells =
                    row.findElements(By.xpath("./td"));

            if (cells.size() != 4) {
                throw new IllegalStateException(
                        "TC_35: Dong chi tiet khong co 4 o."
                );
            }

            result.add(new Detail(
                    clean(cells.get(0).getText()),
                    clean(cells.get(1).getText()),
                    clean(cells.get(2).getText()),
                    clean(cells.get(3).getText())
            ));
        }

        return List.copyOf(result);
    }

    public void collapse() {

        findCourseRow()
                .findElement(EXPAND_BUTTON)
                .click();

        waitDriver().until(d -> {
            try {
                List<WebElement> tables =
                        findDetailRow().findElements(DETAIL_TABLE);

                return tables.isEmpty()
                        || tables.stream()
                        .noneMatch(WebElement::isDisplayed);

            } catch (StaleElementReferenceException ex) {
                return false;
            }
        });
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
