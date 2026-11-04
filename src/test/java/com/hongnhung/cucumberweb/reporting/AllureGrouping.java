
package com.hongnhung.cucumberweb.reporting;

import io.qameta.allure.listener.TestLifecycleListener;
import io.qameta.allure.model.Label;
import io.qameta.allure.model.TestResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Cấu hình phân nhóm Allure tập trung.
 *
 * Áp dụng cho:
 * - Cucumber: Kiểm thử giao diện Web
 * - JUnit: Kiểm chứng bộ so sánh dữ liệu
 *
 * Cấu trúc:
 * Hệ thống đào tạo QNU
 *   -> Test Case
 *       -> Framework
 *           -> Test Result
 */
public final class AllureGrouping
        implements TestLifecycleListener {

    // =========================================
    // 1. TÊN NHÓM ALLURE
    // =========================================

    private static final String ROOT =
            "Kiểm thử hệ thống đào tạo QNU";

    private static final String CUCUMBER =
            "Cucumber - Kiểm thử giao diện";

    private static final String JUNIT =
            "JUnit - Kiểm chứng Comparator";

    // =========================================
    // 2. DANH SÁCH TEST CASE
    // =========================================

    private static final Map<String, String> TEST_CASE_NAMES =
            Map.of(
                    "TC_17", "Học phần chưa tích lũy",
                    "TC_21", "Thời khóa biểu lớp học phần",
                    "TC_28", "Lịch thi",
                    "TC_33", "Kết quả học tập",
                    "TC_38", "Thời khóa biểu giảng viên"
            );

    // =========================================
    // 3. ÁNH XẠ CLASS JUNIT VỚI TEST CASE
    // =========================================

    private static final Map<String, String> JUNIT_CLASSES =
            Map.of(
                    "ExamScheduleComparatorTest", "TC_28",
                    "MarksComparatorTest", "TC_33",
                    "SchedulesComparatorTest", "TC_21",
                    "LecturerScheduleComparatorTest", "TC_38",
                    "UnaccumulatedCourseComparatorTest", "TC_17"
            );

    // =========================================
    // 4. XỬ LÝ KẾT QUẢ TRƯỚC KHI ALLURE GHI
    // =========================================

    @Override
    public void beforeTestWrite(TestResult result) {

        String framework = getFramework(result);

        String testCaseId;
        String subSuite;

        if ("cucumber4jvm".equals(framework)) {

            testCaseId = getCucumberTestCaseId(result);
            subSuite = CUCUMBER;

        } else if ("junit-platform".equals(framework)) {

            testCaseId = getJUnitTestCaseId(result);
            subSuite = JUNIT;

        } else {

            return;
        }

        String testCaseName =
                TEST_CASE_NAMES.get(testCaseId);

        if (testCaseName == null) {

            // Không thay đổi các bài kiểm thử khác.
            return;
        }

        List<Label> labels = result.getLabels();

        if (labels == null) {

            labels = new ArrayList<>();
            result.setLabels(labels);
        }

        // Xóa toàn bộ nhãn phân nhóm cũ
        // do adapter hoặc cấu hình trước đó sinh ra.

        labels.removeIf(label ->
                "parentSuite".equals(label.getName())
                        || "suite".equals(label.getName())
                        || "subSuite".equals(label.getName())
        );

        // Cấp 1: Hệ thống

        labels.add(
                new Label()
                        .setName("parentSuite")
                        .setValue(ROOT)
        );

        // Cấp 2: Test Case

        labels.add(
                new Label()
                        .setName("suite")
                        .setValue(
                                testCaseId + " - " + testCaseName
                        )
        );

        // Cấp 3: Công cụ kiểm thử

        labels.add(
                new Label()
                        .setName("subSuite")
                        .setValue(subSuite)
        );
    }

    // =========================================
    // 5. XÁC ĐỊNH FRAMEWORK
    // =========================================

    private String getFramework(TestResult result) {

        if (result.getLabels() == null) {
            return "";
        }

        return result.getLabels()
                .stream()
                .filter(label ->
                        "framework".equals(label.getName())
                )
                .map(Label::getValue)
                .filter(value -> value != null)
                .findFirst()
                .orElse("");
    }

    // =========================================
    // 6. XÁC ĐỊNH TEST CASE CUCUMBER
    // =========================================

    private String getCucumberTestCaseId(
            TestResult result
    ) {

        if (result.getLabels() == null) {
            return null;
        }

        return result.getLabels()
                .stream()
                .filter(label ->
                        "tag".equals(label.getName())
                )
                .map(Label::getValue)
                .filter(value -> value != null)
                .filter(TEST_CASE_NAMES::containsKey)
                .findFirst()
                .orElse(null);
    }

    // =========================================
    // 7. XÁC ĐỊNH TEST CASE JUNIT
    // =========================================

    private String getJUnitTestCaseId(
            TestResult result
    ) {

        String fullName = result.getFullName();

        if (fullName == null) {
            return null;
        }

        for (Map.Entry<String, String> entry
                : JUNIT_CLASSES.entrySet()) {

            String className = entry.getKey();

            if (fullName.contains(
                    "." + className + "."
            )) {

                return entry.getValue();
            }
        }

        return null;
    }
}
