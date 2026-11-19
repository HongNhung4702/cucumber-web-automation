
package com.hongnhung.cucumberweb.testdata.exams;

import com.hongnhung.cucumberweb.models.exams.ExamRecord;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class ExamTermBaselineReader {

    private static final String RESOURCE =
            "baselines/exams/TC_30_HK02_expected.csv";

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
                    "ghiChu",
                    "vangThi"
            );

    public List<ExamRecord> readTargetTerm() {

        InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream(RESOURCE);

        if (input == null) {
            throw new IllegalStateException(
                    "TC_30: Khong tim thay baseline " + RESOURCE
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
                        "TC_30: Sai header baseline. "
                                + "Actual=" + actualHeaders
                );
            }

            List<ExamRecord> records = new ArrayList<>();

            for (CSVRecord row : parser) {

                if (!row.isConsistent() || row.size() != 11) {
                    throw new IllegalStateException(
                            "TC_30: Sai so cot tai dong "
                                    + row.getRecordNumber()
                    );
                }

                records.add(new ExamRecord(
                        clean(row.get(0)),
                        clean(row.get(1)),
                        clean(row.get(2)),
                        clean(row.get(3)),
                        clean(row.get(4)),
                        clean(row.get(5)),
                        clean(row.get(6)),
                        clean(row.get(7)),
                        clean(row.get(8)),
                        clean(row.get(9)),
                        clean(row.get(10))
                ));
            }

            if (records.size() != 4) {
                throw new IllegalStateException(
                        "TC_30: Baseline HK2 phai co 4 ban ghi. "
                                + "Actual=" + records.size()
                );
            }

            return List.copyOf(records);

        } catch (Exception ex) {
            throw new IllegalStateException(
                    "TC_30: Khong doc duoc baseline HK2.",
                    ex
            );
        }
    }

    private String clean(String value) {
        return value.replace('\u00A0', ' ')
                .replace('\u202F', ' ')
                .strip()
                .replaceAll("\\s+", " ");
    }
}
