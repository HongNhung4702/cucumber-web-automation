
package com.hongnhung.cucumberweb.assertions.unaccumulated;

import com.hongnhung.cucumberweb.models.unaccumulated.UnaccumulatedCourseRecord;

import java.math.BigInteger;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class UnaccumulatedCourseComparator {

    public enum Status {
        MATCH,
        DATA_MISMATCH
    }

    public record Result(
            Status status,
            int expectedCount,
            int actualCount,
            Map<UnaccumulatedCourseRecord, Integer> missing,
            Map<UnaccumulatedCourseRecord, Integer> extra,
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
    // 1. SO SÁNH TOÀN BỘ DỮ LIỆU
    // =========================================

    public Result compare(
            List<UnaccumulatedCourseRecord> expected,
            List<UnaccumulatedCourseRecord> actual
    ) {

        Objects.requireNonNull(
                expected,
                "Expected không được null."
        );

        Objects.requireNonNull(
                actual,
                "Actual không được null."
        );

        List<UnaccumulatedCourseRecord> left =
                normalizeRecords(expected);

        List<UnaccumulatedCourseRecord> right =
                normalizeRecords(actual);

        Map<UnaccumulatedCourseRecord, Integer> leftCounts =
                frequencies(left);

        Map<UnaccumulatedCourseRecord, Integer> rightCounts =
                frequencies(right);

        Map<UnaccumulatedCourseRecord, Integer> missing =
                subtract(leftCounts, rightCounts);

        Map<UnaccumulatedCourseRecord, Integer> extra =
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

    private List<UnaccumulatedCourseRecord> normalizeRecords(
            List<UnaccumulatedCourseRecord> records
    ) {

        List<UnaccumulatedCourseRecord> result =
                new ArrayList<>();

        for (UnaccumulatedCourseRecord r : records) {

            Objects.requireNonNull(
                    r,
                    "Danh sách chứa bản ghi null."
            );

            result.add(new UnaccumulatedCourseRecord(
                    text(r.maHocPhan()),
                    text(r.tenHocPhan()),
                    number(r.soTC()),
                    number(r.soTietLT()),
                    number(r.soTietTH()),
                    number(r.soTietTL()),
                    text(r.hocPhanTuongDuong()),
                    text(r.hocPhanHocTruoc()),
                    text(r.hocPhanThayThe())
            ));
        }

        return List.copyOf(result);
    }

    private String text(String value) {

        Objects.requireNonNull(
                value,
                "Trường dữ liệu không được null."
        );

        String normalized = value
                .replace('\u00A0', ' ')
                .replace('\u202F', ' ')
                .strip()
                .replaceAll("\\s+", " ");

        return Normalizer.normalize(
                normalized,
                Normalizer.Form.NFC
        );
    }

    private String number(String value) {

        String normalized = text(value);

        if (!normalized.matches("\\d+")) {
            throw new IllegalArgumentException(
                    "Giá trị số không hợp lệ: ["
                            + value + "]"
            );
        }

        return new BigInteger(normalized).toString();
    }

    // =========================================
    // 3. SO SÁNH MULTISET
    // =========================================

    private Map<UnaccumulatedCourseRecord, Integer> frequencies(
            List<UnaccumulatedCourseRecord> records
    ) {

        Map<UnaccumulatedCourseRecord, Integer> result =
                new LinkedHashMap<>();

        for (UnaccumulatedCourseRecord record : records) {
            result.merge(record, 1, Integer::sum);
        }

        return result;
    }

    private Map<UnaccumulatedCourseRecord, Integer> subtract(
            Map<UnaccumulatedCourseRecord, Integer> first,
            Map<UnaccumulatedCourseRecord, Integer> second
    ) {

        Map<UnaccumulatedCourseRecord, Integer> result =
                new LinkedHashMap<>();

        for (Map.Entry<UnaccumulatedCourseRecord, Integer> entry :
                first.entrySet()) {

            int difference = entry.getValue()
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
    // 4. ĐẾM HỌC PHẦN VÀ TÍNH TÍN CHỈ
    // =========================================

    public int countCourses(
            List<UnaccumulatedCourseRecord> records
    ) {

        Objects.requireNonNull(records);

        Set<String> codes = new LinkedHashSet<>();

        for (UnaccumulatedCourseRecord r : records) {
            Objects.requireNonNull(r);
            codes.add(text(r.maHocPhan()));
        }

        return codes.size();
    }

    public int totalCredits(
            List<UnaccumulatedCourseRecord> records
    ) {

        Objects.requireNonNull(records);

        BigInteger sum = BigInteger.ZERO;

        for (UnaccumulatedCourseRecord r : records) {

            Objects.requireNonNull(r);

            sum = sum.add(
                    new BigInteger(number(r.soTC()))
            );
        }

        return sum.intValueExact();
    }

    // =========================================
    // 5. MÔ TẢ SAI LỆCH THEO MÃ HỌC PHẦN
    // =========================================

    private List<String> findDifferences(
            List<UnaccumulatedCourseRecord> expected,
            List<UnaccumulatedCourseRecord> actual
    ) {

        Map<String, Integer> expectedKeys =
                countKeys(expected);

        Map<String, Integer> actualKeys =
                countKeys(actual);

        Map<String, UnaccumulatedCourseRecord> expectedMap =
                uniqueByKey(expected, expectedKeys);

        Map<String, UnaccumulatedCourseRecord> actualMap =
                uniqueByKey(actual, actualKeys);

        Set<String> keys = new LinkedHashSet<>();

        keys.addAll(expectedKeys.keySet());
        keys.addAll(actualKeys.keySet());

        List<String> differences =
                new ArrayList<>();

        for (String key : keys) {

            int expectedCount =
                    expectedKeys.getOrDefault(key, 0);

            int actualCount =
                    actualKeys.getOrDefault(key, 0);

            if (expectedCount > 1 || actualCount > 1) {

                differences.add(
                        "Mã học phần trùng: "
                                + key
                                + " | Expected="
                                + expectedCount
                                + ", Actual="
                                + actualCount
                );

                continue;
            }

            UnaccumulatedCourseRecord e =
                    expectedMap.get(key);

            UnaccumulatedCourseRecord a =
                    actualMap.get(key);

            if (e == null) {

                differences.add(
                        "Thừa học phần: " + key
                );

                continue;
            }

            if (a == null) {

                differences.add(
                        "Thiếu học phần: " + key
                );

                continue;
            }

            compareFields(
                    differences,
                    key,
                    e,
                    a
            );
        }

        return differences;
    }

    private Map<String, Integer> countKeys(
            List<UnaccumulatedCourseRecord> records
    ) {

        Map<String, Integer> result =
                new LinkedHashMap<>();

        for (UnaccumulatedCourseRecord r : records) {

            String code = r.maHocPhan();

            result.merge(
                    code,
                    1,
                    Integer::sum
            );
        }

        return result;
    }

    private Map<String, UnaccumulatedCourseRecord> uniqueByKey(
            List<UnaccumulatedCourseRecord> records,
            Map<String, Integer> counts
    ) {

        Map<String, UnaccumulatedCourseRecord> result =
                new LinkedHashMap<>();

        for (UnaccumulatedCourseRecord r : records) {

            String code = r.maHocPhan();

            if (counts.getOrDefault(code, 0) == 1) {
                result.put(code, r);
            }
        }

        return result;
    }

    private void compareFields(
            List<String> differences,
            String code,
            UnaccumulatedCourseRecord expected,
            UnaccumulatedCourseRecord actual
    ) {

        String[] names = {
                "maHocPhan",
                "tenHocPhan",
                "soTC",
                "soTietLT",
                "soTietTH",
                "soTietTL",
                "hocPhanTuongDuong",
                "hocPhanHocTruoc",
                "hocPhanThayThe"
        };

        String[] left = values(expected);
        String[] right = values(actual);

        for (int i = 0; i < names.length; i++) {

            if (!Objects.equals(
                    left[i],
                    right[i]
            )) {

                differences.add(
                        code
                                + " / " + names[i]
                                + ": Expected=["
                                + left[i]
                                + "], Actual=["
                                + right[i]
                                + "]"
                );
            }
        }
    }

    private String[] values(
            UnaccumulatedCourseRecord r
    ) {

        return new String[] {
                r.maHocPhan(),
                r.tenHocPhan(),
                r.soTC(),
                r.soTietLT(),
                r.soTietTH(),
                r.soTietTL(),
                r.hocPhanTuongDuong(),
                r.hocPhanHocTruoc(),
                r.hocPhanThayThe()
        };
    }
}
