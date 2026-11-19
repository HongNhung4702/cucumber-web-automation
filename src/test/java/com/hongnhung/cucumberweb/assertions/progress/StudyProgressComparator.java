
package com.hongnhung.cucumberweb.assertions.progress;

import com.hongnhung.cucumberweb.models.progress.StudyProgressRecord;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public final class StudyProgressComparator {

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
            List<StudyProgressRecord> expected,
            List<StudyProgressRecord> actual
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

            StudyProgressRecord e = expected.get(i);
            StudyProgressRecord a = actual.get(i);

            int row = i + 1;

            check(differences, row, "loaiDong",
                    e.loaiDong(), a.loaiDong());

            check(differences, row, "hocKy",
                    e.hocKy(), a.hocKy());

            check(differences, row, "stcYeuCau",
                    e.stcYeuCau(), a.stcYeuCau());

            check(differences, row, "stcTichLuy",
                    e.stcTichLuy(), a.stcTichLuy());

            check(differences, row, "stcNoDong",
                    e.stcNoDong(), a.stcNoDong());

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
