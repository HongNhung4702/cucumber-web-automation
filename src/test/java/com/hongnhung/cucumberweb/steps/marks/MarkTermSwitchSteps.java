
package com.hongnhung.cucumberweb.steps.marks;

import com.hongnhung.cucumberweb.pages.student.marks.MarkTermSwitchPage;
import com.hongnhung.cucumberweb.pages.student.marks.MarkTermSwitchPage.MarkRow;
import com.hongnhung.cucumberweb.pages.student.marks.MarkTermSwitchPage.Snapshot;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class MarkTermSwitchSteps {

    private static final String A_HEADING =
            "Năm học: 2024-2025 - Học kỳ: HK02";

    private static final String B_HEADING =
            "Năm học: 2026-2027 - Học kỳ: HK01";

    private final MarkTermSwitchPage page;

    private Snapshot firstA;

    public MarkTermSwitchSteps(MarkTermSwitchPage page) {
        this.page = page;
    }

    @When("sinh viên ghi nhận điểm học kỳ A TC_36")
    public void recordA() {

        page.open();
        page.select("2024-2025", "Học kỳ 2");

        firstA = page.awaitSnapshot(A_HEADING, 6);

        assertEquals(
                readCsv("TC_36_A_expected.csv"),
                firstA.rows(),
                "TC_36: Diem hoc ky A sai baseline."
        );

        verifySummaryA(firstA);

        System.out.println(
                "[TC_36] A lan 1: 6 hoc phan MATCH."
        );
    }

    @When("sinh viên chuyển sang học kỳ B TC_36")
    public void switchB() {

        page.select("2026-2027", "Học kỳ 1");
    }

    @Then("học kỳ B TC_36 hiển thị học phần chưa có điểm")
    public void verifyB() {

        assertEquals("2026-2027", page.selectedYear());
        assertEquals("Học kỳ 1", page.selectedTerm());

        Snapshot b = page.awaitSnapshot(B_HEADING, 4);

        assertEquals(
                readCsv("TC_36_B_expected.csv"),
                b.rows(),
                "TC_36: Hoc phan B khong khop baseline."
        );

        verifySummaryB(b);

        System.out.println(
                "[TC_36] B: 4 hoc phan chua co diem MATCH."
        );
    }

    @When("sinh viên quay lại học kỳ A TC_36")
    public void returnA() {

        page.select("2024-2025", "Học kỳ 2");
    }

    @Then("dữ liệu học kỳ A TC_36 khôi phục nguyên vẹn")
    public void verifyReturnA() {

        assertEquals("2024-2025", page.selectedYear());
        assertEquals("Học kỳ 2", page.selectedTerm());

        Snapshot secondA =
                page.awaitSnapshot(A_HEADING, 6);

        assertEquals(
                readCsv("TC_36_A_expected.csv"),
                secondA.rows(),
                "TC_36: A lan 2 sai baseline."
        );

        // So sánh toàn bộ dữ liệu A lần 1 và lần 2.
        assertEquals(
                firstA,
                secondA,
                "TC_36: Du lieu A thay doi sau khi A -> B -> A."
        );

        System.out.println(
                "[TC_36] A -> B -> A: MATCH."
        );
    }

    private void verifySummaryA(Snapshot snapshot) {

        List<String> left = snapshot.summaryLeft();
        List<String> right = snapshot.summaryRight();

        assertEquals(4, left.size());
        assertEquals(3, right.size());

        assertEquals(
                "- Tổng số tín chỉ: 18",
                left.get(0)
        );

        assertEquals(
                "- Số tín chỉ đạt: 18 Số tín chỉ không đạt: 0",
                left.get(1)
        );

        assertEquals(
                "- Điểm trung bình học kỳ (Hệ 10): 7.24",
                left.get(2)
        );

        assertEquals(
                "- Điểm trung bình học kỳ (Hệ 4): 2.93 "
                        + "- Điểm rèn luyện: 100 "
                        + "- Xếp loại (RL): Xuất sắc",
                left.get(3)
        );

        assertEquals(
                "- Số tín chỉ tích lũy: 97",
                right.get(0)
        );

        assertEquals(
                "- Điểm trung bình tích lũy (Hệ 10): 6.55",
                right.get(1)
        );

        assertEquals(
                "- Điểm trung bình tích lũy (Hệ 4): 2.58",
                right.get(2)
        );
    }

    private void verifySummaryB(Snapshot snapshot) {

        List<String> left = snapshot.summaryLeft();
        List<String> right = snapshot.summaryRight();

        assertEquals(4, left.size());
        assertEquals(3, right.size());

        assertEquals(
                "- Tổng số tín chỉ: 17",
                left.get(0)
        );

        assertEquals(
                "- Số tín chỉ đạt: 0 Số tín chỉ không đạt: 0",
                left.get(1)
        );

        assertEquals(
                "- Điểm trung bình học kỳ (Hệ 10):",
                left.get(2)
        );

        assertEquals(
                "- Điểm trung bình học kỳ (Hệ 4): "
                        + "- Điểm rèn luyện: "
                        + "- Xếp loại (RL):",
                left.get(3)
        );

        assertEquals(
                "- Số tín chỉ tích lũy:",
                right.get(0)
        );

        assertEquals(
                "- Điểm trung bình tích lũy (Hệ 10):",
                right.get(1)
        );

        assertEquals(
                "- Điểm trung bình tích lũy (Hệ 4):",
                right.get(2)
        );

        for (MarkRow row : snapshot.rows()) {

            assertTrue(row.d10().isBlank());
            assertTrue(row.d4().isBlank());
            assertTrue(row.chu().isBlank());

            assertEquals(
                    "CHUA_CO",
                    row.ketQua(),
                    "TC_36: Hoc phan chua co diem co ket qua."
            );
        }
    }

    private List<MarkRow> readCsv(String filename) {

        String resource =
                "baselines/marks/" + filename;

        InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream(resource);

        if (input == null) {
            throw new IllegalStateException(
                    "TC_36: Khong tim thay " + resource
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

            assertEquals(
                    List.of(
                            "tt", "ma", "ten", "tc",
                            "d10", "d4", "chu", "ketQua"
                    ),
                    csv.getHeaderNames(),
                    "TC_36: Sai header CSV."
            );

            List<MarkRow> result = new ArrayList<>();

            for (CSVRecord row : csv) {

                if (!row.isConsistent() || row.size() != 8) {
                    throw new IllegalStateException(
                            "TC_36: Sai cot CSV dong "
                                    + row.getRecordNumber()
                    );
                }

                result.add(new MarkRow(
                        row.get(0).strip(),
                        row.get(1).strip(),
                        row.get(2).strip(),
                        row.get(3).strip(),
                        row.get(4).strip(),
                        row.get(5).strip(),
                        row.get(6).strip(),
                        row.get(7).strip()
                ));
            }

            return List.copyOf(result);

        } catch (Exception ex) {

            throw new IllegalStateException(
                    "TC_36: Khong doc duoc " + resource,
                    ex
            );
        }
    }
}
