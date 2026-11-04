
package com.hongnhung.cucumberweb.assertions.lecturers;

import com.hongnhung.cucumberweb.models.lecturers.LecturerScheduleRecord;

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

public final class LecturerScheduleComparator {

    public enum Status {
        MATCH,
        DATA_MISMATCH
    }

    public record Result(
            Status status,
            int expectedCount,
            int actualCount,
            Map<LecturerScheduleRecord, Integer> missing,
            Map<LecturerScheduleRecord, Integer> extra,
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

    public Result compare(
            List<LecturerScheduleRecord> expected,
            List<LecturerScheduleRecord> actual
    ) {

        Objects.requireNonNull(expected, "Expected null.");
        Objects.requireNonNull(actual, "Actual null.");

        List<LecturerScheduleRecord> left =
                normalizeRecords(expected);

        List<LecturerScheduleRecord> right =
                normalizeRecords(actual);

        Map<LecturerScheduleRecord, Integer> leftCounts =
                frequencies(left);

        Map<LecturerScheduleRecord, Integer> rightCounts =
                frequencies(right);

        Map<LecturerScheduleRecord, Integer> missing =
                subtract(leftCounts, rightCounts);

        Map<LecturerScheduleRecord, Integer> extra =
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

    private List<LecturerScheduleRecord> normalizeRecords(
            List<LecturerScheduleRecord> records
    ) {

        List<LecturerScheduleRecord> result =
                new ArrayList<>();

        for (LecturerScheduleRecord r : records) {

            Objects.requireNonNull(
                    r,
                    "Danh sách chứa bản ghi null."
            );

            result.add(new LecturerScheduleRecord(
                    text(r.maLHP()),
                    text(r.tenHocPhan()),
                    number(r.soTietLT()),
                    number(r.soTietTH()),
                    number(r.soTietTL()),
                    number(r.soTC()),
                    text(r.maLop()),
                    text(r.tenCBGV()),
                    number(r.siSo()),
                    text(r.thu()),
                    text(r.tiet()),
                    text(r.phong()),
                    text(r.tuanHoc())
            ));
        }

        return List.copyOf(result);
    }

    private String text(String value) {

        Objects.requireNonNull(
                value,
                "Giá trị dữ liệu không được null."
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
                    "Giá trị số không hợp lệ: " + value
            );
        }

        return new BigInteger(normalized).toString();
    }

    private Map<LecturerScheduleRecord, Integer> frequencies(
            List<LecturerScheduleRecord> records
    ) {

        Map<LecturerScheduleRecord, Integer> result =
                new LinkedHashMap<>();

        for (LecturerScheduleRecord record : records) {
            result.merge(record, 1, Integer::sum);
        }

        return result;
    }

    private Map<LecturerScheduleRecord, Integer> subtract(
            Map<LecturerScheduleRecord, Integer> first,
            Map<LecturerScheduleRecord, Integer> second
    ) {

        Map<LecturerScheduleRecord, Integer> result =
                new LinkedHashMap<>();

        for (Map.Entry<LecturerScheduleRecord, Integer> e :
                first.entrySet()) {

            int difference = e.getValue()
                    - second.getOrDefault(e.getKey(), 0);

            if (difference > 0) {
                result.put(e.getKey(), difference);
            }
        }

        return result;
    }

    public int countCourses(
            List<LecturerScheduleRecord> records
    ) {

        Objects.requireNonNull(records);

        Set<String> codes = new LinkedHashSet<>();

        for (LecturerScheduleRecord r : records) {
            Objects.requireNonNull(r);
            codes.add(text(r.maLHP()));
        }

        return codes.size();
    }

    private List<String> findDifferences(
            List<LecturerScheduleRecord> expected,
            List<LecturerScheduleRecord> actual
    ) {

        Map<String, Integer> expectedKeyCounts =
                countKeys(expected);

        Map<String, Integer> actualKeyCounts =
                countKeys(actual);

        Map<String, LecturerScheduleRecord> expectedMap =
                uniqueByKey(expected, expectedKeyCounts);

        Map<String, LecturerScheduleRecord> actualMap =
                uniqueByKey(actual, actualKeyCounts);

        Set<String> keys = new LinkedHashSet<>();
        keys.addAll(expectedKeyCounts.keySet());
        keys.addAll(actualKeyCounts.keySet());

        List<String> differences = new ArrayList<>();

        for (String key : keys) {

            int eCount =
                    expectedKeyCounts.getOrDefault(key, 0);

            int aCount =
                    actualKeyCounts.getOrDefault(key, 0);

            if (eCount > 1 || aCount > 1) {

                differences.add(
                        "Khóa lịch học xuất hiện nhiều lần: "
                                + key
                                + " | Expected=" + eCount
                                + ", Actual=" + aCount
                                + ". Xem Missing/Extra để "
                                + "đối chiếu các bản ghi."
                );

                continue;
            }

            LecturerScheduleRecord e =
                    expectedMap.get(key);

            LecturerScheduleRecord a =
                    actualMap.get(key);

            if (e == null) {
                differences.add("Thừa lịch học: " + key);
                continue;
            }

            if (a == null) {
                differences.add("Thiếu lịch học: " + key);
                continue;
            }

            compareFields(differences, key, e, a);
        }

        return differences;
    }

    // Khóa phục vụ mô tả sai lệch.
    // Kết quả MATCH vẫn dựa trên toàn bộ 13 trường.
    private String key(LecturerScheduleRecord r) {

        return r.maLHP()
                + " | " + r.maLop()
                + " | " + r.thu()
                + " | " + r.tiet()
                + " | " + r.tuanHoc();
    }

    private Map<String, Integer> countKeys(
            List<LecturerScheduleRecord> records
    ) {

        Map<String, Integer> result =
                new LinkedHashMap<>();

        for (LecturerScheduleRecord r : records) {
            result.merge(key(r), 1, Integer::sum);
        }

        return result;
    }

    private Map<String, LecturerScheduleRecord> uniqueByKey(
            List<LecturerScheduleRecord> records,
            Map<String, Integer> counts
    ) {

        Map<String, LecturerScheduleRecord> result =
                new LinkedHashMap<>();

        for (LecturerScheduleRecord r : records) {

            String key = key(r);

            if (counts.getOrDefault(key, 0) == 1) {
                result.put(key, r);
            }
        }

        return result;
    }

    private void compareFields(
            List<String> differences,
            String key,
            LecturerScheduleRecord e,
            LecturerScheduleRecord a
    ) {

        String[] names = {
                "maLHP",
                "tenHocPhan",
                "soTietLT",
                "soTietTH",
                "soTietTL",
                "soTC",
                "maLop",
                "tenCBGV",
                "siSo",
                "thu",
                "tiet",
                "phong",
                "tuanHoc"
        };

        String[] left = values(e);
        String[] right = values(a);

        for (int i = 0; i < names.length; i++) {

            if (!Objects.equals(left[i], right[i])) {

                differences.add(
                        key + " / " + names[i]
                                + ": Expected=[" + left[i]
                                + "], Actual=[" + right[i]
                                + "]"
                );
            }
        }
    }

    private String[] values(LecturerScheduleRecord r) {

        return new String[] {
                r.maLHP(),
                r.tenHocPhan(),
                r.soTietLT(),
                r.soTietTH(),
                r.soTietTL(),
                r.soTC(),
                r.maLop(),
                r.tenCBGV(),
                r.siSo(),
                r.thu(),
                r.tiet(),
                r.phong(),
                r.tuanHoc()
        };
    }
}
