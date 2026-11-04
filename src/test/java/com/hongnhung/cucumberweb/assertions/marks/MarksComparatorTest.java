
package com.hongnhung.cucumberweb.assertions.marks;

import com.hongnhung.cucumberweb.models.marks.MarkRecord;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public final class MarksComparatorTest {

    private final MarksComparator comparator =
            new MarksComparator();

    // =========================================
    // DỮ LIỆU GIẢ LẬP BAN ĐẦU
    // =========================================

    private List<MarkRecord> baselineCourses() {

        return List.of(
                new MarkRecord(
                        "DEMO001",
                        "Học phần mẫu A",
                        "4",
                        "7.5",
                        "3",
                        "B+",
                        "dau_tich_xanh"
                ),
                new MarkRecord(
                        "DEMO002",
                        "Học phần mẫu B",
                        "3",
                        "5.3",
                        "2",
                        "C",
                        "dau_tich_xanh"
                ),
                new MarkRecord(
                        "DEMO003",
                        "Học phần mẫu C",
                        "3",
                        "8.5",
                        "3.5",
                        "A",
                        "dau_tich_xanh"
                )
        );
    }

    private Map<String, String> baselineSummary() {

        Map<String, String> result =
                new LinkedHashMap<>();

        result.put("tongSoTinChi", "10");
        result.put("soTinChiDat", "10");
        result.put("soTinChiKhongDat", "0");
        result.put("diemTrungBinhHocKyHe10", "7.1");
        result.put("diemTrungBinhHocKyHe4", "2.85");

        return result;
    }

    private void verify(
            String experimentId,
            MarksComparator.Status expectedStatus,
            MarksComparator.Result result
    ) {

        System.out.println(
                "[" + experimentId + "] "
                        + result.details()
        );

        assertEquals(
                expectedStatus,
                result.status(),
                experimentId
                        + " không trả về trạng thái kỳ vọng."
        );

        assertEquals(
                expectedStatus == MarksComparator.Status.MATCH,
                result.passed(),
                experimentId
                        + " không đúng trạng thái PASS/FAIL."
        );
    }

    // =========================================
    // M01 - DỮ LIỆU KHỚP
    // =========================================

    @Test
    @DisplayName("M01 - Dữ liệu học phần khớp baseline")
    void m01_matchingData() {

        List<MarkRecord> actual =
                new ArrayList<>(baselineCourses());

        MarkRecord r = actual.get(0);

        // Khác biểu diễn số và Unicode,
        // nhưng giá trị vẫn tương đương.
        actual.set(0, new MarkRecord(
                r.maHocPhan(),
                "Ho\u0323c phần mẫu A",
                "4.0",
                "7.50",
                "3.00",
                r.diemChu(),
                r.ketQuaHienThi()
        ));

        MarksComparator.Result result =
                comparator.compareCourses(
                        baselineCourses(),
                        actual
                );

        verify(
                "M01",
                MarksComparator.Status.MATCH,
                result
        );

        assertTrue(result.missing().isEmpty());
        assertTrue(result.extra().isEmpty());
        assertTrue(result.differences().isEmpty());
    }

    // =========================================
    // M02 - SAI ĐIỂM HỆ 10
    // =========================================

    @Test
    @DisplayName("M02 - Phát hiện sai điểm hệ 10")
    void m02_wrongMark10() {

        List<MarkRecord> actual =
                new ArrayList<>(baselineCourses());

        MarkRecord r = actual.get(0);

        actual.set(0, new MarkRecord(
                r.maHocPhan(),
                r.tenHocPhan(),
                r.tinChi(),
                "7.6",
                r.diem4(),
                r.diemChu(),
                r.ketQuaHienThi()
        ));

        MarksComparator.Result result =
                comparator.compareCourses(
                        baselineCourses(),
                        actual
                );

        verify(
                "M02",
                MarksComparator.Status.DATA_MISMATCH,
                result
        );

        assertEquals(3, result.actualCount());

        assertTrue(
                result.differences().stream()
                        .anyMatch(s ->
                                s.contains("diem10")
                                        && s.contains("7.5")
                                        && s.contains("7.6"))
        );
    }

    // =========================================
    // M03 - SAI ĐIỂM CHỮ
    // =========================================

    @Test
    @DisplayName("M03 - Phát hiện sai điểm chữ")
    void m03_wrongLetterGrade() {

        List<MarkRecord> actual =
                new ArrayList<>(baselineCourses());

        MarkRecord r = actual.get(1);

        actual.set(1, new MarkRecord(
                r.maHocPhan(),
                r.tenHocPhan(),
                r.tinChi(),
                r.diem10(),
                r.diem4(),
                "B",
                r.ketQuaHienThi()
        ));

        MarksComparator.Result result =
                comparator.compareCourses(
                        baselineCourses(),
                        actual
                );

        verify(
                "M03",
                MarksComparator.Status.DATA_MISMATCH,
                result
        );

        assertTrue(
                result.differences().stream()
                        .anyMatch(s ->
                                s.contains("diemChu"))
        );
    }

    // =========================================
    // M04 - THIẾU MỘT HỌC PHẦN
    // =========================================

    @Test
    @DisplayName("M04 - Phát hiện thiếu học phần")
    void m04_missingCourse() {

        List<MarkRecord> actual =
                new ArrayList<>(baselineCourses());

        actual.remove(2);

        MarksComparator.Result result =
                comparator.compareCourses(
                        baselineCourses(),
                        actual
                );

        verify(
                "M04",
                MarksComparator.Status.DATA_MISMATCH,
                result
        );

        assertEquals(3, result.expectedCount());
        assertEquals(2, result.actualCount());

        assertFalse(result.missing().isEmpty());
        assertTrue(result.extra().isEmpty());
    }

    // =========================================
    // M05 - TRÙNG VÀ THIẾU HỌC PHẦN
    // =========================================

    @Test
    @DisplayName("M05 - Phát hiện trùng và thiếu học phần")
    void m05_duplicateCourse() {

        List<MarkRecord> actual =
                new ArrayList<>(baselineCourses());

        // Giữ nguyên 3 dòng.
        // DEMO001 xuất hiện hai lần,
        // DEMO002 không còn trong danh sách.
        actual.set(1, actual.get(0));

        MarksComparator.Result result =
                comparator.compareCourses(
                        baselineCourses(),
                        actual
                );

        verify(
                "M05",
                MarksComparator.Status.DATA_MISMATCH,
                result
        );

        assertEquals(3, result.expectedCount());
        assertEquals(3, result.actualCount());

        assertFalse(result.missing().isEmpty());
        assertFalse(result.extra().isEmpty());

        assertEquals(
                1,
                result.missing().values().stream()
                        .mapToInt(Integer::intValue)
                        .sum()
        );

        assertEquals(
                1,
                result.extra().values().stream()
                        .mapToInt(Integer::intValue)
                        .sum()
        );
    }

    // =========================================
    // M06 - ĐẢO THỨ TỰ BẢN GHI
    // =========================================

    @Test
    @DisplayName("M06 - Đảo thứ tự học phần vẫn khớp")
    void m06_reorderedCourses() {

        List<MarkRecord> actual =
                new ArrayList<>(baselineCourses());

        Collections.reverse(actual);

        MarksComparator.Result result =
                comparator.compareCourses(
                        baselineCourses(),
                        actual
                );

        verify(
                "M06",
                MarksComparator.Status.MATCH,
                result
        );

        assertTrue(result.missing().isEmpty());
        assertTrue(result.extra().isEmpty());
    }

    // =========================================
    // M07 - SAI ĐIỂM TRUNG BÌNH HỌC KỲ
    // =========================================

    @Test
    @DisplayName("M07 - Phát hiện sai điểm trung bình học kỳ")
    void m07_wrongSemesterAverage() {

        Map<String, String> expected =
                baselineSummary();

        Map<String, String> actual =
                new LinkedHashMap<>(expected);

        // Cố ý làm sai điểm trung bình hệ 10.
        actual.put(
                "diemTrungBinhHocKyHe10",
                "7.6"
        );

        MarksComparator.Result result =
                comparator.compareSummary(
                        expected,
                        actual
                );

        verify(
                "M07",
                MarksComparator.Status.DATA_MISMATCH,
                result
        );

        assertEquals(5, result.expectedCount());
        assertEquals(5, result.actualCount());

        assertTrue(
                result.differences().stream()
                        .anyMatch(s ->
                                s.contains(
                                        "diemTrungBinhHocKyHe10"))
        );
    }
}
