
package com.hongnhung.cucumberweb.assertions.unaccumulated;

import com.hongnhung.cucumberweb.models.unaccumulated.UnaccumulatedCourseRecord;
import com.hongnhung.cucumberweb.testdata.unaccumulated.UnaccumulatedBaselineReader;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public final class UnaccumulatedCourseComparatorTest {

    private final UnaccumulatedCourseComparator comparator =
            new UnaccumulatedCourseComparator();

    // =========================================
    // DỮ LIỆU MẪU ĐỘC LẬP VỚI WEBSITE
    // =========================================

    private List<UnaccumulatedCourseRecord> sample() {

        return List.of(
                new UnaccumulatedCourseRecord(
                        "DEMO001",
                        "Công nghệ Web",
                        "3",
                        "30",
                        "30",
                        "0",
                        "",
                        "",
                        ""
                ),
                new UnaccumulatedCourseRecord(
                        "DEMO002",
                        "Học phần mẫu B",
                        "2",
                        "22",
                        "0",
                        "8",
                        "",
                        "",
                        ""
                ),
                new UnaccumulatedCourseRecord(
                        "DEMO003",
                        "Học phần mẫu C",
                        "8",
                        "0",
                        "0",
                        "0",
                        "",
                        "",
                        ""
                )
        );
    }

    private UnaccumulatedCourseRecord change(
            UnaccumulatedCourseRecord r,
            String field,
            String value
    ) {

        return new UnaccumulatedCourseRecord(
                field.equals("maHocPhan")
                        ? value : r.maHocPhan(),
                field.equals("tenHocPhan")
                        ? value : r.tenHocPhan(),
                field.equals("soTC")
                        ? value : r.soTC(),
                field.equals("soTietLT")
                        ? value : r.soTietLT(),
                field.equals("soTietTH")
                        ? value : r.soTietTH(),
                field.equals("soTietTL")
                        ? value : r.soTietTL(),
                field.equals("hocPhanTuongDuong")
                        ? value : r.hocPhanTuongDuong(),
                field.equals("hocPhanHocTruoc")
                        ? value : r.hocPhanHocTruoc(),
                field.equals("hocPhanThayThe")
                        ? value : r.hocPhanThayThe()
        );
    }

    private void verify(
            String id,
            UnaccumulatedCourseComparator.Status expected,
            UnaccumulatedCourseComparator.Result actual
    ) {

        System.out.println(
                "[" + id + "] " + actual.details()
        );

        assertEquals(
                expected,
                actual.status()
        );

        assertEquals(
                expected
                        == UnaccumulatedCourseComparator.Status.MATCH,
                actual.passed()
        );
    }

    // =========================================
    // U00 - KIỂM TRA BASELINE THỰC TẾ
    // =========================================

    @Test
    @DisplayName("U00 - Đọc baseline TC_17: 7 học phần, 23 tín chỉ")
    void u00_readRealBaseline() {

        List<UnaccumulatedCourseRecord> records =
                new UnaccumulatedBaselineReader()
                        .readRecords();

        assertEquals(
                7,
                records.size()
        );

        assertEquals(
                7,
                comparator.countCourses(records)
        );

        assertEquals(
                23,
                comparator.totalCredits(records)
        );

        System.out.println(
                "[U00] Baseline hợp lệ: "
                        + "7 học phần, 23 tín chỉ."
        );
    }

    // =========================================
    // U01 - DỮ LIỆU KHỚP
    // =========================================

    @Test
    @DisplayName("U01 - Dữ liệu khớp và chuẩn hóa Unicode, số nguyên")
    void u01_match() {

        List<UnaccumulatedCourseRecord> actual =
                new ArrayList<>(sample());

        UnaccumulatedCourseRecord first =
                actual.get(0);

        actual.set(
                0,
                change(
                        first,
                        "tenHocPhan",
                        Normalizer.normalize(
                                first.tenHocPhan(),
                                Normalizer.Form.NFD
                        )
                )
        );

        actual.set(
                1,
                change(
                        actual.get(1),
                        "soTC",
                        "02"
                )
        );

        verify(
                "U01",
                UnaccumulatedCourseComparator.Status.MATCH,
                comparator.compare(
                        sample(),
                        actual
                )
        );
    }

    // =========================================
    // U02 - SAI SỐ TÍN CHỈ
    // =========================================

    @Test
    @DisplayName("U02 - Phát hiện sai số tín chỉ")
    void u02_wrongCredits() {

        List<UnaccumulatedCourseRecord> actual =
                new ArrayList<>(sample());

        actual.set(
                0,
                change(
                        actual.get(0),
                        "soTC",
                        "4"
                )
        );

        var result = comparator.compare(
                sample(),
                actual
        );

        verify(
                "U02",
                UnaccumulatedCourseComparator.Status.DATA_MISMATCH,
                result
        );

        assertTrue(
                result.differences()
                        .stream()
                        .anyMatch(s -> s.contains("soTC"))
        );
    }

    // =========================================
    // U03 - SAI SỐ TIẾT
    // =========================================

    @Test
    @DisplayName("U03 - Phát hiện sai số tiết thực hành")
    void u03_wrongPeriods() {

        List<UnaccumulatedCourseRecord> actual =
                new ArrayList<>(sample());

        actual.set(
                0,
                change(
                        actual.get(0),
                        "soTietTH",
                        "45"
                )
        );

        var result = comparator.compare(
                sample(),
                actual
        );

        verify(
                "U03",
                UnaccumulatedCourseComparator.Status.DATA_MISMATCH,
                result
        );

        assertTrue(
                result.differences()
                        .stream()
                        .anyMatch(s -> s.contains("soTietTH"))
        );
    }

    // =========================================
    // U04 - THIẾU HỌC PHẦN
    // =========================================

    @Test
    @DisplayName("U04 - Phát hiện thiếu học phần")
    void u04_missingCourse() {

        List<UnaccumulatedCourseRecord> actual =
                new ArrayList<>(sample());

        actual.remove(1);

        var result = comparator.compare(
                sample(),
                actual
        );

        verify(
                "U04",
                UnaccumulatedCourseComparator.Status.DATA_MISMATCH,
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

        assertFalse(
                result.missing().isEmpty()
        );
    }

    // =========================================
    // U05 - THỪA HỌC PHẦN
    // =========================================

    @Test
    @DisplayName("U05 - Phát hiện học phần không có trong baseline")
    void u05_extraCourse() {

        List<UnaccumulatedCourseRecord> actual =
                new ArrayList<>(sample());

        actual.add(
                new UnaccumulatedCourseRecord(
                        "DEMO999",
                        "Học phần ngoài baseline",
                        "3",
                        "30",
                        "0",
                        "0",
                        "",
                        "",
                        ""
                )
        );

        var result = comparator.compare(
                sample(),
                actual
        );

        verify(
                "U05",
                UnaccumulatedCourseComparator.Status.DATA_MISMATCH,
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
    // U06 - TRÙNG MÃ VÀ THIẾU HỌC PHẦN
    // =========================================

    @Test
    @DisplayName("U06 - Phát hiện trùng học phần dù số dòng không đổi")
    void u06_duplicateCourse() {

        List<UnaccumulatedCourseRecord> actual =
                new ArrayList<>(sample());

        actual.set(
                1,
                actual.get(0)
        );

        var result = comparator.compare(
                sample(),
                actual
        );

        verify(
                "U06",
                UnaccumulatedCourseComparator.Status.DATA_MISMATCH,
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
    // U07 - ĐẢO THỨ TỰ
    // =========================================

    @Test
    @DisplayName("U07 - Đảo thứ tự không làm thay đổi kết quả")
    void u07_reorderedCourses() {

        List<UnaccumulatedCourseRecord> actual =
                new ArrayList<>(sample());

        Collections.reverse(actual);

        verify(
                "U07",
                UnaccumulatedCourseComparator.Status.MATCH,
                comparator.compare(
                        sample(),
                        actual
                )
        );
    }

    // =========================================
    // U08 - PHÁT HIỆN SAI TRƯỜNG ĐANG TRỐNG
    // =========================================

    @Test
    @DisplayName("U08 - Phát hiện giá trị mới ở cột học phần học trước")
    void u08_unexpectedPrerequisite() {

        List<UnaccumulatedCourseRecord> actual =
                new ArrayList<>(sample());

        actual.set(
                0,
                change(
                        actual.get(0),
                        "hocPhanHocTruoc",
                        "DEMO000"
                )
        );

        var result = comparator.compare(
                sample(),
                actual
        );

        verify(
                "U08",
                UnaccumulatedCourseComparator.Status.DATA_MISMATCH,
                result
        );

        assertTrue(
                result.differences()
                        .stream()
                        .anyMatch(
                                s -> s.contains(
                                        "hocPhanHocTruoc"
                                )
                        )
        );
    }
}
