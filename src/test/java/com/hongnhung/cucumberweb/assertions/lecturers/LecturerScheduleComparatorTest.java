
package com.hongnhung.cucumberweb.assertions.lecturers;

import com.hongnhung.cucumberweb.models.lecturers.LecturerScheduleRecord;
import com.hongnhung.cucumberweb.testdata.lecturers.LecturerScheduleBaselineReader;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public final class LecturerScheduleComparatorTest {

    private final LecturerScheduleComparator comparator =
            new LecturerScheduleComparator();

    // =========================================
    // DỮ LIỆU GIẢ LẬP
    // =========================================

    private List<LecturerScheduleRecord> baseline() {

        return List.of(
                new LecturerScheduleRecord(
                        "DEMO001",
                        "Lập trình hướng đối tượng",
                        "30", "30", "0", "3",
                        "KTPM K48",
                        "Giảng viên mẫu A",
                        "53",
                        "Thứ Hai",
                        "1-2",
                        "A1.310",
                        "_2345678901234_____"
                ),
                new LecturerScheduleRecord(
                        "DEMO001",
                        "Lập trình hướng đối tượng",
                        "30", "30", "0", "3",
                        "KTPM K48",
                        "Giảng viên mẫu A",
                        "53",
                        "Thứ Tư",
                        "3-4",
                        "A1.310",
                        "_2345678901234_____"
                ),
                new LecturerScheduleRecord(
                        "DEMO002",
                        "Học phần mẫu B",
                        "30", "30", "0", "3",
                        "CNTT K48",
                        "Giảng viên mẫu A",
                        "55",
                        "Thứ Sáu",
                        "6-7",
                        "A5.07",
                        "_234567890123______"
                )
        );
    }

    private LecturerScheduleRecord change(
            LecturerScheduleRecord r,
            String field,
            String value
    ) {

        return new LecturerScheduleRecord(
                r.maLHP(),
                r.tenHocPhan(),
                r.soTietLT(),
                r.soTietTH(),
                r.soTietTL(),
                r.soTC(),
                r.maLop(),
                field.equals("tenCBGV")
                        ? value : r.tenCBGV(),
                r.siSo(),
                r.thu(),
                r.tiet(),
                field.equals("phong")
                        ? value : r.phong(),
                field.equals("tuanHoc")
                        ? value : r.tuanHoc()
        );
    }

    private void verify(
            String id,
            LecturerScheduleComparator.Status expected,
            LecturerScheduleComparator.Result result
    ) {

        System.out.println(
                "[" + id + "] " + result.details()
        );

        assertEquals(
                expected,
                result.status()
        );

        assertEquals(
                expected == LecturerScheduleComparator.Status.MATCH,
                result.passed()
        );
    }

    // =========================================
    // L00 - ĐỌC BASELINE THỰC TẾ
    // =========================================

    @Test
    @DisplayName("L00 - Xác nhận baseline 34 dòng và 11 mã LHP")
    void l00_readRealBaseline() {

        List<LecturerScheduleRecord> records =
                new LecturerScheduleBaselineReader()
                        .readRecords();

        assertEquals(
                34,
                records.size()
        );

        assertEquals(
                11,
                comparator.countCourses(records)
        );

        for (LecturerScheduleRecord r : records) {

            assertEquals(
                    "****001 - Phạm Trần Thiện - "
                            + "Khoa Công nghệ thông tin",
                    r.tenCBGV()
            );
        }

        System.out.println(
                "[L00] Baseline hợp lệ: 34 dòng, "
                        + "11 mã LHP."
        );
    }

    // =========================================
    // L01 - DỮ LIỆU KHỚP
    // =========================================

    @Test
    @DisplayName("L01 - Dữ liệu khớp và chuẩn hóa Unicode")
    void l01_match() {

        List<LecturerScheduleRecord> actual =
                new ArrayList<>(baseline());

        LecturerScheduleRecord first =
                actual.get(0);

        actual.set(
                0,
                new LecturerScheduleRecord(
                        first.maLHP(),
                        Normalizer.normalize(
                                first.tenHocPhan(),
                                Normalizer.Form.NFD
                        ),
                        "030",
                        first.soTietTH(),
                        first.soTietTL(),
                        first.soTC(),
                        first.maLop(),
                        first.tenCBGV(),
                        first.siSo(),
                        first.thu(),
                        first.tiet(),
                        first.phong(),
                        first.tuanHoc()
                )
        );

        verify(
                "L01",
                LecturerScheduleComparator.Status.MATCH,
                comparator.compare(
                        baseline(),
                        actual
                )
        );
    }

    // =========================================
    // L02 - SAI PHÒNG HỌC
    // =========================================

    @Test
    @DisplayName("L02 - Phát hiện sai phòng học")
    void l02_wrongRoom() {

        List<LecturerScheduleRecord> actual =
                new ArrayList<>(baseline());

        actual.set(
                0,
                change(
                        actual.get(0),
                        "phong",
                        "B9.999"
                )
        );

        var result = comparator.compare(
                baseline(),
                actual
        );

        verify(
                "L02",
                LecturerScheduleComparator.Status.DATA_MISMATCH,
                result
        );

        assertTrue(
                result.differences()
                        .stream()
                        .anyMatch(s -> s.contains("phong"))
        );
    }

    // =========================================
    // L03 - SAI CHUỖI TUẦN HỌC
    // =========================================

    @Test
    @DisplayName("L03 - Phát hiện sai chuỗi tuần học")
    void l03_wrongWeeks() {

        List<LecturerScheduleRecord> actual =
                new ArrayList<>(baseline());

        actual.set(
                0,
                change(
                        actual.get(0),
                        "tuanHoc",
                        "_23456789012345____"
                )
        );

        var result = comparator.compare(
                baseline(),
                actual
        );

        verify(
                "L03",
                LecturerScheduleComparator.Status.DATA_MISMATCH,
                result
        );

        assertFalse(
                result.missing().isEmpty()
        );

        assertFalse(
                result.extra().isEmpty()
        );
    }

    // =========================================
    // L04 - THIẾU DÒNG LỊCH GIẢNG DẠY
    // =========================================

    @Test
    @DisplayName("L04 - Phát hiện thiếu dòng lịch giảng dạy")
    void l04_missingRow() {

        List<LecturerScheduleRecord> actual =
                new ArrayList<>(baseline());

        actual.remove(1);

        var result = comparator.compare(
                baseline(),
                actual
        );

        verify(
                "L04",
                LecturerScheduleComparator.Status.DATA_MISMATCH,
                result
        );

        assertEquals(
                3,
                result.expectedCount()
        );

        assertEquals(
                2,
                result.actualCount()
        );
    }

    // =========================================
    // L05 - THỪA DÒNG LỊCH GIẢNG DẠY
    // =========================================

    @Test
    @DisplayName("L05 - Phát hiện thừa dòng lịch giảng dạy")
    void l05_extraRow() {

        List<LecturerScheduleRecord> actual =
                new ArrayList<>(baseline());

        actual.add(actual.get(0));

        var result = comparator.compare(
                baseline(),
                actual
        );

        verify(
                "L05",
                LecturerScheduleComparator.Status.DATA_MISMATCH,
                result
        );

        assertEquals(
                4,
                result.actualCount()
        );

        assertFalse(
                result.extra().isEmpty()
        );
    }

    // =========================================
    // L06 - TRÙNG VÀ THIẾU DÒNG
    // =========================================

    @Test
    @DisplayName("L06 - Phát hiện trùng và thiếu dòng lịch")
    void l06_duplicateRow() {

        List<LecturerScheduleRecord> actual =
                new ArrayList<>(baseline());

        // Số dòng vẫn là 3 nhưng có một dòng bị lặp.
        actual.set(1, actual.get(0));

        var result = comparator.compare(
                baseline(),
                actual
        );

        verify(
                "L06",
                LecturerScheduleComparator.Status.DATA_MISMATCH,
                result
        );

        assertEquals(
                3,
                result.actualCount()
        );

        assertFalse(
                result.missing().isEmpty()
        );

        assertFalse(
                result.extra().isEmpty()
        );
    }

    // =========================================
    // L07 - ĐẢO THỨ TỰ
    // =========================================

    @Test
    @DisplayName("L07 - Đảo thứ tự dòng vẫn khớp")
    void l07_reorderedRows() {

        List<LecturerScheduleRecord> actual =
                new ArrayList<>(baseline());

        Collections.reverse(actual);

        verify(
                "L07",
                LecturerScheduleComparator.Status.MATCH,
                comparator.compare(
                        baseline(),
                        actual
                )
        );
    }

    // =========================================
    // L08 - SAI TÊN GIẢNG VIÊN
    // =========================================

    @Test
    @DisplayName("L08 - Phát hiện sai tên giảng viên")
    void l08_wrongLecturer() {

        List<LecturerScheduleRecord> actual =
                new ArrayList<>(baseline());

        actual.set(
                0,
                change(
                        actual.get(0),
                        "tenCBGV",
                        "Giảng viên mẫu B"
                )
        );

        var result = comparator.compare(
                baseline(),
                actual
        );

        verify(
                "L08",
                LecturerScheduleComparator.Status.DATA_MISMATCH,
                result
        );

        assertTrue(
                result.differences()
                        .stream()
                        .anyMatch(s ->
                                s.contains("tenCBGV"))
        );
    }
}
