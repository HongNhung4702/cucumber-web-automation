
package com.hongnhung.cucumberweb.testdata.progress;

import com.hongnhung.cucumberweb.models.progress.StudyProgressRecord;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class StudyProgressBaselineReader {

    private static final String RESOURCE =
            "baselines/progress/TC_20_expected.csv";

    private static final List<String> HEADERS =
            List.of(
                    "loaiDong",
                    "hocKy",
                    "stcYeuCau",
                    "stcTichLuy",
                    "stcNoDong",
                    "ghiChu"
            );

    public List<StudyProgressRecord> readRecords() {

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
                        "TC_20: Sai header CSV. "
                                + "Expected=" + HEADERS
                                + ", Actual=" + actualHeaders
                );
            }

            List<StudyProgressRecord> result =
                    new ArrayList<>();

            for (CSVRecord row : parser) {

                if (!row.isConsistent() || row.size() != 6) {
                    throw new IllegalStateException(
                            "TC_20: Sai so cot CSV tai dong "
                                    + row.getRecordNumber()
                    );
                }

                result.add(new StudyProgressRecord(
                        row.get(0).strip(),
                        row.get(1).strip(),
                        row.get(2).strip(),
                        row.get(3).strip(),
                        row.get(4).strip(),
                        row.get(5).strip()
                ));
            }

            if (result.size() != 11) {
                throw new IllegalStateException(
                        "TC_20: Baseline phai co 11 dong. "
                                + "Actual=" + result.size()
                );
            }

            for (int i = 0; i < 9; i++) {
                if (!"SEMESTER".equals(result.get(i).loaiDong())) {
                    throw new IllegalStateException(
                            "TC_20: Sai loai dong hoc ky " + (i + 1)
                    );
                }
            }

            if (!"TOTAL".equals(result.get(9).loaiDong())
                    || !"NOTE".equals(result.get(10).loaiDong())) {
                throw new IllegalStateException(
                        "TC_20: Sai thu tu dong TOTAL va NOTE."
                );
            }

            return List.copyOf(result);

        } catch (Exception ex) {
            throw new IllegalStateException(
                    "Khong doc duoc baseline TC_20.",
                    ex
            );
        }
    }
}
