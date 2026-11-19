
package com.hongnhung.cucumberweb.steps.marks;

import com.hongnhung.cucumberweb.pages.student.marks.CourseMarkDetailPage;

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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class CourseMarkDetailSteps {

    private static final String RESOURCE =
            "baselines/marks/TC_35_detail_expected.csv";

    private final CourseMarkDetailPage page;

    private CourseMarkDetailPage.Summary before;

    public CourseMarkDetailSteps(CourseMarkDetailPage page) {
        this.page = page;
    }

    @When("sinh viên mở chi tiết điểm Toán logic TC_35")
    public void openDetail() {

        page.open();

        assertEquals(
                "Năm học: 2022-2023 - Học kỳ: HK01",
                page.semesterHeading(),
                "TC_35: Sai nam hoc hoac hoc ky."
        );

        before = page.summary();

        assertEquals("1050074", before.code());
        assertEquals("Toán logic", before.name());
        assertEquals("2", before.credits());
        assertEquals("7.5", before.grade10());
        assertEquals("3.00", before.grade4());
        assertEquals("B+", before.letter());
        assertEquals("DAT", before.result());

        page.expand();
    }

    @Then("bảng chi tiết điểm Toán logic TC_35 khớp baseline")
    public void verifyDetails() {

        assertEquals(
                "Chi tiết học phần: 1050074 - Toán logic",
                page.detailHeading(),
                "TC_35: Sai tieu de chi tiet."
        );

        assertEquals(
                List.of(
                        "STT",
                        "Tên thành phần",
                        "Điểm lần 1",
                        "Điểm lần 2"
                ),
                page.detailHeaders(),
                "TC_35: Sai tieu de cot."
        );

        assertEquals(
                readBaseline(),
                page.details(),
                "TC_35: Diem chi tiet khong khop baseline."
        );

        // Kiểm tra điểm tổng hợp không thay đổi
        // khi mở bảng chi tiết.
        assertEquals(
                before,
                page.summary(),
                "TC_35: Diem tong hop bi thay doi "
                        + "sau khi mo chi tiet."
        );

        System.out.println(
                "[TC_35] Chi tiet 2 thanh phan: MATCH."
        );
    }

    @Then("đóng chi tiết TC_35 không làm thay đổi điểm tổng hợp")
    public void verifyCollapse() {

        page.collapse();

        assertEquals(
                before,
                page.summary(),
                "TC_35: Diem tong hop bi thay doi "
                        + "sau khi dong chi tiet."
        );

        System.out.println(
                "[TC_35] Mo/dong chi tiet: MATCH."
        );
    }

    private List<CourseMarkDetailPage.Detail> readBaseline() {

        InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream(RESOURCE);

        if (input == null) {
            throw new IllegalStateException(
                    "TC_35: Khong tim thay baseline " + RESOURCE
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

            assertEquals(
                    List.of(
                            "stt",
                            "tenThanhPhan",
                            "diemLan1",
                            "diemLan2"
                    ),
                    headers,
                    "TC_35: Sai header CSV."
            );

            List<CourseMarkDetailPage.Detail> result =
                    new ArrayList<>();

            for (CSVRecord record : csv) {

                if (!record.isConsistent()
                        || record.size() != 4) {
                    throw new IllegalStateException(
                            "TC_35: Sai cau truc CSV tai dong "
                                    + record.getRecordNumber()
                    );
                }

                result.add(
                        new CourseMarkDetailPage.Detail(
                                clean(record.get(0)),
                                clean(record.get(1)),
                                clean(record.get(2)),
                                clean(record.get(3))
                        )
                );
            }

            assertEquals(
                    2,
                    result.size(),
                    "TC_35: Baseline phai co 2 thanh phan."
            );

            return List.copyOf(result);

        } catch (Exception ex) {
            throw new IllegalStateException(
                    "TC_35: Khong doc duoc baseline.",
                    ex
            );
        }
    }

    private String clean(String value) {

        return Normalizer.normalize(
                value.replace('\u00A0', ' ')
                        .replace('\u202F', ' ')
                        .strip()
                        .replaceAll("\\s+", " "),
                Normalizer.Form.NFC
        );
    }
}
