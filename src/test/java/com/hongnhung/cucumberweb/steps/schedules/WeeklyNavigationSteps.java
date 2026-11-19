
package com.hongnhung.cucumberweb.steps.schedules;

import com.hongnhung.cucumberweb.assertions.schedules.WeeklyScheduleComparator;
import com.hongnhung.cucumberweb.models.schedules.WeeklyScheduleRecord;
import com.hongnhung.cucumberweb.pages.student.schedules.StudentWeeklySchedulePage;
import com.hongnhung.cucumberweb.pages.student.schedules.WeeklyNavigationPage;
import com.hongnhung.cucumberweb.testdata.schedules.WeeklyScheduleBaselineReader;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class WeeklyNavigationSteps {

    private static final String NEXT_BASELINE =
            "baselines/schedules/TC_24_next_week_expected.csv";

    private static final List<String> CSV_HEADERS =
            List.of(
                    "thu",
                    "buoi",
                    "monHoc",
                    "nhom",
                    "lop",
                    "tiet",
                    "giangVien"
            );

    private static final List<String> DAYS =
            List.of(
                    "Thứ 2",
                    "Thứ 3",
                    "Thứ 4",
                    "Thứ 5",
                    "Thứ 6",
                    "Thứ 7",
                    "Chủ nhật"
            );

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final WeeklyNavigationPage navigationPage;
    private final WeeklyScheduleBaselineReader initialReader;

    private final WeeklyScheduleComparator comparator =
            new WeeklyScheduleComparator();

    private StudentWeeklySchedulePage.Snapshot initialSnapshot;
    private StudentWeeklySchedulePage.Snapshot nextSnapshot;

    private List<WeeklyScheduleRecord> expectedInitial;
    private List<WeeklyScheduleRecord> expectedNext;

    public WeeklyNavigationSteps(
            WeeklyNavigationPage navigationPage,
            WeeklyScheduleBaselineReader initialReader
    ) {
        this.navigationPage = navigationPage;
        this.initialReader = initialReader;
    }

    @When("sinh viên mở và ghi nhận tuần ban đầu TC_24")
    public void openAndCaptureInitialWeek() {

        navigationPage.openInitialWeek();

        expectedInitial = initialReader.readRecords();
        expectedNext = readNextBaseline();

        initialSnapshot = navigationPage.snapshot();

        System.out.println(
                "[TC_24] Da ghi nhan tuan A."
        );
    }

    @Then("lịch tuần ban đầu TC_24 khớp baseline")
    public void verifyInitialWeek() {

        assertEquals(
                WeeklyNavigationPage.WEEK_A,
                navigationPage.selectedWeek(),
                "TC_24: Sai tuan ban dau."
        );

        assertEquals(
                "47",
                navigationPage.selectedWeekCode(),
                "TC_24: Sai ma tuan ban dau."
        );

        assertHeaders(LocalDate.of(2024, 11, 18));

        assertSnapshot(
                expectedInitial,
                initialSnapshot,
                "Tuan A ban dau"
        );
    }

    @When("sinh viên chuyển sang tuần tiếp theo TC_24")
    public void moveToNextWeek() {

        navigationPage.moveNext();
        nextSnapshot = navigationPage.snapshot();
    }

    @Then("tuần tiếp theo TC_24 tăng đúng bảy ngày và khớp baseline")
    public void verifyNextWeek() {

        assertEquals(
                WeeklyNavigationPage.WEEK_B,
                navigationPage.selectedWeek(),
                "TC_24: Sai tuan sau khi chuyen."
        );

        assertEquals(
                "48",
                navigationPage.selectedWeekCode(),
                "TC_24: Sai ma tuan tiep theo."
        );

        LocalDate startA = LocalDate.of(2024, 11, 18);
        LocalDate endA = LocalDate.of(2024, 11, 24);

        LocalDate startB = LocalDate.of(2024, 11, 25);
        LocalDate endB = LocalDate.of(2024, 12, 1);

        assertEquals(
                7,
                ChronoUnit.DAYS.between(startA, startB),
                "TC_24: Ngay bat dau khong tang 7 ngay."
        );

        assertEquals(
                7,
                ChronoUnit.DAYS.between(endA, endB),
                "TC_24: Ngay ket thuc khong tang 7 ngay."
        );

        assertHeaders(startB);

        assertSnapshot(
                expectedNext,
                nextSnapshot,
                "Tuan B"
        );

        System.out.println(
                "[TC_24] Chuyen tuan: MATCH."
        );
    }

    @When("sinh viên quay lại tuần trước TC_24")
    public void returnToPreviousWeek() {

        navigationPage.movePrevious();
    }

    @Then("tuần ban đầu TC_24 được khôi phục đúng dữ liệu")
    public void verifyRestoredWeek() {

        assertEquals(
                WeeklyNavigationPage.WEEK_A,
                navigationPage.selectedWeek(),
                "TC_24: Khong quay lai dung tuan A."
        );

        assertEquals(
                "47",
                navigationPage.selectedWeekCode(),
                "TC_24: Sai ma tuan khi quay lai."
        );

        assertHeaders(LocalDate.of(2024, 11, 18));

        StudentWeeklySchedulePage.Snapshot restored =
                navigationPage.snapshot();

        // Đối chiếu độc lập với baseline tuần A.
        assertSnapshot(
                expectedInitial,
                restored,
                "Tuan A sau khi quay lai"
        );

        // So sánh với dữ liệu đã ghi nhận trước khi chuyển.
        WeeklyScheduleComparator.Result initialComparison =
                comparator.compare(
                        initialSnapshot.lessons(),
                        restored.lessons(),
                        restored.emptyCells(),
                        restored.occupiedCells()
                );

        assertTrue(
                initialComparison.passed(),
                "TC_24: Du lieu khi quay lai khong khop "
                        + "du lieu ban dau. "
                        + initialComparison.details()
        );

        assertEquals(
                initialSnapshot.emptyCells(),
                restored.emptyCells(),
                "TC_24: Vi tri o trong thay doi."
        );

        System.out.println(
                "[TC_24] Quay lai tuan A: MATCH."
        );

        System.out.println(
                "[TC_24] Navigation test: PASS."
        );
    }

    private void assertHeaders(LocalDate monday) {

        List<String> expected = new ArrayList<>();
        expected.add("");

        for (int i = 0; i < DAYS.size(); i++) {

            expected.add(
                    DAYS.get(i) + " "
                            + monday.plusDays(i).format(DATE_FORMAT)
            );
        }

        assertEquals(
                expected,
                navigationPage.headers(),
                "TC_24: Sai ngay tren bang thoi khoa bieu."
        );
    }

    private void assertSnapshot(
            List<WeeklyScheduleRecord> baseline,
            StudentWeeklySchedulePage.Snapshot actual,
            String stage
    ) {

        WeeklyScheduleComparator.Result result =
                comparator.compare(
                        baseline,
                        actual.lessons(),
                        actual.emptyCells(),
                        actual.occupiedCells()
                );

        assertTrue(
                result.passed(),
                "TC_24 - " + stage
                        + ": Lich hoc khong khop baseline. "
                        + result.details()
        );

        System.out.println(
                "[TC_24] " + stage
                        + ": " + result.details()
        );
    }

    private List<WeeklyScheduleRecord> readNextBaseline() {

        InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream(NEXT_BASELINE);

        if (input == null) {
            throw new IllegalStateException(
                    "Khong tim thay baseline: " + NEXT_BASELINE
            );
        }

        try (
                InputStream stream = input;
                InputStreamReader reader =
                        new InputStreamReader(
                                stream,
                                StandardCharsets.UTF_8
                        );
                CSVParser parser =
                        CSVFormat.DEFAULT.builder()
                                .setHeader()
                                .setSkipHeaderRecord(true)
                                .get()
                                .parse(reader)
        ) {

            List<String> actualHeaders =
                    new ArrayList<>(parser.getHeaderNames());

            if (!actualHeaders.isEmpty()) {
                actualHeaders.set(
                        0,
                        actualHeaders.get(0)
                                .replace("\uFEFF", "")
                );
            }

            if (!CSV_HEADERS.equals(actualHeaders)) {
                throw new IllegalStateException(
                        "TC_24: Sai header CSV. "
                                + "Expected=" + CSV_HEADERS
                                + ", Actual=" + actualHeaders
                );
            }

            List<WeeklyScheduleRecord> result =
                    new ArrayList<>();

            for (CSVRecord row : parser) {

                if (!row.isConsistent() || row.size() != 7) {
                    throw new IllegalStateException(
                            "TC_24: Sai so cot CSV dong "
                                    + row.getRecordNumber()
                    );
                }

                result.add(new WeeklyScheduleRecord(
                        row.get(0).strip(),
                        row.get(1).strip(),
                        row.get(2).strip(),
                        row.get(3).strip(),
                        row.get(4).strip(),
                        row.get(5).strip(),
                        row.get(6).strip()
                ));
            }

            if (result.size() != 14) {
                throw new IllegalStateException(
                        "TC_24: Baseline tuan B phai co "
                                + "14 luot hoc. Actual=" + result.size()
                );
            }

            return List.copyOf(result);

        } catch (Exception ex) {
            throw new IllegalStateException(
                    "Khong doc duoc baseline tuan B TC_24.",
                    ex
            );
        }
    }
}
