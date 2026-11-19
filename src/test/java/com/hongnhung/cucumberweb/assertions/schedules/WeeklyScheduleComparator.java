
package com.hongnhung.cucumberweb.assertions.schedules;

import com.hongnhung.cucumberweb.models.schedules.WeeklyScheduleRecord;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class WeeklyScheduleComparator {

    private static final List<String> DAYS =
            List.of(
                    "Thứ 2", "Thứ 3", "Thứ 4",
                    "Thứ 5", "Thứ 6", "Thứ 7",
                    "Chủ nhật"
            );

    private static final List<String> SESSIONS =
            List.of("Sáng", "Chiều", "Tối");

    public record Result(
            int expectedCount,
            int actualCount,
            List<String> differences
    ) {
        public boolean passed() {
            return differences.isEmpty();
        }

        public String details() {
            return "Expected=" + expectedCount
                    + ", Actual=" + actualCount
                    + ", Differences=" + differences;
        }
    }

    public Result compare(
            List<WeeklyScheduleRecord> expected,
            List<WeeklyScheduleRecord> actual,
            List<String> actualEmptyCells,
            int actualOccupiedCells
    ) {

        List<String> differences = new ArrayList<>();

        if (expected.size() != actual.size()) {
            differences.add(
                    "Sai tong luot hoc: expected="
                            + expected.size()
                            + ", actual=" + actual.size()
            );
        }

        Map<String, Integer> expectedCounts =
                countRecords(expected);

        Map<String, Integer> actualCounts =
                countRecords(actual);

        Set<String> keys = new HashSet<>();
        keys.addAll(expectedCounts.keySet());
        keys.addAll(actualCounts.keySet());

        for (String key : keys) {

            int expectedNumber =
                    expectedCounts.getOrDefault(key, 0);

            int actualNumber =
                    actualCounts.getOrDefault(key, 0);

            if (expectedNumber != actualNumber) {
                differences.add(
                        "Ban ghi sai lech: "
                                + key
                                + ", expected=" + expectedNumber
                                + ", actual=" + actualNumber
                );
            }
        }

        Set<String> expectedOccupied =
                new HashSet<>();

        for (WeeklyScheduleRecord record : expected) {
            expectedOccupied.add(
                    normalize(record.thu())
                            + " / "
                            + normalize(record.buoi())
            );
        }

        List<String> expectedEmpty =
                new ArrayList<>();

        for (String session : SESSIONS) {
            for (String day : DAYS) {

                String cellId = day + " / " + session;

                if (!expectedOccupied.contains(cellId)) {
                    expectedEmpty.add(cellId);
                }
            }
        }

        if (expectedOccupied.size() != actualOccupiedCells) {
            differences.add(
                    "Sai so o co lich: expected="
                            + expectedOccupied.size()
                            + ", actual=" + actualOccupiedCells
            );
        }

        Set<String> actualEmpty = new HashSet<>();
        for (String cell : actualEmptyCells) {
            actualEmpty.add(normalize(cell));
        }

        if (!new HashSet<>(expectedEmpty).equals(actualEmpty)
                || expectedEmpty.size() != actualEmptyCells.size()) {

            differences.add(
                    "Sai vi tri cac o trong: expected="
                            + expectedEmpty
                            + ", actual=" + actualEmptyCells
            );
        }

        if (actualOccupiedCells + actualEmptyCells.size() != 21) {
            differences.add(
                    "Tong o lich khong bang 21."
            );
        }

        return new Result(
                expected.size(),
                actual.size(),
                List.copyOf(differences)
        );
    }

    private Map<String, Integer> countRecords(
            List<WeeklyScheduleRecord> records
    ) {

        Map<String, Integer> counts =
                new HashMap<>();

        for (WeeklyScheduleRecord record : records) {

            String key = String.join(" | ",
                    normalize(record.thu()),
                    normalize(record.buoi()),
                    normalize(record.monHoc()),
                    normalize(record.nhom()),
                    normalize(record.lop()),
                    normalize(record.tiet()),
                    normalize(record.giangVien())
            );

            counts.merge(key, 1, Integer::sum);
        }

        return counts;
    }

    private String normalize(String value) {

        if (value == null) {
            return "";
        }

        return Normalizer.normalize(
                value.replace('\u00A0', ' ')
                        .replace('\u202F', ' ')
                        .strip()
                        .replaceAll("\\s+", " "),
                Normalizer.Form.NFC
        );
    }
}
