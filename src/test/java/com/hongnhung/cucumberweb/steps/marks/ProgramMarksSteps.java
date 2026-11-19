
package com.hongnhung.cucumberweb.steps.marks;

import com.hongnhung.cucumberweb.models.marks.ProgramMarkRecord;
import com.hongnhung.cucumberweb.pages.student.marks.ProgramMarksPage;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class ProgramMarksSteps {

    private static final String RESOURCE =
            "baselines/marks/TC_34_program_expected.csv";

    private static final List<String> CSV_HEADERS =
            List.of(
                    "ky", "loai", "nhom", "tt", "ma",
                    "ten", "tc", "d10", "d4", "chu",
                    "ketQua"
            );

    private final ProgramMarksPage page;

    public ProgramMarksSteps(ProgramMarksPage page) {
        this.page = page;
    }

    @When("sinh viên mở bảng điểm chương trình đào tạo TC_34")
    public void openProgramMarks() {
        page.open();
    }

    @Then("chương trình đào tạo và cấu trúc bảng điểm TC_34 hiển thị đúng")
    public void verifyStructure() {

        assertEquals(
                "Kỹ thuật phần mềm",
                page.selectedProgram(),
                "TC_34: Sai ten chuong trinh."
        );

        assertEquals(
                "119DHCQK45",
                page.selectedProgramCode(),
                "TC_34: Sai ma chuong trinh."
        );

        assertTrue(
                page.programModeSelected(),
                "TC_34: Chua chon che do CTDT."
        );

        ProgramMarksPage.Snapshot actual =
                page.snapshot();

        assertEquals(
                9,
                actual.semesterHeadings().size(),
                "TC_34: Sai so hoc ky."
        );

        assertEquals(
                82,
                actual.records().size(),
                "TC_34: Sai so hoc phan."
        );

        for (int i = 0; i < 82; i++) {
            assertEquals(
                    String.valueOf(i + 1),
                    actual.records().get(i).tt(),
                    "TC_34: Sai TT hoc phan tai dong "
                            + (i + 1)
            );
        }

        System.out.println(
                "[TC_34] Chuong trinh, 9 hoc ky, "
                        + "82 hoc phan: MATCH."
        );
    }

    @Then("toàn bộ điểm chương trình đào tạo TC_34 khớp baseline")
    public void verifyBaseline() {

        List<ProgramMarkRecord> expected =
                readBaseline();

        ProgramMarksPage.Snapshot snapshot =
                page.snapshot();

        List<ProgramMarkRecord> actual =
                snapshot.records();

        assertEquals(
                82,
                actual.size(),
                "TC_34: Sai tong so hoc phan."
        );

        // Kiểm tra thiếu, thừa và bản ghi trùng.
        assertEquals(
                countRecords(expected),
                countRecords(actual),
                "TC_34: Cac ban ghi "
                        + "khong khop baseline."
        );

        // Kiểm tra cả thứ tự và vị trí trong các nhóm.
        List<String> differences = new ArrayList<>();

        for (int i = 0; i < expected.size(); i++) {

            if (!expected.get(i).equals(actual.get(i))) {

                differences.add(
                        "Dong " + (i + 1)
                                + ": expected=" + expected.get(i)
                                + ", actual=" + actual.get(i)
                );
            }
        }

        assertTrue(
                differences.isEmpty(),
                "TC_34: Sai thu tu/du lieu. "
                        + String.join("\n", differences)
        );

        long passed = actual.stream()
                .filter(r -> "DAT".equals(r.ketQua()))
                .count();

        long failed = actual.stream()
                .filter(r -> "KHONG_DAT".equals(r.ketQua()))
                .count();

        long notAvailable = actual.stream()
                .filter(r -> "CHUA_CO".equals(r.ketQua()))
                .count();

        assertEquals(51L, passed);
        assertEquals(6L, failed);
        assertEquals(25L, notAvailable);

        // Đối chiếu chính xác thứ tự tiêu đề học kỳ
        // dựa trên lần xuất hiện đầu tiên trong CSV.
        List<String> expectedSemesters =
                expected.stream()
                        .map(ProgramMarkRecord::ky)
                        .distinct()
                        .toList();

        assertEquals(
                expectedSemesters,
                snapshot.semesterHeadings(),
                "TC_34: Sai thu tu hoc ky."
        );

        assertEquals(
                82,
                snapshot.ignoredDetailRows(),
                "TC_34: Sai so dong chi tiet thu gon."
        );

        System.out.println(
                "[TC_34] Baseline 82 hoc phan: MATCH."
        );

        System.out.println(
                "[TC_34] DAT=51, KHONG_DAT=6, "
                        + "CHUA_CO=25."
        );
    }

    private Map<ProgramMarkRecord, Integer> countRecords(
            List<ProgramMarkRecord> records
    ) {

        Map<ProgramMarkRecord, Integer> result =
                new HashMap<>();

        for (ProgramMarkRecord record : records) {
            result.merge(record, 1, Integer::sum);
        }

        return result;
    }

    private List<ProgramMarkRecord> readBaseline() {

        InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream(RESOURCE);

        if (input == null) {
            throw new IllegalStateException(
                    "TC_34: Khong tim thay baseline " + RESOURCE
            );
        }

        try (
                InputStream stream = input;
                InputStreamReader reader =
                        new InputStreamReader(
                                stream,
                                StandardCharsets.UTF_8
                        );
                CSVParser csv =
                        CSVFormat.DEFAULT.builder()
                                .setHeader()
                                .setSkipHeaderRecord(true)
                                .get()
                                .parse(reader)
        ) {

            List<String> headers =
                    new ArrayList<>(csv.getHeaderNames());

            if (!headers.isEmpty()) {
                headers.set(
                        0,
                        headers.get(0).replace("\uFEFF", "")
                );
            }

            if (!CSV_HEADERS.equals(headers)) {
                throw new IllegalStateException(
                        "TC_34: Sai header CSV: " + headers
                );
            }

            List<ProgramMarkRecord> result =
                    new ArrayList<>();

            for (CSVRecord row : csv) {

                if (!row.isConsistent() || row.size() != 11) {
                    throw new IllegalStateException(
                            "TC_34: Sai CSV dong "
                                    + row.getRecordNumber()
                    );
                }

                result.add(new ProgramMarkRecord(
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

            if (result.size() != 82) {
                throw new IllegalStateException(
                        "TC_34: Baseline phai co 82 hoc phan."
                );
            }

            return List.copyOf(result);

        } catch (Exception ex) {
            throw new IllegalStateException(
                    "TC_34: Khong doc duoc baseline.",
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
