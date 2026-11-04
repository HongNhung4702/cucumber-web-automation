
package com.hongnhung.cucumberweb.testdata.exams;

import com.hongnhung.cucumberweb.models.exams.ExamRecord;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class ExamBaselineReader {

    private static final String RESOURCE =
            "baselines/exams/TC_28_expected.csv";

    private static final List<String> COLUMNS = List.of(
            "maLopHocPhan",
            "tenLopHocPhan",
            "lanThi",
            "ngayThi",
            "gioThi",
            "phongThi",
            "diaDiem",
            "hinhThucThi",
            "kyThi",
            "ghiChu",
            "vangThi"
    );

    public List<ExamRecord> readTc28() {

        InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream(RESOURCE);

        if (input == null) {
            throw new IllegalStateException(
                    "Không tìm thấy baseline: " + RESOURCE
            );
        }

        try (PushbackReader reader = new PushbackReader(
                new InputStreamReader(
                        input,
                        StandardCharsets.UTF_8
                ), 1)) {

            // Xử lý ký tự BOM nếu CSV có BOM UTF-8.
            int first = reader.read();

            if (first != -1 && first != '\uFEFF') {
                reader.unread(first);
            }

            try (CSVParser csv = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .get()
                    .parse(reader)) {

                if (!COLUMNS.equals(csv.getHeaderNames())) {
                    throw new IllegalStateException(
                            "Header của baseline không đúng. Expected="
                                    + COLUMNS
                                    + ", Actual="
                                    + csv.getHeaderNames()
                    );
                }

                List<ExamRecord> result = new ArrayList<>();

                for (CSVRecord row : csv) {

                    if (!row.isConsistent()) {
                        throw new IllegalStateException(
                                "Sai số cột CSV tại dòng "
                                        + row.getRecordNumber()
                        );
                    }

                    result.add(new ExamRecord(
                            value(row, "maLopHocPhan"),
                            value(row, "tenLopHocPhan"),
                            value(row, "lanThi"),
                            value(row, "ngayThi"),
                            value(row, "gioThi"),
                            value(row, "phongThi"),
                            value(row, "diaDiem"),
                            value(row, "hinhThucThi"),
                            value(row, "kyThi"),
                            value(row, "ghiChu"),
                            value(row, "vangThi")
                    ));
                }

                if (result.isEmpty()) {
                    throw new IllegalStateException(
                            "Baseline TC_28 đang rỗng."
                    );
                }

                return List.copyOf(result);
            }

        } catch (IOException ex) {
            throw new IllegalStateException(
                    "Không đọc được baseline: " + RESOURCE,
                    ex
            );
        }
    }

    private String value(CSVRecord row, String column) {
        return row.get(column)
                .strip()
                .replaceAll("\\s+", " ");
    }
}
