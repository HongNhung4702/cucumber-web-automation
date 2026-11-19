
package com.hongnhung.cucumberweb.testdata.schedules;

import com.hongnhung.cucumberweb.models.schedules.PrintScheduleRecord;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public final class PrintScheduleBaselineReader {

    private static final String RESOURCE =
            "baselines/schedules/TC_26_print_expected.csv";

    private static final List<String> HEADERS =
            List.of(
                    "stt",
                    "maLhpTenLhp",
                    "tenGv",
                    "stc",
                    "maLop",
                    "thu",
                    "tiet",
                    "phongHoc",
                    "tuan",
                    "diaDiem"
            );

    public List<PrintScheduleRecord> readRecords() {

        InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream(RESOURCE);

        if (input == null) {
            throw new IllegalStateException(
                    "Khong tim thay baseline: " + RESOURCE
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
                        "TC_26: Sai header CSV. "
                                + "Expected=" + HEADERS
                                + ", Actual=" + actualHeaders
                );
            }

            List<PrintScheduleRecord> records =
                    new ArrayList<>();

            for (CSVRecord row : parser) {

                if (!row.isConsistent() || row.size() != 10) {
                    throw new IllegalStateException(
                            "TC_26: Sai so cot CSV tai dong "
                                    + row.getRecordNumber()
                    );
                }

                records.add(new PrintScheduleRecord(
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

            if (records.size() != 16) {
                throw new IllegalStateException(
                        "TC_26: Baseline phai co 16 dong lich. "
                                + "Actual=" + records.size()
                );
            }

            long courseCount = records.stream()
                    .map(PrintScheduleRecord::stt)
                    .distinct()
                    .count();

            if (courseCount != 7) {
                throw new IllegalStateException(
                        "TC_26: Baseline phai co 7 hoc phan."
                );
            }

            return List.copyOf(records);

        } catch (Exception ex) {
            throw new IllegalStateException(
                    "Khong doc duoc baseline TC_26.",
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
