
package com.hongnhung.cucumberweb.steps.schedules;

import com.hongnhung.cucumberweb.assertions.schedules.RoomScheduleComparator;
import com.hongnhung.cucumberweb.models.schedules.RoomScheduleRecord;
import com.hongnhung.cucumberweb.pages.student.schedules.RoomWeekNavigationPage;
import com.hongnhung.cucumberweb.pages.student.schedules.RoomWeeklySchedulePage;
import com.hongnhung.cucumberweb.testdata.schedules.RoomScheduleBaselineReader;

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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class RoomWeekNavigationSteps {

    private static final String NEXT_BASELINE =
            "baselines/schedules/TC_25_next_week_expected.csv";

    private static final List<String> CSV_HEADERS =
            List.of(
                    "phong",
                    "thu",
                    "monHoc",
                    "nhom",
                    "lop",
                    "tiet",
                    "giangVien"
            );

    private static final List<String> DAYS =
            List.of(
                    "Thứ 2", "Thứ 3", "Thứ 4",
                    "Thứ 5", "Thứ 6", "Thứ 7",
                    "Chủ nhật"
            );

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final RoomWeeklySchedulePage roomPage;
    private final RoomWeekNavigationPage navigationPage;
    private final RoomScheduleBaselineReader initialReader;

    private final RoomScheduleComparator initialComparator =
            new RoomScheduleComparator();

    private List<RoomScheduleRecord> expectedA;
    private List<RoomScheduleRecord> expectedB;

    private RoomWeeklySchedulePage.Snapshot snapshotA;
    private RoomWeekNavigationPage.Snapshot snapshotB;

    private LocalDate startA;
    private LocalDate startB;

    public RoomWeekNavigationSteps(
            RoomWeeklySchedulePage roomPage,
            RoomWeekNavigationPage navigationPage,
            RoomScheduleBaselineReader initialReader
    ) {
        this.roomPage = roomPage;
        this.navigationPage = navigationPage;
        this.initialReader = initialReader;
    }

    @When("sinh viên mở và ghi nhận lịch phòng tuần ban đầu TC_25")
    public void openInitialWeek() {

        // Mo tuan A bang Page Object TC_23 da PASS.
        roomPage.open();

        expectedA = initialReader.readRecords();
        expectedB = readNextBaseline();

        assertEquals(
                RoomWeekNavigationPage.WEEK_A,
                navigationPage.selectedWeek()
        );

        assertEquals(
                "50",
                navigationPage.selectedWeekCode()
        );

        startA = LocalDate.of(2024, 12, 9);

        assertHeaders(startA);

        snapshotA = roomPage.snapshot();

        assertInitialSnapshot(
                snapshotA,
                "Tuan A ban dau"
        );

        System.out.println(
                "[TC_25] Da ghi nhan tuan A: "
                        + RoomWeekNavigationPage.WEEK_A
        );
    }

    @When("sinh viên chuyển sang tuần tiếp theo của lịch phòng TC_25")
    public void moveToNextWeek() {

        navigationPage.nextWeek();

        snapshotB = navigationPage.snapshotNextWeek();

        startB = LocalDate.of(2024, 12, 16);
    }

    @Then("lịch phòng tuần tiếp theo TC_25 tăng bảy ngày và khớp baseline")
    public void verifyNextWeek() {

        assertEquals(
                RoomWeekNavigationPage.WEEK_B,
                navigationPage.selectedWeek(),
                "TC_25: Sai tuan B."
        );

        assertEquals(
                "51",
                navigationPage.selectedWeekCode(),
                "TC_25: Sai ma tuan B."
        );

        assertEquals(
                7,
                ChronoUnit.DAYS.between(startA, startB),
                "TC_25: Khoang ngay khong tang 7 ngay."
        );

        assertHeaders(startB);

        assertEquals(
                navigationPage.expectedRoomsNextWeek(),
                snapshotB.rooms(),
                "TC_25: Sai danh sach phong tuan B."
        );

        assertNextSnapshot();

        System.out.println(
                "[TC_25] Tuan B: 4 phong, "
                        + "5 luot hoc, 23 o trong - MATCH."
        );
    }

    @When("sinh viên quay lại tuần trước của lịch phòng TC_25")
    public void returnToPreviousWeek() {

        navigationPage.previousWeek();
    }

    @Then("lịch phòng tuần ban đầu TC_25 được khôi phục")
    public void verifyRestoredWeek() {

        assertEquals(
                RoomWeekNavigationPage.WEEK_A,
                navigationPage.selectedWeek(),
                "TC_25: Khong quay lai dung tuan A."
        );

        assertEquals(
                "50",
                navigationPage.selectedWeekCode(),
                "TC_25: Sai ma tuan khi quay lai."
        );

        assertHeaders(startA);

        assertEquals(
                roomPage.expectedRooms(),
                roomPage.rooms(),
                "TC_25: Danh sach 7 phong "
                        + "khong duoc khoi phuc."
        );

        RoomWeeklySchedulePage.Snapshot restored =
                roomPage.snapshot();

        // Doi chieu lai voi baseline doc thu cong.
        assertInitialSnapshot(
                restored,
                "Tuan A sau khi quay lai"
        );

        // Doi chieu voi du lieu thuc te truoc khi chuyen.
        RoomScheduleComparator.Result comparison =
                initialComparator.compare(
                        snapshotA.lessons(),
                        restored.lessons(),
                        restored.emptyCells(),
                        restored.occupiedCells()
                );

        assertTrue(
                comparison.passed(),
                "TC_25: Du lieu sau khi quay lai "
                        + "khac du lieu ban dau. "
                        + comparison.details()
        );

        assertEquals(
                snapshotA.emptyCells(),
                restored.emptyCells(),
                "TC_25: Vi tri o trong thay doi."
        );

        System.out.println(
                "[TC_25] Da khoi phuc tuan A: MATCH."
        );
    }

    private void assertHeaders(LocalDate monday) {

        List<String> expected = new ArrayList<>();
        expected.add("Phòng");

        for (int i = 0; i < 7; i++) {
            expected.add(
                    DAYS.get(i) + " "
                            + monday.plusDays(i).format(DATE_FORMAT)
            );
        }

        assertEquals(
                expected,
                navigationPage.headers(),
                "TC_25: Sai ngay tren bang lich phong."
        );
    }

    private void assertInitialSnapshot(
            RoomWeeklySchedulePage.Snapshot actual,
            String stage
    ) {

        RoomScheduleComparator.Result result =
                initialComparator.compare(
                        expectedA,
                        actual.lessons(),
                        actual.emptyCells(),
                        actual.occupiedCells()
                );

        assertTrue(
                result.passed(),
                "TC_25 - " + stage
                        + ": Sai baseline tuan A. "
                        + result.details()
        );

        System.out.println(
                "[TC_25] " + stage + ": MATCH."
        );
    }

    private void assertNextSnapshot() {

        assertEquals(
                5,
                snapshotB.lessons().size(),
                "TC_25: Sai so luot hoc tuan B."
        );

        assertEquals(
                5,
                snapshotB.occupiedCells(),
                "TC_25: Sai so o co lich tuan B."
        );

        assertEquals(
                23,
                snapshotB.emptyCells().size(),
                "TC_25: Sai so o trong tuan B."
        );

        assertEquals(
                28,
                snapshotB.occupiedCells()
                        + snapshotB.emptyCells().size(),
                "TC_25: Tong so o lich phai bang 28."
        );

        // Kiem tra thieu, thua, trung ban ghi.
        assertEquals(
                countRecords(expectedB),
                countRecords(snapshotB.lessons()),
                "TC_25: Du lieu cac luot hoc "
                        + "tuan B khong khop baseline."
        );

        // Xac dinh vi tri o co lich tu baseline.
        Set<String> occupied = new HashSet<>();

        for (RoomScheduleRecord record : expectedB) {
            occupied.add(
                    record.phong() + " / " + record.thu()
            );
        }

        Set<String> expectedEmpty = new HashSet<>();

        for (String room :
                navigationPage.expectedRoomsNextWeek()) {

            for (String day : DAYS) {

                String cellKey = room + " / " + day;

                if (!occupied.contains(cellKey)) {
                    expectedEmpty.add(cellKey);
                }
            }
        }

        assertEquals(
                expectedEmpty,
                new HashSet<>(snapshotB.emptyCells()),
                "TC_25: Vi tri o trong "
                        + "tuan B khong khop baseline."
        );
    }

    private Map<RoomScheduleRecord, Integer> countRecords(
            List<RoomScheduleRecord> records
    ) {

        Map<RoomScheduleRecord, Integer> counts =
                new HashMap<>();

        for (RoomScheduleRecord record : records) {
            counts.merge(record, 1, Integer::sum);
        }

        return counts;
    }

    private List<RoomScheduleRecord> readNextBaseline() {

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
                        "TC_25: Sai header CSV. "
                                + "Expected=" + CSV_HEADERS
                                + ", Actual=" + actualHeaders
                );
            }

            List<RoomScheduleRecord> result =
                    new ArrayList<>();

            for (CSVRecord row : parser) {

                if (!row.isConsistent() || row.size() != 7) {
                    throw new IllegalStateException(
                            "TC_25: Sai so cot CSV tai dong "
                                    + row.getRecordNumber()
                    );
                }

                result.add(new RoomScheduleRecord(
                        row.get(0).strip(),
                        row.get(1).strip(),
                        row.get(2).strip(),
                        row.get(3).strip(),
                        row.get(4).strip(),
                        row.get(5).strip(),
                        row.get(6).strip()
                ));
            }

            if (result.size() != 5) {
                throw new IllegalStateException(
                        "TC_25: Baseline tuan B "
                                + "phai co 5 luot hoc. Actual="
                                + result.size()
                );
            }

            return List.copyOf(result);

        } catch (Exception ex) {
            throw new IllegalStateException(
                    "Khong doc duoc baseline TC_25.",
                    ex
            );
        }
    }
}
