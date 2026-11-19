
package com.hongnhung.cucumberweb.steps.lecturers;

import com.hongnhung.cucumberweb.assertions.lecturers.LecturerScheduleComparator;
import com.hongnhung.cucumberweb.models.lecturers.LecturerScheduleRecord;
import com.hongnhung.cucumberweb.pages.publicpages.LecturerTimetablePage;
import com.hongnhung.cucumberweb.testdata.lecturers.LecturerScheduleBaselineReader;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public final class LecturerConsecutiveSearchSteps {

    private static final String A_YEAR = "2026-2027";
    private static final String A_TERM = "Học kỳ 1";
    private static final String A_FACULTY = "Khoa Công nghệ thông tin";
    private static final String A_TEACHER = "Phạm Trần Thiện";

    private static final String B_YEAR = "2023-2024";
    private static final String B_TERM = "Học kỳ 1";
    private static final String B_FACULTY = "Khoa Giáo dục thể chất";
    private static final String B_TEACHER = "Bùi Trọng Duy";

    private static final String BASELINE_B =
            "baselines/lecturers/TC_42_B_expected.csv";

    private final LecturerTimetablePage page;
    private final LecturerScheduleBaselineReader baselineReader;
    private final LecturerScheduleComparator comparator;

    private List<LecturerScheduleRecord> firstA;

    public LecturerConsecutiveSearchSteps(
            LecturerTimetablePage page,
            LecturerScheduleBaselineReader baselineReader
    ) {
        this.page = page;
        this.baselineReader = baselineReader;
        this.comparator = new LecturerScheduleComparator();
    }

    @When("người dùng tra cứu bộ lọc A TC_42")
    public void searchA() {

        page.open();

        search(
                A_YEAR, A_TERM, A_FACULTY, A_TEACHER
        );

        firstA = page.readRecords();

        assertEquals(
                34,
                firstA.size(),
                "TC_42: Số dòng A không đúng."
        );

        compare(
                baselineReader.readRecords(),
                firstA,
                "TC_42: A lần đầu sai Baseline TC_38."
        );

        System.out.println(
                "[TC_42] A lần 1: 34 dòng MATCH."
        );
    }

    @When("người dùng tra cứu bộ lọc B TC_42")
    public void searchB() {

        search(
                B_YEAR, B_TERM, B_FACULTY, B_TEACHER
        );

        List<LecturerScheduleRecord> actualB =
                page.readRecords();

        List<LecturerScheduleRecord> expectedB =
                readBaselineB();

        assertEquals(
                13,
                actualB.size(),
                "TC_42: Số dòng B không đúng."
        );

        compare(
                expectedB,
                actualB,
                "TC_42: B sai Baseline."
        );

        assertTrue(
                page.headingMatches(
                        B_YEAR, "HK01", B_FACULTY
                ),
                "TC_42: Tiêu đề B không đúng."
        );

        System.out.println(
                "[TC_42] B: 13 dòng MATCH."
        );
    }

    @Then("người dùng quay lại A TC_42 và dữ liệu không thay đổi")
    public void returnA() {

        search(
                A_YEAR, A_TERM, A_FACULTY, A_TEACHER
        );

        List<LecturerScheduleRecord> secondA =
                page.readRecords();

        compare(
                baselineReader.readRecords(),
                secondA,
                "TC_42: A lần cuối sai Baseline TC_38."
        );

        compare(
                firstA,
                secondA,
                "TC_42: A lần cuối khác A lần đầu."
        );

        assertTrue(
                page.headingMatches(
                        A_YEAR, "HK01", A_FACULTY
                ),
                "TC_42: Tiêu đề A cuối không đúng."
        );

        System.out.println(
                "[TC_42] A -> B -> A: PASS."
        );
    }

    private void search(
            String year,
            String term,
            String faculty,
            String teacher
    ) {

        page.selectFilters(
                year, term, faculty, teacher
        );

        page.clickFilter(
                year, term, faculty, teacher
        );

        assertTrue(
                page.filtersMatch(
                        year, term, faculty, teacher
                ),
                "TC_42: Bộ lọc không đúng."
        );
    }

    private void compare(
            List<LecturerScheduleRecord> expected,
            List<LecturerScheduleRecord> actual,
            String message
    ) {

        LecturerScheduleComparator.Result result =
                comparator.compare(expected, actual);

        assertTrue(
                result.passed(),
                message + "\n" + result.details()
        );
    }

    private List<LecturerScheduleRecord> readBaselineB() {

        InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream(BASELINE_B);

        if (input == null) {
            throw new IllegalStateException(
                    "TC_42: Không tìm thấy " + BASELINE_B
            );
        }

        try (
                InputStream stream = input;
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                stream,
                                StandardCharsets.UTF_8
                        )
                )
        ) {

            List<LecturerScheduleRecord> records =
                    new ArrayList<>();

            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {

                lineNumber++;

                if (line.isBlank()) {
                    continue;
                }

                String[] c = line.split("\\|", -1);

                if (c.length != 13) {
                    throw new IllegalStateException(
                            "TC_42: Baseline B dòng "
                                    + lineNumber
                                    + " phải có 13 cột, thực tế "
                                    + c.length
                    );
                }

                for (int i = 0; i < c.length; i++) {
                    c[i] = c[i].strip();
                }

                records.add(new LecturerScheduleRecord(
                        c[0], c[1], c[2], c[3], c[4],
                        c[5], c[6], c[7], c[8], c[9],
                        c[10], c[11], c[12]
                ));
            }

            assertEquals(
                    13,
                    records.size(),
                    "TC_42: Baseline B phải có 13 dòng."
            );

            return List.copyOf(records);

        } catch (Exception ex) {
            throw new IllegalStateException(
                    "TC_42: Không đọc được Baseline B.",
                    ex
            );
        }
    }
}
