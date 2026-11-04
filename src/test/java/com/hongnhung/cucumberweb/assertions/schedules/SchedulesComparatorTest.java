
package com.hongnhung.cucumberweb.assertions.schedules;

import com.hongnhung.cucumberweb.models.schedules.ScheduleRecord;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public final class SchedulesComparatorTest {

    private final SchedulesComparator comparator =
            new SchedulesComparator();

    // =========================================
    // DỮ LIỆU GIẢ LẬP
    // =========================================

    private List<ScheduleRecord> baseline() {

        return List.of(
                new ScheduleRecord(
                        "1", "DEMO001", "Học phần mẫu A",
                        "Giảng viên A", "3",
                        "Thứ Ba",
                        "1 (07g00)->2 (08g40)",
                        "A1.310",
                        "21/07/2026->18/08/2026",
                        "21/07/2026->18/08/2026",
                        "Địa điểm mẫu", "Địa chỉ mẫu"
                ),
                new ScheduleRecord(
                        "1", "DEMO001", "Học phần mẫu A",
                        "Giảng viên A", "3",
                        "Thứ Tư",
                        "1 (07g00)->2 (08g40)",
                        "A1.310",
                        "22/07/2026->19/08/2026",
                        "22/07/2026->19/08/2026",
                        "Địa điểm mẫu", "Địa chỉ mẫu"
                ),
                new ScheduleRecord(
                        "2", "DEMO002", "Học phần mẫu B",
                        "Giảng viên B", "3",
                        "Thứ Năm",
                        "6 (13g00)->10 (17g30)",
                        "A7.309",
                        "23/07/2026->20/08/2026",
                        "23/07/2026->20/08/2026",
                        "Địa điểm mẫu", "Địa chỉ mẫu"
                )
        );
    }

    private ScheduleRecord withRoom(
            ScheduleRecord r, String room
    ) {

        return new ScheduleRecord(
                r.sttHocPhan(),
                r.maLopHocPhan(),
                r.tenHocPhan(),
                r.tenGiaoVien(),
                r.soTC(),
                r.thu(),
                r.tietHoc(),
                room,
                r.tuan(),
                r.tuNgayDenNgay(),
                r.phongDiaDiem(),
                r.diaChi()
        );
    }

    private ScheduleRecord withPeriod(
            ScheduleRecord r, String period
    ) {

        return new ScheduleRecord(
                r.sttHocPhan(),
                r.maLopHocPhan(),
                r.tenHocPhan(),
                r.tenGiaoVien(),
                r.soTC(),
                r.thu(),
                period,
                r.phongHoc(),
                r.tuan(),
                r.tuNgayDenNgay(),
                r.phongDiaDiem(),
                r.diaChi()
        );
    }

    private ScheduleRecord withDateRange(
            ScheduleRecord r, String range
    ) {

        return new ScheduleRecord(
                r.sttHocPhan(),
                r.maLopHocPhan(),
                r.tenHocPhan(),
                r.tenGiaoVien(),
                r.soTC(),
                r.thu(),
                r.tietHoc(),
                r.phongHoc(),
                r.tuan(),
                range,
                r.phongDiaDiem(),
                r.diaChi()
        );
    }

    private void verify(
            String id,
            SchedulesComparator.Status expected,
            SchedulesComparator.Result result
    ) {

        System.out.println(
                "[" + id + "] " + result.details()
        );

        assertEquals(
                expected,
                result.status(),
                id + " trả về trạng thái không đúng."
        );

        assertEquals(
                expected == SchedulesComparator.Status.MATCH,
                result.passed()
        );
    }

    // =========================================
    // S01 - DỮ LIỆU KHỚP
    // =========================================

    @Test
    @DisplayName("S01 - Dữ liệu khớp baseline")
    void s01_match() {

        var result = comparator.compare(
                baseline(), baseline()
        );

        verify(
                "S01",
                SchedulesComparator.Status.MATCH,
                result
        );
    }

    // =========================================
    // S02 - SAI PHÒNG HỌC
    // =========================================

    @Test
    @DisplayName("S02 - Phát hiện sai phòng học")
    void s02_wrongRoom() {

        List<ScheduleRecord> actual =
                new ArrayList<>(baseline());

        actual.set(
                0,
                withRoom(actual.get(0), "B9.999")
        );

        var result = comparator.compare(
                baseline(), actual
        );

        verify(
                "S02",
                SchedulesComparator.Status.DATA_MISMATCH,
                result
        );

        assertTrue(
                result.differences().stream()
                        .anyMatch(s ->
                                s.contains("phongHoc"))
        );
    }

    // =========================================
    // S03 - SAI TIẾT HỌC
    // =========================================

    @Test
    @DisplayName("S03 - Phát hiện sai tiết học")
    void s03_wrongPeriod() {

        List<ScheduleRecord> actual =
                new ArrayList<>(baseline());

        actual.set(
                0,
                withPeriod(
                        actual.get(0),
                        "3 (09g00)->4 (10g40)"
                )
        );

        var result = comparator.compare(
                baseline(), actual
        );

        verify(
                "S03",
                SchedulesComparator.Status.DATA_MISMATCH,
                result
        );

        assertFalse(result.missing().isEmpty());
        assertFalse(result.extra().isEmpty());
    }

    // =========================================
    // S04 - SAI KHOẢNG NGÀY
    // =========================================

    @Test
    @DisplayName("S04 - Phát hiện sai khoảng ngày")
    void s04_wrongDateRange() {

        List<ScheduleRecord> actual =
                new ArrayList<>(baseline());

        actual.set(
                1,
                withDateRange(
                        actual.get(1),
                        "22/07/2026->20/08/2026"
                )
        );

        var result = comparator.compare(
                baseline(), actual
        );

        verify(
                "S04",
                SchedulesComparator.Status.DATA_MISMATCH,
                result
        );
    }

    // =========================================
    // S05 - THIẾU DÒNG LỊCH HỌC
    // =========================================

    @Test
    @DisplayName("S05 - Phát hiện thiếu dòng lịch học")
    void s05_missingRow() {

        List<ScheduleRecord> actual =
                new ArrayList<>(baseline());

        actual.remove(1);

        var result = comparator.compare(
                baseline(), actual
        );

        verify(
                "S05",
                SchedulesComparator.Status.DATA_MISMATCH,
                result
        );

        assertEquals(3, result.expectedCount());
        assertEquals(2, result.actualCount());
    }

    // =========================================
    // S06 - TRÙNG DÒNG LỊCH HỌC
    // =========================================

    @Test
    @DisplayName("S06 - Phát hiện dòng lịch học bị trùng")
    void s06_duplicateRow() {

        List<ScheduleRecord> actual =
                new ArrayList<>(baseline());

        actual.set(1, actual.get(0));

        var result = comparator.compare(
                baseline(), actual
        );

        verify(
                "S06",
                SchedulesComparator.Status.DATA_MISMATCH,
                result
        );

        assertEquals(3, result.actualCount());

        assertFalse(result.missing().isEmpty());
        assertFalse(result.extra().isEmpty());
    }

    // =========================================
    // S07 - ĐẢO THỨ TỰ
    // =========================================

    @Test
    @DisplayName("S07 - Đảo thứ tự vẫn khớp")
    void s07_reorderedRows() {

        List<ScheduleRecord> actual =
                new ArrayList<>(baseline());

        Collections.reverse(actual);

        var result = comparator.compare(
                baseline(), actual
        );

        verify(
                "S07",
                SchedulesComparator.Status.MATCH,
                result
        );
    }

    // =========================================
    // S08 - THIẾU LỚP HỌC PHẦN
    // =========================================

    @Test
    @DisplayName("S08 - Phát hiện thiếu lớp học phần")
    void s08_missingCourse() {

        List<ScheduleRecord> actual =
                baseline().stream()
                        .filter(r ->
                                !r.maLopHocPhan()
                                        .equals("DEMO002"))
                        .toList();

        var result = comparator.compare(
                baseline(), actual
        );

        verify(
                "S08",
                SchedulesComparator.Status.DATA_MISMATCH,
                result
        );

        assertEquals(
                2,
                comparator.countCourses(baseline())
        );

        assertEquals(
                1,
                comparator.countCourses(actual)
        );
    }
}
