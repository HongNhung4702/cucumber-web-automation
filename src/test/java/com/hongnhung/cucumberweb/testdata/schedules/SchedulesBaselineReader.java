
package com.hongnhung.cucumberweb.testdata.schedules;

import com.hongnhung.cucumberweb.models.schedules.ScheduleRecord;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class SchedulesBaselineReader {

    private static final String RESOURCE =
            "baselines/schedules/TC_21_expected.csv";

    private static final List<String> HEADERS = List.of(
            "sttHocPhan",
            "maLopHocPhan",
            "tenHocPhan",
            "tenGiaoVien",
            "soTC",
            "thu",
            "tietHoc",
            "phongHoc",
            "tuan",
            "tuNgayDenNgay",
            "phongDiaDiem",
            "diaChi"
    );

    public List<ScheduleRecord> readRecords() {

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
                        "Header CSV không khớp.\n"
                                + "Expected: " + HEADERS
                                + "\nActual: " + actualHeaders
                );
            }

            List<ScheduleRecord> result =
                    new ArrayList<>();

            for (CSVRecord row : parser) {

                if (!row.isConsistent()
                        || row.size() != HEADERS.size()) {

                    throw new IllegalStateException(
                            "Sai số cột tại dòng CSV "
                                    + row.getRecordNumber()
                                    + ". Expected=12, Actual="
                                    + row.size()
                    );
                }

                result.add(new ScheduleRecord(
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
                        cell(row, 11)
                ));
            }

            if (result.isEmpty()) {
                throw new IllegalStateException(
                        "Baseline TC_21 không có bản ghi."
                );
            }

            return List.copyOf(result);

        } catch (Exception ex) {

            throw new IllegalStateException(
                    "Không đọc được dữ liệu TC_21: "
                            + RESOURCE,
                    ex
            );
        }
    }

    private String cell(CSVRecord row, int index) {

        String value = row.get(index);

        if (value == null) {
            return "";
        }

        return value.strip();
    }
}
