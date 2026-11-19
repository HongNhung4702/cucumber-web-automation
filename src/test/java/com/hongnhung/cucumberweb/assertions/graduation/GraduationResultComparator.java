
package com.hongnhung.cucumberweb.assertions.graduation;

import com.hongnhung.cucumberweb.models.graduation.GraduationResultRecord;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public final class GraduationResultComparator {

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
            List<GraduationResultRecord> expected,
            List<GraduationResultRecord> actual
    ) {

        List<String> differences = new ArrayList<>();

        if (expected.size() != actual.size()) {
            differences.add(
                    "Sai so dong: expected="
                            + expected.size()
                            + ", actual=" + actual.size()
            );
        }

        int count = Math.min(
                expected.size(),
                actual.size()
        );

        for (int i = 0; i < count; i++) {

            GraduationResultRecord e = expected.get(i);
            GraduationResultRecord a = actual.get(i);

            int row = i + 1;

            check(differences, row, "tenDot",
                    e.tenDot(), a.tenDot());

            check(differences, row, "dangKyCTDT",
                    e.dangKyCTDT(), a.dangKyCTDT());

            check(differences, row, "ngayXet",
                    e.ngayXet(), a.ngayXet());

            check(differences, row, "ketQua",
                    e.ketQua(), a.ketQua());

            check(differences, row, "diemTrungBinh",
                    e.diemTrungBinh(), a.diemTrungBinh());

            check(differences, row, "xepLoai",
                    e.xepLoai(), a.xepLoai());

            check(differences, row, "ghiChu",
                    e.ghiChu(), a.ghiChu());
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
                        .replace('\u202F', ' ')
                        .strip()
                        .replaceAll("\\s+", " "),
                Normalizer.Form.NFC
        );
    }
}
