
package com.hongnhung.cucumberweb.assertions.marks;

import com.hongnhung.cucumberweb.models.marks.MarkRecord;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class MarksComparator {

    public enum Status {
        MATCH,
        DATA_MISMATCH
    }

    public record Result(
            Status status,
            int expectedCount,
            int actualCount,
            Map<MarkRecord, Integer> missing,
            Map<MarkRecord, Integer> extra,
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
    // 1. SO SÁNH DANH SÁCH HỌC PHẦN
    // =========================================

    public Result compareCourses(
            List<MarkRecord> expected,
            List<MarkRecord> actual
    ) {
        Objects.requireNonNull(
                expected,
                "Danh sách Expected không được null."
        );

        Objects.requireNonNull(
                actual,
                "Danh sách Actual không được null."
        );

        List<MarkRecord> normalizedExpected =
                normalizeCourses(expected);

        List<MarkRecord> normalizedActual =
                normalizeCourses(actual);

        Map<MarkRecord, Integer> expectedCounts =
                frequencies(normalizedExpected);

        Map<MarkRecord, Integer> actualCounts =
                frequencies(normalizedActual);

        Map<MarkRecord, Integer> missing =
                subtract(expectedCounts, actualCounts);

        Map<MarkRecord, Integer> extra =
                subtract(actualCounts, expectedCounts);

        boolean match =
                missing.isEmpty() && extra.isEmpty();

        List<String> differences =
                findCourseDifferences(
                        normalizedExpected,
                        normalizedActual
                );

        return new Result(
                match
                        ? Status.MATCH
                        : Status.DATA_MISMATCH,
                expected.size(),
                actual.size(),
                missing,
                extra,
                differences
        );
    }

    // =========================================
    // 2. CHUẨN HÓA BẢN GHI HỌC PHẦN
    // =========================================

    private List<MarkRecord> normalizeCourses(
            List<MarkRecord> records
    ) {
        List<MarkRecord> result = new ArrayList<>();

        for (MarkRecord r : records) {

            Objects.requireNonNull(
                    r,
                    "Danh sách có bản ghi null."
            );

            result.add(new MarkRecord(
                    normalizeText(r.maHocPhan()),
                    normalizeText(r.tenHocPhan()),
                    normalizeNumber(r.tinChi()),
                    normalizeNumber(r.diem10()),
                    normalizeNumber(r.diem4()),
                    normalizeText(r.diemChu()),
                    normalizeText(r.ketQuaHienThi())
            ));
        }

        return List.copyOf(result);
    }

    private String normalizeText(String value) {
        Objects.requireNonNull(
                value,
                "Giá trị trường không được null."
        );

        return Normalizer.normalize(
                value.replace('\u00A0', ' ')
                        .replace('\u202F', ' ')
                        .strip()
                        .replaceAll("\\s+", " "),
                Normalizer.Form.NFC
        );
    }

    private BigDecimal decimal(String value) {
        String normalized =
                normalizeText(value).replace(',', '.');

        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(
                    "Giá trị số không được rỗng."
            );
        }

        try {
            return new BigDecimal(normalized);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(
                    "Giá trị số không hợp lệ: " + value,
                    ex
            );
        }
    }

    private String normalizeNumber(String value) {
        return decimal(value)
                .stripTrailingZeros()
                .toPlainString();
    }

    // =========================================
    // 3. ĐẾM SỐ LẦN XUẤT HIỆN BẢN GHI
    // =========================================

    private Map<MarkRecord, Integer> frequencies(
            List<MarkRecord> records
    ) {
        Map<MarkRecord, Integer> result =
                new LinkedHashMap<>();

        for (MarkRecord record : records) {
            result.merge(
                    record,
                    1,
                    Integer::sum
            );
        }

        return result;
    }

    private Map<MarkRecord, Integer> subtract(
            Map<MarkRecord, Integer> first,
            Map<MarkRecord, Integer> second
    ) {
        Map<MarkRecord, Integer> result =
                new LinkedHashMap<>();

        for (Map.Entry<MarkRecord, Integer> entry :
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
    // 4. PHÂN TÍCH SAI LỆCH TỪNG TRƯỜNG
    // =========================================

    private List<String> findCourseDifferences(
            List<MarkRecord> expected,
            List<MarkRecord> actual
    ) {
        Map<String, List<MarkRecord>> expectedByCode =
                groupByCode(expected);

        Map<String, List<MarkRecord>> actualByCode =
                groupByCode(actual);

        List<String> differences = new ArrayList<>();

        for (Map.Entry<String, List<MarkRecord>> entry :
                expectedByCode.entrySet()) {

            String code = entry.getKey();

            List<MarkRecord> left = entry.getValue();
            List<MarkRecord> right =
                    actualByCode.get(code);

            if (right == null) {
                differences.add(
                        "Thiếu mã học phần: " + code
                );
                continue;
            }

            // Chỉ so sánh từng trường khi mã học phần
            // xuất hiện đúng một lần ở mỗi danh sách.
            if (left.size() != 1 || right.size() != 1) {
                differences.add(
                        "Mã học phần " + code
                                + " xuất hiện nhiều lần; "
                                + "kiểm tra Missing/Extra."
                );
                continue;
            }

            MarkRecord e = left.get(0);
            MarkRecord a = right.get(0);

            addDifference(
                    differences,
                    code,
                    "tenHocPhan",
                    e.tenHocPhan(),
                    a.tenHocPhan()
            );

            addDifference(
                    differences,
                    code,
                    "tinChi",
                    e.tinChi(),
                    a.tinChi()
            );

            addDifference(
                    differences,
                    code,
                    "diem10",
                    e.diem10(),
                    a.diem10()
            );

            addDifference(
                    differences,
                    code,
                    "diem4",
                    e.diem4(),
                    a.diem4()
            );

            addDifference(
                    differences,
                    code,
                    "diemChu",
                    e.diemChu(),
                    a.diemChu()
            );

            addDifference(
                    differences,
                    code,
                    "ketQuaHienThi",
                    e.ketQuaHienThi(),
                    a.ketQuaHienThi()
            );
        }

        for (String code : actualByCode.keySet()) {
            if (!expectedByCode.containsKey(code)) {
                differences.add(
                        "Thừa mã học phần: " + code
                );
            }
        }

        return differences;
    }

    private Map<String, List<MarkRecord>> groupByCode(
            List<MarkRecord> records
    ) {
        Map<String, List<MarkRecord>> result =
                new LinkedHashMap<>();

        for (MarkRecord record : records) {

            result.computeIfAbsent(
                    record.maHocPhan(),
                    key -> new ArrayList<>()
            ).add(record);
        }

        return result;
    }

    private void addDifference(
            List<String> differences,
            String code,
            String field,
            String expected,
            String actual
    ) {
        if (!Objects.equals(expected, actual)) {

            differences.add(
                    code + " / " + field
                            + ": Expected=[" + expected
                            + "], Actual=[" + actual + "]"
            );
        }
    }

    // =========================================
    // 5. SO SÁNH CHỈ TIÊU TỔNG HỢP
    // =========================================

    public Result compareSummary(
            Map<String, String> expected,
            Map<String, String> actual
    ) {
        Objects.requireNonNull(
                expected,
                "Summary Expected không được null."
        );

        Objects.requireNonNull(
                actual,
                "Summary Actual không được null."
        );

        List<String> differences =
                new ArrayList<>();

        Set<String> allKeys = new LinkedHashSet<>();
        allKeys.addAll(expected.keySet());
        allKeys.addAll(actual.keySet());

        for (String key : allKeys) {

            if (!expected.containsKey(key)) {

                differences.add(
                        "Thừa chỉ tiêu: " + key
                );
                continue;
            }

            if (!actual.containsKey(key)) {

                differences.add(
                        "Thiếu chỉ tiêu: " + key
                );
                continue;
            }

            String expectedValue = expected.get(key);
            String actualValue = actual.get(key);

            try {

                BigDecimal e = decimal(expectedValue);
                BigDecimal a = decimal(actualValue);

                if (e.compareTo(a) != 0) {

                    differences.add(
                            key
                                    + ": Expected=["
                                    + expectedValue
                                    + "], Actual=["
                                    + actualValue + "]"
                    );
                }

            } catch (IllegalArgumentException ex) {

                differences.add(
                        key + ": Giá trị số không hợp lệ."
                                + " Expected=["
                                + expectedValue
                                + "], Actual=["
                                + actualValue + "]"
                );
            }
        }

        return new Result(
                differences.isEmpty()
                        ? Status.MATCH
                        : Status.DATA_MISMATCH,
                expected.size(),
                actual.size(),
                Map.of(),
                Map.of(),
                differences
        );
    }
}
