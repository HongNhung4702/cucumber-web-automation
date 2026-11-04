
package com.hongnhung.cucumberweb.testdata.marks;

import com.hongnhung.cucumberweb.models.marks.MarkRecord;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class MarksBaselineReader {

    private static final String COURSE_FILE =
            "baselines/marks/TC_33_expected.csv";

    private static final String SUMMARY_FILE =
            "baselines/marks/TC_33_summary_expected.csv";

    private static final List<String> COURSE_HEADERS = List.of(
            "maHocPhan",
            "tenHocPhan",
            "tinChi",
            "diem10",
            "diem4",
            "diemChu",
            "ketQuaHienThi"
    );

    private static final List<String> SUMMARY_HEADERS = List.of(
            "chiTieu",
            "giaTri",
            "phamVi"
    );

    private static final Set<String> CORE_METRICS = Set.of(
            "tongSoTinChi",
            "soTinChiDat",
            "soTinChiKhongDat",
            "diemTrungBinhHocKyHe10",
            "diemTrungBinhHocKyHe4"
    );

    public List<MarkRecord> readCourses() {

        try (CSVParser csv = openCsv(
                COURSE_FILE,
                COURSE_HEADERS
        )) {
            List<MarkRecord> result = new ArrayList<>();

            for (CSVRecord row : csv) {
                checkRow(row, COURSE_HEADERS.size());

                result.add(new MarkRecord(
                        value(row, "maHocPhan"),
                        value(row, "tenHocPhan"),
                        value(row, "tinChi"),
                        value(row, "diem10"),
                        value(row, "diem4"),
                        value(row, "diemChu"),
                        value(row, "ketQuaHienThi")
                ));
            }

            if (result.isEmpty()) {
                throw new IllegalStateException(
                        "Baseline TC_33 không có học phần."
                );
            }

            return List.copyOf(result);

        } catch (IOException ex) {
            throw new IllegalStateException(
                    "Không đọc được CSV học phần.", ex
            );
        }
    }

    public Map<String, String> readSemesterSummary() {

        try (CSVParser csv = openCsv(
                SUMMARY_FILE,
                SUMMARY_HEADERS
        )) {
            Map<String, String> result =
                    new LinkedHashMap<>();

            for (CSVRecord row : csv) {
                checkRow(row, SUMMARY_HEADERS.size());

                String key = value(row, "chiTieu");
                String value = value(row, "giaTri");
                String scope = value(row, "phamVi");

                if (!"hoc_ky".equals(scope)) {
                    continue;
                }

                if (!CORE_METRICS.contains(key)) {
                    throw new IllegalStateException(
                            "Chỉ tiêu học kỳ không hợp lệ: "
                                    + key
                    );
                }

                if (result.putIfAbsent(key, value) != null) {
                    throw new IllegalStateException(
                            "Trùng chỉ tiêu: " + key
                    );
                }
            }

            if (!result.keySet().equals(CORE_METRICS)) {
                throw new IllegalStateException(
                        "Thiếu hoặc thừa chỉ tiêu học kỳ. "
                                + "Đã đọc: " + result.keySet()
                );
            }

            return Map.copyOf(result);

        } catch (IOException ex) {
            throw new IllegalStateException(
                    "Không đọc được CSV tổng hợp.", ex
            );
        }
    }

    private CSVParser openCsv(
            String resource,
            List<String> expectedHeaders
    ) throws IOException {

        InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream(resource);

        if (input == null) {
            throw new IllegalStateException(
                    "Không tìm thấy file: " + resource
            );
        }

        CSVParser parser;

        try {
            // UTF-8 BOM nếu có sẽ được xử lý
            // bằng cách loại bỏ ở tên cột đầu tiên.
            parser = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .get()
                    .parse(new InputStreamReader(
                            input,
                            StandardCharsets.UTF_8
                    ));
        } catch (IOException | RuntimeException ex) {
            input.close();
            throw ex;
        }

        List<String> actualHeaders =
                new ArrayList<>(parser.getHeaderNames());

        if (!actualHeaders.isEmpty()) {
            actualHeaders.set(
                    0,
                    actualHeaders.get(0)
                            .replace("\uFEFF", "")
            );
        }

        if (!actualHeaders.equals(expectedHeaders)) {
            parser.close();
            throw new IllegalStateException(
                    "Header CSV không đúng: " + resource
                            + ". Expected=" + expectedHeaders
                            + ", Actual=" + actualHeaders
            );
        }

        return parser;
    }

    private void checkRow(CSVRecord row, int expectedSize) {
        if (!row.isConsistent()
                || row.size() != expectedSize) {
            throw new IllegalStateException(
                    "Sai số cột CSV tại dòng "
                            + row.getRecordNumber()
            );
        }
    }

    private String value(CSVRecord row, String column) {

        String result;

        // Hỗ trợ file có BOM UTF-8 tại cột đầu tiên.
        if (row.isMapped(column)) {
            result = row.get(column);
        } else if (row.isMapped("\uFEFF" + column)) {
            result = row.get("\uFEFF" + column);
        } else {
            throw new IllegalStateException(
                    "Không tìm thấy cột: " + column
            );
        }

        return Normalizer.normalize(
                result.strip().replaceAll("\\s+", " "),
                Normalizer.Form.NFC
        );
    }
}
