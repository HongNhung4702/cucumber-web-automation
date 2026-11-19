
package com.hongnhung.cucumberweb.steps.exams;

import com.hongnhung.cucumberweb.assertions.exams.ExamScheduleComparator;
import com.hongnhung.cucumberweb.models.exams.ExamRecord;
import com.hongnhung.cucumberweb.pages.student.exams.ExamYearSwitchPage;
import com.hongnhung.cucumberweb.testdata.exams.ExamBaselineReader;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class ExamYearSwitchSteps {

    private static final String INITIAL_BASELINE =
            "baselines/exams/TC_29_initial_expected.csv";

    private static final List<String> CSV_HEADERS =
            List.of(
                    "maLopHocPhan",
                    "tenLopHocPhan",
                    "lanThi",
                    "ngayThi",
                    "gioThi",
                    "phongThi",
                    "diaDiem",
                    "hinhThucThi",
                    "kyThi",
                    "ghiChu",
                    "vangThi"
            );

    private static final List<String> UI_HEADERS =
            List.of(
                    "Mã lớp học phần/Tên lớp học phần",
                    "Lần thi",
                    "Ngày thi",
                    "Giờ thi",
                    "Phòng thi",
                    "Địa điểm",
                    "Hình thức thi",
                    "Kỳ thi",
                    "Ghi chú",
                    "Vắng thi"
            );

    private final ExamYearSwitchPage page;
    private final ExamBaselineReader tc28Reader;

    private final ExamScheduleComparator comparator =
            new ExamScheduleComparator();

    private List<ExamRecord> expectedInitial;
    private List<ExamRecord> actualInitial;

    private List<ExamRecord> expectedTarget;
    private List<ExamRecord> actualTarget;

    public ExamYearSwitchSteps(
            ExamYearSwitchPage page,
            ExamBaselineReader tc28Reader
    ) {
        this.page = page;
        this.tc28Reader = tc28Reader;
    }

    @When("sinh viên mở lịch thi năm ban đầu TC_29")
    public void openInitialYear() {

        expectedInitial = readInitialBaseline();
        expectedTarget = tc28Reader.readTc28();

        page.openInitialYear();

        actualInitial = page.records();
    }

    @Then("lịch thi năm ban đầu TC_29 khớp baseline")
    public void verifyInitialYear() {

        assertTrue(
                page.yearSemesterTabSelected(),
                "TC_29: Tab Nam hoc-Hoc ky chua duoc chon."
        );

        assertEquals(
                "2023-2024",
                page.selectedYear(),
                "TC_29: Sai nam ban dau."
        );

        assertEquals(
                "Học kỳ 1",
                page.selectedTerm(),
                "TC_29: Sai hoc ky ban dau."
        );

        assertEquals(
                UI_HEADERS,
                page.headers(),
                "TC_29: Sai tieu de cot."
        );

        assertEquals(
                3,
                actualInitial.size(),
                "TC_29: Nam ban dau phai co 3 ban ghi."
        );

        assertRecords(
                expectedInitial,
                actualInitial,
                "Nam 2023-2024, HK1"
        );
    }

    @When("sinh viên đổi năm học lịch thi sang 2022-2023 TC_29")
    public void switchYear() {

        page.switchToTargetYear();
        actualTarget = page.records();
    }

    @Then("lịch thi năm mới TC_29 hiển thị đúng bộ lọc và baseline")
    public void verifyTargetYear() {

        assertTrue(
                page.yearSemesterTabSelected(),
                "TC_29: Tab Nam hoc-Hoc ky khong con duoc chon."
        );

        assertEquals(
                "2022-2023",
                page.selectedYear(),
                "TC_29: Nam hoc khong duoc cap nhat."
        );

        assertEquals(
                "Học kỳ 1",
                page.selectedTerm(),
                "TC_29: Hoc ky bi thay doi ngoai y muon."
        );

        assertEquals(
                UI_HEADERS,
                page.headers(),
                "TC_29: Sai tieu de cot sau khi doi nam."
        );

        assertEquals(
                5,
                actualTarget.size(),
                "TC_29: Nam 2022-2023 phai co 5 ban ghi."
        );

        assertRecords(
                expectedTarget,
                actualTarget,
                "Nam 2022-2023, HK1"
        );

        System.out.println(
                "[TC_29] Chuyen nam hoc va doi chieu: PASS."
        );
    }

    private void assertRecords(
            List<ExamRecord> expected,
            List<ExamRecord> actual,
            String stage
    ) {

        ExamScheduleComparator.Result result =
                comparator.compare(expected, actual);

        assertTrue(
                result.passed(),
                "TC_29 - " + stage
                        + ": Du lieu khong khop baseline. "
                        + result.details()
        );

        System.out.println(
                "[TC_29] " + stage + ": MATCH. "
                        + result.details()
        );
    }

    private List<ExamRecord> readInitialBaseline() {

        InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream(INITIAL_BASELINE);

        if (input == null) {
            throw new IllegalStateException(
                    "Khong tim thay baseline: "
                            + INITIAL_BASELINE
            );
        }

        try (
                InputStream stream = input;
                InputStreamReader reader =
                        new InputStreamReader(
                                stream,
                                StandardCharsets.UTF_8
                        );
                CSVParser csv =
                        CSVFormat.DEFAULT.builder()
                                .setHeader()
                                .setSkipHeaderRecord(true)
                                .get()
                                .parse(reader)
        ) {

            List<String> headers =
                    new ArrayList<>(csv.getHeaderNames());

            if (!headers.isEmpty()) {
                headers.set(
                        0,
                        headers.get(0).replace("\uFEFF", "")
                );
            }

            if (!CSV_HEADERS.equals(headers)) {
                throw new IllegalStateException(
                        "TC_29: Sai header CSV. "
                                + "Actual=" + headers
                );
            }

            List<ExamRecord> result = new ArrayList<>();

            for (CSVRecord row : csv) {

                if (!row.isConsistent() || row.size() != 11) {
                    throw new IllegalStateException(
                            "TC_29: Sai so cot CSV tai dong "
                                    + row.getRecordNumber()
                    );
                }

                result.add(new ExamRecord(
                        clean(row.get(0)),
                        clean(row.get(1)),
                        clean(row.get(2)),
                        clean(row.get(3)),
                        clean(row.get(4)),
                        clean(row.get(5)),
                        clean(row.get(6)),
                        clean(row.get(7)),
                        clean(row.get(8)),
                        clean(row.get(9)),
                        clean(row.get(10))
                ));
            }

            if (result.size() != 3) {
                throw new IllegalStateException(
                        "TC_29: Baseline ban dau phai co 3 dong."
                );
            }

            return List.copyOf(result);

        } catch (Exception ex) {
            throw new IllegalStateException(
                    "Khong doc duoc baseline TC_29.",
                    ex
            );
        }
    }

    private String clean(String value) {

        return value.replace('\u00A0', ' ')
                .replace('\u202F', ' ')
                .strip()
                .replaceAll("\\s+", " ");
    }
}
