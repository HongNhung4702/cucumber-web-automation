
package com.hongnhung.cucumberweb.assertions.exams;

import com.hongnhung.cucumberweb.models.exams.ExamRecord;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class ExamScheduleComparator {

    public enum Status {
        MATCH, DATA_MISMATCH, LOAD_FAILED
    }

    public record Result(
            Status status,
            int expectedCount,
            int actualCount,
            Map<ExamRecord, Integer> missing,
            Map<ExamRecord, Integer> extra,
            List<String> fieldDifferences,
            boolean unicodeNormalized
    ) {
        public Result {
            missing = Collections.unmodifiableMap(
                    new LinkedHashMap<>(missing));
            extra = Collections.unmodifiableMap(
                    new LinkedHashMap<>(extra));
            fieldDifferences = List.copyOf(fieldDifferences);
        }

        public boolean passed() {
            return status == Status.MATCH;
        }

        public String details() {
            return "Status=" + status
                    + ", Expected=" + expectedCount
                    + ", Actual=" + (actualCount < 0
                    ? "NOT_LOADED" : actualCount)
                    + ", Missing=" + missing
                    + ", Extra=" + extra
                    + ", FieldDifferences=" + fieldDifferences
                    + ", NFC_normalization_needed="
                    + unicodeNormalized;
        }
    }

    public Result compare(
            List<ExamRecord> expected,
            List<ExamRecord> actual
    ) {
        Objects.requireNonNull(
                expected, "Baseline Expected không được null");

        if (actual == null) {
            return new Result(
                    Status.LOAD_FAILED,
                    expected.size(),
                    -1,
                    Map.of(),
                    Map.of(),
                    List.of("Không tải được danh sách Actual"),
                    false
            );
        }

        Map<ExamRecord, Integer> rawExpected =
                counts(expected);
        Map<ExamRecord, Integer> rawActual =
                counts(actual);

        List<ExamRecord> normalizedExpected =
                normalize(expected);
        List<ExamRecord> normalizedActual =
                normalize(actual);

        Map<ExamRecord, Integer> expectedCounts =
                counts(normalizedExpected);
        Map<ExamRecord, Integer> actualCounts =
                counts(normalizedActual);

        Map<ExamRecord, Integer> missing =
                subtract(expectedCounts, actualCounts);
        Map<ExamRecord, Integer> extra =
                subtract(actualCounts, expectedCounts);

        boolean match =
                missing.isEmpty() && extra.isEmpty();

        boolean unicodeNormalized =
                match && !rawExpected.equals(rawActual);

        return new Result(
                match ? Status.MATCH : Status.DATA_MISMATCH,
                expected.size(),
                actual.size(),
                missing,
                extra,
                findFieldDifferences(
                        normalizedExpected,
                        normalizedActual
                ),
                unicodeNormalized
        );
    }

    private List<ExamRecord> normalize(
            List<ExamRecord> records
    ) {
        List<ExamRecord> output = new ArrayList<>();

        for (ExamRecord r : records) {
            Objects.requireNonNull(
                    r, "Danh sách chứa bản ghi null");

            output.add(new ExamRecord(
                    nfc(r.maLopHocPhan()),
                    nfc(r.tenLopHocPhan()),
                    nfc(r.lanThi()),
                    nfc(r.ngayThi()),
                    nfc(r.gioThi()),
                    nfc(r.phongThi()),
                    nfc(r.diaDiem()),
                    nfc(r.hinhThucThi()),
                    nfc(r.kyThi()),
                    nfc(r.ghiChu()),
                    nfc(r.vangThi())
            ));
        }

        return output;
    }

    private String nfc(String value) {
        return Normalizer.normalize(
                Objects.requireNonNull(
                        value, "Giá trị trường không được null"),
                Normalizer.Form.NFC
        );
    }

    private Map<ExamRecord, Integer> counts(
            List<ExamRecord> records
    ) {
        Map<ExamRecord, Integer> output =
                new LinkedHashMap<>();

        for (ExamRecord record : records) {
            output.merge(
                    Objects.requireNonNull(record),
                    1,
                    Integer::sum
            );
        }

        return output;
    }

    private Map<ExamRecord, Integer> subtract(
            Map<ExamRecord, Integer> first,
            Map<ExamRecord, Integer> second
    ) {
        Map<ExamRecord, Integer> difference =
                new LinkedHashMap<>();

        for (Map.Entry<ExamRecord, Integer> entry
                : first.entrySet()) {

            int delta = entry.getValue()
                    - second.getOrDefault(
                    entry.getKey(), 0);

            if (delta > 0) {
                difference.put(
                        entry.getKey(), delta);
            }
        }

        return difference;
    }

    private List<String> findFieldDifferences(
            List<ExamRecord> expected,
            List<ExamRecord> actual
    ) {
        Map<String, List<ExamRecord>> byKeyExpected =
                byKey(expected);
        Map<String, List<ExamRecord>> byKeyActual =
                byKey(actual);

        List<String> output = new ArrayList<>();

        for (Map.Entry<String, List<ExamRecord>> entry
                : byKeyExpected.entrySet()) {

            List<ExamRecord> left = entry.getValue();
            List<ExamRecord> right =
                    byKeyActual.get(entry.getKey());

            // Không tự ghép các bản ghi có khóa mơ hồ.
            if (right == null
                    || left.size() != 1
                    || right.size() != 1) {
                continue;
            }

            ExamRecord e = left.get(0);
            ExamRecord a = right.get(0);
            String key = entry.getKey();

            appendIfDifferent(output, key,
                    "tenLopHocPhan",
                    e.tenLopHocPhan(), a.tenLopHocPhan());

            appendIfDifferent(output, key,
                    "ngayThi", e.ngayThi(), a.ngayThi());

            appendIfDifferent(output, key,
                    "gioThi", e.gioThi(), a.gioThi());

            appendIfDifferent(output, key,
                    "phongThi", e.phongThi(), a.phongThi());

            appendIfDifferent(output, key,
                    "diaDiem", e.diaDiem(), a.diaDiem());

            appendIfDifferent(output, key,
                    "hinhThucThi",
                    e.hinhThucThi(), a.hinhThucThi());

            appendIfDifferent(output, key,
                    "kyThi", e.kyThi(), a.kyThi());

            appendIfDifferent(output, key,
                    "ghiChu", e.ghiChu(), a.ghiChu());

            appendIfDifferent(output, key,
                    "vangThi", e.vangThi(), a.vangThi());
        }

        return output;
    }

    private Map<String, List<ExamRecord>> byKey(
            List<ExamRecord> records
    ) {
        Map<String, List<ExamRecord>> result =
                new LinkedHashMap<>();

        for (ExamRecord r : records) {
            String key = r.maLopHocPhan()
                    + " | " + r.lanThi();

            result.computeIfAbsent(
                    key, unused -> new ArrayList<>()
            ).add(r);
        }

        return result;
    }

    private void appendIfDifferent(
            List<String> output,
            String key,
            String field,
            String expected,
            String actual
    ) {
        if (!expected.equals(actual)) {
            output.add(
                    key + " / " + field
                            + ": Expected=[" + expected
                            + "], Actual=[" + actual + "]"
            );
        }
    }
}
