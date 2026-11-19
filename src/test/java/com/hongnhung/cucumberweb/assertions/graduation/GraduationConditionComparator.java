
package com.hongnhung.cucumberweb.assertions.graduation;

import com.hongnhung.cucumberweb.models.graduation.GraduationConditionRecord;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public final class GraduationConditionComparator {

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
            List<GraduationConditionRecord> expected,
            List<GraduationConditionRecord> actual
    ) {

        List<String> differences = new ArrayList<>();

        if (expected.size() != actual.size()) {
            differences.add(
                    "Sai so dong: expected="
                            + expected.size()
                            + ", actual=" + actual.size()
            );
        }

        int count = Math.min(expected.size(), actual.size());

        for (int i = 0; i < count; i++) {

            GraduationConditionRecord e = expected.get(i);
            GraduationConditionRecord a = actual.get(i);

            int row = i + 1;

            check(differences, row, "loaiChuanDauRa",
                    e.loaiChuanDauRa(), a.loaiChuanDauRa());

            check(differences, row, "dieuKienDauRa",
                    e.dieuKienDauRa(), a.dieuKienDauRa());

            check(differences, row, "ghiChu",
                    e.ghiChu(), a.ghiChu());

            check(differences, row, "chungChiSinhVien",
                    e.chungChiSinhVien(), a.chungChiSinhVien());

            check(differences, row, "ketQua",
                    e.ketQua(), a.ketQua());
        }

        return new Result(
                expected.size(),
                actual.size(),
                List.copyOf(differences)
        );
    }

    private void check(
            List<String> differences,
            int row,
            String field,
            String expected,
            String actual
    ) {

        if (!normalize(expected).equals(normalize(actual))) {
            differences.add(
                    "Dong " + row
                            + ", truong " + field
                            + ": expected=[" + expected
                            + "], actual=[" + actual + "]"
            );
        }
    }

    private String normalize(String value) {

        return Normalizer.normalize(
                value.replace('\u00A0', ' ')
                        .strip()
                        .replaceAll("\\s+", " "),
                Normalizer.Form.NFC
        );
    }
}
