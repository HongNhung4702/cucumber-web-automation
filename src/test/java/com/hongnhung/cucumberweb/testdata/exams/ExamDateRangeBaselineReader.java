
package com.hongnhung.cucumberweb.testdata.exams;

import com.hongnhung.cucumberweb.models.exams.ExamDateRangeRecord;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public final class ExamDateRangeBaselineReader {

    private static final String RESOURCE =
            "baselines/exams/TC_31_date_range_expected.csv";

    private static final List<String> HEADERS =
            List.of(
                    "maLopHocPhan",
                    "tenLopHocPhan",
                    "lanThi",
                    "ngayThi",
                    "gioThi",
                    "phongThi",
                    "diaDiem",
                    "hinhThucThi",
                    "kyThi",
                    "ghiChu"
            );

    public List<ExamDateRangeRecord> readRecords() {

        InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream(RESOURCE);

        if (input == null) {
            throw new IllegalStateException(
                    "TC_31: Khong tim thay baseline: "
                            + RESOURCE
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
                        "TC_31: Sai header CSV. "
                                + "Expected=" + HEADERS
                                + ", Actual=" + actualHeaders
                );
            }

            List<ExamDateRangeRecord> result =
                    new ArrayList<>();

            for (CSVRecord row : parser) {

                if (!row.isConsistent() || row.size() != 10) {
                    throw new IllegalStateException(
                            "TC_31: Sai so cot CSV tai dong "
                                    + row.getRecordNumber()
                    );
                }

                result.add(new ExamDateRangeRecord(
                        clean(row.get(0)),
                        clean(row.get(1)),
                        clean(row.get(2)),
                        clean(row.get(3)),
                        clean(row.get(4)),
                        clean(row.get(5)),
                        clean(row.get(6)),
                        clean(row.get(7)),
                        clean(row.get(8)),
                        clean(row.get(9))
                ));
            }

            if (result.size() != 2) {
                throw new IllegalStateException(
                        "TC_31: Baseline phai co 2 ban ghi. "
                                + "Actual=" + result.size()
                );
            }

            return List.copyOf(result);

        } catch (Exception ex) {
            throw new IllegalStateException(
                    "TC_31: Khong doc duoc baseline.",
                    ex
            );
        }
    }

    private String clean(String text) {

        return Normalizer.normalize(
                text.replace('\u00A0', ' ')
                        .replace('\u202F', ' ')
                        .strip()
                        .replaceAll("\\s+", " "),
                Normalizer.Form.NFC
        );
    }
}
