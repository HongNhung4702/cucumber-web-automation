
package com.hongnhung.cucumberweb.reporting;

import io.qameta.allure.listener.TestLifecycleListener;
import io.qameta.allure.model.Label;
import io.qameta.allure.model.TestResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

/**
 * Phan nhom 32 Test Case Cucumber trong Allure.
 *
 * Cap 1: Kiem thu he thong dao tao QNU
 * Cap 2: Nhom chuc nang
 * Cap 3: Ma TC va ten Test Case
 * Cap 4: Scenario
 *
 * Chi thay doi metadata Allure, khong thay doi test.
 */
public final class AllureGrouping
        implements TestLifecycleListener {

    private static final String ROOT =
            "Kiểm thử hệ thống đào tạo QNU";

    private record TestCaseInfo(
            String group,
            String name
    ) {
    }

    private static final String AUTH =
            "01 - Đăng nhập và bảo mật";

    private static final String NOTIFICATIONS =
            "02 - Thông báo cá nhân";

    private static final String UNACCUMULATED =
            "03 - Học phần chưa tích lũy";

    private static final String GRADUATION =
            "04 - Tốt nghiệp";

    private static final String PROGRESS =
            "05 - Tiến độ học tập";

    private static final String SCHEDULES =
            "06 - Thời khóa biểu sinh viên và phòng học";

    private static final String EXAMS =
            "07 - Lịch thi";

    private static final String MARKS =
            "08 - Bảng điểm";

    private static final String LECTURERS =
            "09 - Thời khóa biểu giảng viên";

    private static final Map<String, TestCaseInfo> TEST_CASES =
            Map.ofEntries(

                    // Dang nhap va bao mat
                    entry("TC_01", new TestCaseInfo(
                            AUTH, "Đăng nhập thành công"
                    )),
                    entry("TC_02", new TestCaseInfo(
                            AUTH, "Đăng nhập sai mật khẩu"
                    )),
                    entry("TC_03", new TestCaseInfo(
                            AUTH, "Đăng nhập sai tên đăng nhập"
                    )),
                    entry("TC_04", new TestCaseInfo(
                            AUTH, "Kiểm tra che mật khẩu"
                    )),
                    entry("TC_06", new TestCaseInfo(
                            AUTH, "Vô hiệu hóa phiên giữa hai tab"
                    )),

                    // Thong bao ca nhan
                    entry("TC_10", new TestCaseInfo(
                            NOTIFICATIONS,
                            "Hiển thị thông báo cá nhân"
                    )),
                    entry("TC_11", new TestCaseInfo(
                            NOTIFICATIONS,
                            "Tìm kiếm thông báo hợp lệ"
                    )),
                    entry("TC_12", new TestCaseInfo(
                            NOTIFICATIONS,
                            "Tìm kiếm thông báo không tồn tại"
                    )),

                    // Hoc phan chua tich luy
                    entry("TC_17", new TestCaseInfo(
                            UNACCUMULATED,
                            "Danh sách học phần chưa tích lũy"
                    )),

                    // Tot nghiep
                    entry("TC_18", new TestCaseInfo(
                            GRADUATION,
                            "Điều kiện tốt nghiệp"
                    )),
                    entry("TC_19", new TestCaseInfo(
                            GRADUATION,
                            "Kết quả xét tốt nghiệp"
                    )),

                    // Tien do hoc tap
                    entry("TC_20", new TestCaseInfo(
                            PROGRESS,
                            "Kiểm tra tiến độ học tập"
                    )),

                    // Thoi khoa bieu sinh vien va phong hoc
                    entry("TC_21", new TestCaseInfo(
                            SCHEDULES,
                            "Thời khóa biểu lớp học phần"
                    )),
                    entry("TC_22", new TestCaseInfo(
                            SCHEDULES,
                            "Thời khóa biểu theo tuần"
                    )),
                    entry("TC_23", new TestCaseInfo(
                            SCHEDULES,
                            "Thời khóa biểu theo phòng"
                    )),
                    entry("TC_24", new TestCaseInfo(
                            SCHEDULES,
                            "Chuyển tuần thời khóa biểu sinh viên"
                    )),
                    entry("TC_25", new TestCaseInfo(
                            SCHEDULES,
                            "Chuyển tuần thời khóa biểu phòng học"
                    )),
                    entry("TC_26", new TestCaseInfo(
                            SCHEDULES,
                            "Bản xem trước thời khóa biểu sinh viên"
                    )),

                    // Lich thi
                    entry("TC_28", new TestCaseInfo(
                            EXAMS,
                            "Lịch thi theo năm học và học kỳ"
                    )),
                    entry("TC_29", new TestCaseInfo(
                            EXAMS,
                            "Thay đổi năm học lịch thi"
                    )),
                    entry("TC_30", new TestCaseInfo(
                            EXAMS,
                            "Thay đổi học kỳ lịch thi"
                    )),
                    entry("TC_31", new TestCaseInfo(
                            EXAMS,
                            "Tra cứu lịch thi theo khoảng ngày"
                    )),
                    entry("TC_32", new TestCaseInfo(
                            EXAMS,
                            "Tra cứu lịch thi với khoảng ngày không hợp lệ"
                    )),

                    // Bang diem
                    entry("TC_33", new TestCaseInfo(
                            MARKS,
                            "Kết quả học tập theo học kỳ"
                    )),
                    entry("TC_34", new TestCaseInfo(
                            MARKS,
                            "Bảng điểm theo chương trình đào tạo"
                    )),
                    entry("TC_35", new TestCaseInfo(
                            MARKS,
                            "Chi tiết điểm học phần"
                    )),
                    entry("TC_36", new TestCaseInfo(
                            MARKS,
                            "Chuyển đổi học kỳ xem điểm"
                    )),

                    // Thoi khoa bieu giang vien
                    entry("TC_38", new TestCaseInfo(
                            LECTURERS,
                            "Tra cứu thời khóa biểu giảng viên"
                    )),
                    entry("TC_39", new TestCaseInfo(
                            LECTURERS,
                            "Tra cứu giảng viên không hợp lệ"
                    )),
                    entry("TC_40", new TestCaseInfo(
                            LECTURERS,
                            "Làm mới điều kiện tra cứu"
                    )),
                    entry("TC_41", new TestCaseInfo(
                            LECTURERS,
                            "Tra cứu lại sau khi làm mới"
                    )),
                    entry("TC_42", new TestCaseInfo(
                            LECTURERS,
                            "Tra cứu giảng viên liên tiếp A-B-A"
                    ))
            );

    @Override
    public void beforeTestWrite(TestResult result) {

        if (!isCucumberResult(result)) {
            return;
        }

        String testCaseId = getTestCaseId(result);

        if (testCaseId == null) {
            return;
        }

        TestCaseInfo info = TEST_CASES.get(testCaseId);

        if (info == null) {
            return;
        }

        List<Label> labels = result.getLabels();

        if (labels == null) {
            labels = new ArrayList<>();
            result.setLabels(labels);
        }

        // Xoa nhan phan nhom do Cucumber gan truoc do.
        labels.removeIf(label ->
                "parentSuite".equals(label.getName())
                        || "suite".equals(label.getName())
                        || "subSuite".equals(label.getName())
        );

        // Cap 1: He thong
        labels.add(
                new Label()
                        .setName("parentSuite")
                        .setValue(ROOT)
        );

        // Cap 2: Nhom chuc nang
        labels.add(
                new Label()
                        .setName("suite")
                        .setValue(info.group())
        );

        // Cap 3: Test Case
        labels.add(
                new Label()
                        .setName("subSuite")
                        .setValue(
                                testCaseId + " - " + info.name()
                        )
        );

        // Ten Scenario duoc Cucumber cung cap san.
    }

    private boolean isCucumberResult(TestResult result) {

        List<Label> labels = result.getLabels();

        if (labels == null) {
            return false;
        }

        return labels.stream().anyMatch(label ->
                "framework".equals(label.getName())
                        && "cucumber4jvm".equals(
                        label.getValue()
                )
        );
    }

    private String getTestCaseId(TestResult result) {

        List<Label> labels = result.getLabels();

        if (labels == null) {
            return null;
        }

        return labels.stream()
                .filter(label ->
                        "tag".equals(label.getName())
                )
                .map(Label::getValue)
                .filter(value -> value != null)
                .map(value ->
                        value.startsWith("@")
                                ? value.substring(1)
                                : value
                )
                .filter(TEST_CASES::containsKey)
                .findFirst()
                .orElse(null);
    }
}
