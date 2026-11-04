
package com.hongnhung.cucumberweb.testdata.unaccumulated;

import com.hongnhung.cucumberweb.models.unaccumulated.UnaccumulatedCourseRecord;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class UnaccumulatedBaselineReader {

    private static final String RESOURCE =
            "baselines/unaccumulated/TC_17_expected.csv";

    private static final List<String> HEADERS = List.of(
            "maHocPhan",
            "tenHocPhan",
            "soTC",
            "soTietLT",
            "soTietTH",
            "soTietTL",
            "hocPhanTuongDuong",
            "hocPhanHocTruoc",
            "hocPhanThayThe"
    );

    public List<UnaccumulatedCourseRecord> readRecords() {

        InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream(RESOURCE);

        if (input == null) {
            throw new IllegalStateException(
                    "Không tìm thấy baseline: " + RESOURCE
            );
        }

        try (
                InputStream stream = input;
                InputStreamReader reader =
                        new InputStreamReader(
                                stream,
                                StandardCharsets.UTF_8
                        );
                CSVParser parser =
                        CSVFormat.DEFAULT.builder()
                                .setHeader()
                                .setSkipHeaderRecord(true)
                                .get()
                                .parse(reader)
        ) {

            List<String> actualHeaders =
                    new ArrayList<>(parser.getHeaderNames());

            if (!actualHeaders.isEmpty()) {
                actualHeaders.set(
                        0,
                        actualHeaders.get(0)
                                .replace("\uFEFF", "")
                );
            }

            if (!HEADERS.equals(actualHeaders)) {
                throw new IllegalStateException(
                        "Header baseline TC_17 không khớp.\n"
                                + "Expected: " + HEADERS
                                + "\nActual: " + actualHeaders
                );
            }

            List<UnaccumulatedCourseRecord> records =
                    new ArrayList<>();

            for (CSVRecord row : parser) {

                if (!row.isConsistent()
                        || row.size() != HEADERS.size()) {

                    throw new IllegalStateException(
                            "Bản ghi CSV số "
                                    + row.getRecordNumber()
                                    + " có sai số cột. "
                                    + "Expected=9, Actual="
                                    + row.size()
                    );
                }

                records.add(new UnaccumulatedCourseRecord(
                        cell(row, 0),
                        cell(row, 1),
                        cell(row, 2),
                        cell(row, 3),
                        cell(row, 4),
                        cell(row, 5),
                        cell(row, 6),
                        cell(row, 7),
                        cell(row, 8)
                ));
            }

            if (records.isEmpty()) {
                throw new IllegalStateException(
                        "Baseline TC_17 không có học phần."
                );
            }

            System.out.println(
                    "[TC_17] Đọc baseline thành công: "
                            + records.size() + " học phần."
            );

            return List.copyOf(records);

        } catch (Exception ex) {
            throw new IllegalStateException(
                    "Không đọc được baseline TC_17: "
                            + RESOURCE,
                    ex
            );
        }
    }

    private String cell(CSVRecord row, int index) {

        String value = row.get(index);

        return value == null ? "" : value.strip();
    }
}
