
package com.hongnhung.cucumberweb.testdata.lecturers;

import com.hongnhung.cucumberweb.models.lecturers.LecturerScheduleRecord;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class LecturerScheduleBaselineReader {

    private static final String RESOURCE =
            "baselines/lecturers/TC_38_expected.csv";

    private static final List<String> HEADERS = List.of(
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
    );

    public List<LecturerScheduleRecord> readRecords() {

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
                        "Header TC_38 không khớp.\n"
                                + "Expected: " + HEADERS
                                + "\nActual: " + actualHeaders
                );
            }

            List<LecturerScheduleRecord> result =
                    new ArrayList<>();

            for (CSVRecord row : parser) {

                if (!row.isConsistent()
                        || row.size() != HEADERS.size()) {

                    throw new IllegalStateException(
                            "Sai số cột tại bản ghi CSV "
                                    + row.getRecordNumber()
                                    + ". Expected=13, Actual="
                                    + row.size()
                    );
                }

                result.add(new LecturerScheduleRecord(
                        cell(row, 0),
                        cell(row, 1),
                        cell(row, 2),
                        cell(row, 3),
                        cell(row, 4),
                        cell(row, 5),
                        cell(row, 6),
                        cell(row, 7),
                        cell(row, 8),
                        cell(row, 9),
                        cell(row, 10),
                        cell(row, 11),
                        cell(row, 12)
                ));
            }

            if (result.isEmpty()) {
                throw new IllegalStateException(
                        "Baseline TC_38 không có bản ghi."
                );
            }

            System.out.println(
                    "[TC_38] Đọc baseline thành công: "
                            + result.size() + " dòng."
            );

            return List.copyOf(result);

        } catch (Exception ex) {
            throw new IllegalStateException(
                    "Không đọc được baseline TC_38: "
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
