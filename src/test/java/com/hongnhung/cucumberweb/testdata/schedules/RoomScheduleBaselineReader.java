
package com.hongnhung.cucumberweb.testdata.schedules;

import com.hongnhung.cucumberweb.models.schedules.RoomScheduleRecord;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class RoomScheduleBaselineReader {

    private static final String RESOURCE =
            "baselines/schedules/TC_23_expected.csv";

    private static final List<String> HEADERS =
            List.of(
                    "phong",
                    "thu",
                    "monHoc",
                    "nhom",
                    "lop",
                    "tiet",
                    "giangVien"
            );

    public List<RoomScheduleRecord> readRecords() {

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
                        "TC_23: Sai header CSV. "
                                + "Expected=" + HEADERS
                                + ", Actual=" + actualHeaders
                );
            }

            List<RoomScheduleRecord> result =
                    new ArrayList<>();

            for (CSVRecord row : parser) {

                if (!row.isConsistent() || row.size() != 7) {
                    throw new IllegalStateException(
                            "TC_23: Sai CSV tai dong "
                                    + row.getRecordNumber()
                    );
                }

                result.add(new RoomScheduleRecord(
                        row.get(0).strip(),
                        row.get(1).strip(),
                        row.get(2).strip(),
                        row.get(3).strip(),
                        row.get(4).strip(),
                        row.get(5).strip(),
                        row.get(6).strip()
                ));
            }

            if (result.size() != 8) {
                throw new IllegalStateException(
                        "TC_23: Baseline phai co 8 luot hoc. "
                                + "Actual=" + result.size()
                );
            }

            return List.copyOf(result);

        } catch (Exception ex) {
            throw new IllegalStateException(
                    "Khong doc duoc baseline TC_23.",
                    ex
            );
        }
    }
}
