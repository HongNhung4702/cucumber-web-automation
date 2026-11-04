
package com.hongnhung.cucumberweb.assertions.schedules;

import com.hongnhung.cucumberweb.models.schedules.ScheduleRecord;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class SchedulesComparator {

    public enum Status {
        MATCH,
        DATA_MISMATCH
    }

    public record Result(
            Status status,
            int expectedCount,
            int actualCount,
            Map<ScheduleRecord, Integer> missing,
            Map<ScheduleRecord, Integer> extra,
            List<String> differences
    ) {
        public Result {
            missing = Collections.unmodifiableMap(
                    new LinkedHashMap<>(missing)
            );
            extra = Collections.unmodifiableMap(
                    new LinkedHashMap<>(extra)
            );
            differences = List.copyOf(differences);
        }

        public boolean passed() {
            return status == Status.MATCH;
        }

        public String details() {
            return "Status=" + status
                    + ", Expected=" + expectedCount
                    + ", Actual=" + actualCount
                    + ", Missing=" + missing
                    + ", Extra=" + extra
                    + ", Differences=" + differences;
        }
    }

    // =========================================
    // 1. ĐỐI CHIẾU DANH SÁCH LỊCH HỌC
    // =========================================

    public Result compare(
            List<ScheduleRecord> expected,
            List<ScheduleRecord> actual
    ) {
        Objects.requireNonNull(
                expected,
                "Expected không được null."
        );

        Objects.requireNonNull(
                actual,
                "Actual không được null."
        );

        List<ScheduleRecord> left =
                normalizeRecords(expected);

        List<ScheduleRecord> right =
                normalizeRecords(actual);

        Map<ScheduleRecord, Integer> leftCounts =
                frequencies(left);

        Map<ScheduleRecord, Integer> rightCounts =
                frequencies(right);

        Map<ScheduleRecord, Integer> missing =
                subtract(leftCounts, rightCounts);

        Map<ScheduleRecord, Integer> extra =
                subtract(rightCounts, leftCounts);

        List<String> differences =
                findDifferences(left, right);

        Status status =
                missing.isEmpty() && extra.isEmpty()
                        ? Status.MATCH
                        : Status.DATA_MISMATCH;

        return new Result(
                status,
                expected.size(),
                actual.size(),
                missing,
                extra,
                differences
        );
    }

    // =========================================
    // 2. CHUẨN HÓA DỮ LIỆU
    // =========================================

    private List<ScheduleRecord> normalizeRecords(
            List<ScheduleRecord> records
    ) {
        List<ScheduleRecord> result =
                new ArrayList<>();

        for (ScheduleRecord r : records) {

            Objects.requireNonNull(
                    r,
                    "Bản ghi không được null."
            );

            result.add(new ScheduleRecord(
                    number(r.sttHocPhan()),
                    text(r.maLopHocPhan()),
                    text(r.tenHocPhan()),
                    text(r.tenGiaoVien()),
                    number(r.soTC()),
                    text(r.thu()),
                    text(r.tietHoc()),
                    text(r.phongHoc()),
                    text(r.tuan()),
                    text(r.tuNgayDenNgay()),
                    text(r.phongDiaDiem()),
                    text(r.diaChi())
            ));
        }

        return List.copyOf(result);
    }

    private String text(String value) {

        Objects.requireNonNull(
                value,
                "Giá trị không được null."
        );

        return Normalizer.normalize(
                value.replace('\u00A0', ' ')
                        .replace('\u202F', ' ')
                        .strip()
                        .replaceAll("\\s+", " "),
                Normalizer.Form.NFC
        );
    }

    private String number(String value) {

        String normalized = text(value);

        if (!normalized.matches("\\d+")) {
            throw new IllegalArgumentException(
                    "Giá trị số nguyên không hợp lệ: "
                            + value
            );
        }

        return new java.math.BigInteger(normalized)
                .toString();
    }

    // =========================================
    // 3. SO SÁNH ĐA TẬP HỢP
    // =========================================

    private Map<ScheduleRecord, Integer> frequencies(
            List<ScheduleRecord> records
    ) {
        Map<ScheduleRecord, Integer> result =
                new LinkedHashMap<>();

        for (ScheduleRecord record : records) {
            result.merge(record, 1, Integer::sum);
        }

        return result;
    }

    private Map<ScheduleRecord, Integer> subtract(
            Map<ScheduleRecord, Integer> first,
            Map<ScheduleRecord, Integer> second
    ) {
        Map<ScheduleRecord, Integer> result =
                new LinkedHashMap<>();

        for (Map.Entry<ScheduleRecord, Integer> entry :
                first.entrySet()) {

            int difference =
                    entry.getValue()
                            - second.getOrDefault(
                            entry.getKey(), 0
                    );

            if (difference > 0) {
                result.put(
                        entry.getKey(),
                        difference
                );
            }
        }

        return result;
    }

    // =========================================
    // 4. XÁC ĐỊNH SAI LỆCH TỪNG TRƯỜNG
    // =========================================

    private List<String> findDifferences(
            List<ScheduleRecord> expected,
            List<ScheduleRecord> actual
    ) {
        Map<String, ScheduleRecord> expectedMap =
                uniqueByKey(expected);

        Map<String, ScheduleRecord> actualMap =
                uniqueByKey(actual);

        Set<String> keys = new LinkedHashSet<>();
        keys.addAll(expectedMap.keySet());
        keys.addAll(actualMap.keySet());

        List<String> differences =
                new ArrayList<>();

        for (String key : keys) {

            ScheduleRecord e = expectedMap.get(key);
            ScheduleRecord a = actualMap.get(key);

            if (e == null) {
                differences.add(
                        "Thừa lịch học: " + key
                );
                continue;
            }

            if (a == null) {
                differences.add(
                        "Thiếu lịch học: " + key
                );
                continue;
            }

            compareField(
                    differences, key, "sttHocPhan",
                    e.sttHocPhan(), a.sttHocPhan()
            );

            compareField(
                    differences, key, "maLopHocPhan",
                    e.maLopHocPhan(), a.maLopHocPhan()
            );

            compareField(
                    differences, key, "tenHocPhan",
                    e.tenHocPhan(), a.tenHocPhan()
            );

            compareField(
                    differences, key, "tenGiaoVien",
                    e.tenGiaoVien(), a.tenGiaoVien()
            );

            compareField(
                    differences, key, "soTC",
                    e.soTC(), a.soTC()
            );

            compareField(
                    differences, key, "thu",
                    e.thu(), a.thu()
            );

            compareField(
                    differences, key, "tietHoc",
                    e.tietHoc(), a.tietHoc()
            );

            compareField(
                    differences, key, "phongHoc",
                    e.phongHoc(), a.phongHoc()
            );

            compareField(
                    differences, key, "tuan",
                    e.tuan(), a.tuan()
            );

            compareField(
                    differences, key, "tuNgayDenNgay",
                    e.tuNgayDenNgay(), a.tuNgayDenNgay()
            );

            compareField(
                    differences, key, "phongDiaDiem",
                    e.phongDiaDiem(), a.phongDiaDiem()
            );

            compareField(
                    differences, key, "diaChi",
                    e.diaChi(), a.diaChi()
            );
        }

        return differences;
    }

    // Mã học phần + thứ + tiết + khoảng ngày
    // là khóa phục vụ phân tích sai lệch.
    // Kết quả MATCH vẫn do multiset quyết định.
    private String key(ScheduleRecord r) {
        return r.maLopHocPhan()
                + " | " + r.thu()
                + " | " + r.tietHoc()
                + " | " + r.tuNgayDenNgay();
    }

    private Map<String, ScheduleRecord> uniqueByKey(
            List<ScheduleRecord> records
    ) {
        Map<String, ScheduleRecord> result =
                new LinkedHashMap<>();

        Set<String> duplicatedKeys =
                new LinkedHashSet<>();

        for (ScheduleRecord r : records) {

            String key = key(r);

            if (result.containsKey(key)) {
                duplicatedKeys.add(key);
            } else {
                result.put(key, r);
            }
        }

        // Khóa trùng không dùng để ghép từng trường,
        // tránh kết luận sai về cặp bản ghi.
        for (String duplicated : duplicatedKeys) {
            result.remove(duplicated);
        }

        return result;
    }

    private void compareField(
            List<String> differences,
            String key,
            String field,
            String expected,
            String actual
    ) {
        if (!Objects.equals(expected, actual)) {

            differences.add(
                    key + " / " + field
                            + ": Expected=[" + expected
                            + "], Actual=[" + actual + "]"
            );
        }
    }

    // =========================================
    // 5. ĐẾM SỐ LỚP HỌC PHẦN
    // =========================================

    public int countCourses(
            List<ScheduleRecord> records
    ) {
        Objects.requireNonNull(records);

        Set<String> codes = new LinkedHashSet<>();

        for (ScheduleRecord record : records) {

            Objects.requireNonNull(record);

            codes.add(text(record.maLopHocPhan()));
        }

        return codes.size();
    }
}
