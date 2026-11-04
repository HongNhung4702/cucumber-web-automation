
package com.hongnhung.cucumberweb.assertions.exams;

import com.hongnhung.cucumberweb.models.exams.ExamRecord;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public final class ExamScheduleComparatorTest {

    private final ExamScheduleComparator comparator =
            new ExamScheduleComparator();

    // =========================================
    // 1. DỮ LIỆU GIẢ LẬP
    // =========================================

    // Dữ liệu giả lập, không phải dữ liệu của nhà trường.
    private List<ExamRecord> baseline() {

        return List.of(
                new ExamRecord(
                        "DEMO001", "Triết học Mác - Lênin",
                        "Lần 1", "08/01/2023", "08h15",
                        "A5.14", "Địa điểm mẫu",
                        "Trắc nghiệm", "Kết thúc học phần",
                        "", ""
                ),
                new ExamRecord(
                        "DEMO002", "Toán logic",
                        "Lần 1", "09/01/2023", "15h45",
                        "A3.202", "Địa điểm mẫu",
                        "Tự luận", "Kết thúc học phần",
                        "", ""
                ),
                new ExamRecord(
                        "DEMO003", "Tiếng Anh 1",
                        "Lần 1", "10/01/2023", "09h15",
                        "A3.301", "Địa điểm mẫu",
                        "Tự luận", "Kết thúc học phần",
                        "", ""
                )
        );
    }

    private void verify(
            String id,
            ExamScheduleComparator.Status expectedStatus,
            ExamScheduleComparator.Result result
    ) {

        System.out.println(
                "[" + id + "] " + result.details()
        );

        assertEquals(
                expectedStatus,
                result.status(),
                id + " không cho kết quả mong đợi"
        );

        assertEquals(
                expectedStatus
                        == ExamScheduleComparator.Status.MATCH,
                result.passed(),
                id + " sai trạng thái PASS/FAIL"
        );
    }

    // =========================================
    // E01 - DỮ LIỆU KHỚP
    // =========================================

    @Test
    @DisplayName("E01 - Dữ liệu đúng, kể cả Unicode NFC/NFD")
    void e01_matchingDataPasses() {

        List<ExamRecord> actual =
                new ArrayList<>(baseline());

        ExamRecord r = actual.get(1);

        actual.set(1, new ExamRecord(
                r.maLopHocPhan(),
                "Toa\u0301n logic",
                r.lanThi(),
                r.ngayThi(),
                r.gioThi(),
                r.phongThi(),
                r.diaDiem(),
                r.hinhThucThi(),
                r.kyThi(),
                r.ghiChu(),
                r.vangThi()
        ));

        var result = comparator.compare(
                baseline(), actual
        );

        verify(
                "E01",
                ExamScheduleComparator.Status.MATCH,
                result
        );

        assertTrue(result.unicodeNormalized());
    }

    // =========================================
    // E02 - SAI NGÀY THI
    // =========================================

    @Test
    @DisplayName("E02 - Sai ngày thi")
    void e02_wrongDateDetected() {

        List<ExamRecord> actual =
                new ArrayList<>(baseline());

        ExamRecord r = actual.get(0);

        actual.set(0, new ExamRecord(
                r.maLopHocPhan(),
                r.tenLopHocPhan(),
                r.lanThi(),
                "11/01/2023",
                r.gioThi(),
                r.phongThi(),
                r.diaDiem(),
                r.hinhThucThi(),
                r.kyThi(),
                r.ghiChu(),
                r.vangThi()
        ));

        var result = comparator.compare(
                baseline(), actual
        );

        verify(
                "E02",
                ExamScheduleComparator.Status.DATA_MISMATCH,
                result
        );

        assertEquals(3, result.actualCount());

        assertTrue(
                result.details().contains("ngayThi")
        );
    }

    // =========================================
    // E03 - THIẾU BẢN GHI
    // =========================================

    @Test
    @DisplayName("E03 - Thiếu một bản ghi")
    void e03_missingRecordDetected() {

        List<ExamRecord> actual =
                new ArrayList<>(baseline());

        actual.remove(2);

        var result = comparator.compare(
                baseline(), actual
        );

        verify(
                "E03",
                ExamScheduleComparator.Status.DATA_MISMATCH,
                result
        );

        assertEquals(2, result.actualCount());

        assertFalse(
                result.missing().isEmpty()
        );
    }

    // =========================================
    // E04 - THỪA BẢN GHI
    // =========================================

    @Test
    @DisplayName("E04 - Thừa một bản ghi")
    void e04_extraRecordDetected() {

        List<ExamRecord> actual =
                new ArrayList<>(baseline());

        actual.add(new ExamRecord(
                "DEMO004", "Học phần giả lập",
                "Lần 1", "12/01/2023", "13h15",
                "A2.307", "Địa điểm mẫu",
                "Tự luận", "Kết thúc học phần",
                "", ""
        ));

        var result = comparator.compare(
                baseline(), actual
        );

        verify(
                "E04",
                ExamScheduleComparator.Status.DATA_MISMATCH,
                result
        );

        assertEquals(4, result.actualCount());

        assertFalse(
                result.extra().isEmpty()
        );
    }

    // =========================================
    // E05 - SAI PHÒNG THI
    // =========================================

    @Test
    @DisplayName("E05 - Sai phòng thi")
    void e05_wrongRoomDetected() {

        List<ExamRecord> actual =
                new ArrayList<>(baseline());

        ExamRecord r = actual.get(1);

        actual.set(1, new ExamRecord(
                r.maLopHocPhan(),
                r.tenLopHocPhan(),
                r.lanThi(),
                r.ngayThi(),
                r.gioThi(),
                "B9.999",
                r.diaDiem(),
                r.hinhThucThi(),
                r.kyThi(),
                r.ghiChu(),
                r.vangThi()
        ));

        var result = comparator.compare(
                baseline(), actual
        );

        verify(
                "E05",
                ExamScheduleComparator.Status.DATA_MISMATCH,
                result
        );

        assertTrue(
                result.details().contains("phongThi")
        );
    }

    // =========================================
    // E06 - ĐẢO THỨ TỰ
    // =========================================

    @Test
    @DisplayName("E06 - Thay đổi thứ tự dòng")
    void e06_reorderStillPasses() {

        List<ExamRecord> actual =
                new ArrayList<>(baseline());

        Collections.reverse(actual);

        var result = comparator.compare(
                baseline(), actual
        );

        verify(
                "E06",
                ExamScheduleComparator.Status.MATCH,
                result
        );
    }

    // =========================================
    // E07 - TRÙNG VÀ THIẾU BẢN GHI
    // =========================================

    @Test
    @DisplayName("E07 - Trùng bản ghi và thiếu bản ghi")
    void e07_duplicateWithMissingDetected() {

        List<ExamRecord> actual =
                new ArrayList<>(baseline());

        // Tổng vẫn là 3 dòng nhưng một dòng bị lặp.
        actual.set(1, actual.get(0));

        var result = comparator.compare(
                baseline(), actual
        );

        verify(
                "E07",
                ExamScheduleComparator.Status.DATA_MISMATCH,
                result
        );

        assertEquals(3, result.actualCount());

        assertFalse(
                result.missing().isEmpty()
        );

        assertFalse(
                result.extra().isEmpty()
        );
    }

    // =========================================
    // E08 - KHÔNG TẢI ĐƯỢC DỮ LIỆU
    // =========================================

    @Test
    @DisplayName("E08 - Không tải được dữ liệu")
    void e08_loadFailedDetected() {

        // null biểu diễn lỗi thu thập dữ liệu.
        var result = comparator.compare(
                baseline(), null
        );

        verify(
                "E08",
                ExamScheduleComparator.Status.LOAD_FAILED,
                result
        );

        assertEquals(-1, result.actualCount());

        assertTrue(
                result.details().contains("NOT_LOADED")
        );

        // Bảng đã tải nhưng rỗng khác với lỗi tải.
        var emptyButLoaded = comparator.compare(
                baseline(), List.of()
        );

        assertEquals(
                ExamScheduleComparator.Status.DATA_MISMATCH,
                emptyButLoaded.status()
        );
    }
}
