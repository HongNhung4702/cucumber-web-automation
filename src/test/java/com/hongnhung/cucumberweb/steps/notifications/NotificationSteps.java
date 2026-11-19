
package com.hongnhung.cucumberweb.steps.notifications;

import com.hongnhung.cucumberweb.assertions.notifications.NotificationComparator;
import com.hongnhung.cucumberweb.models.notifications.NotificationRecord;
import com.hongnhung.cucumberweb.pages.student.notifications.NotificationPage;
import com.hongnhung.cucumberweb.testdata.notifications.NotificationBaselineReader;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class NotificationSteps {

    private final NotificationPage notificationPage;
    private final NotificationBaselineReader baselineReader;

    private final NotificationComparator comparator =
            new NotificationComparator();

    private List<NotificationRecord> expected;
    private List<NotificationRecord> actual;

    private String currentKeyword;

    public NotificationSteps(
            NotificationPage notificationPage,
            NotificationBaselineReader baselineReader
    ) {
        this.notificationPage = notificationPage;
        this.baselineReader = baselineReader;
    }

    // =========================================
    // TC_10 - DANH SACH THONG BAO
    // =========================================

    @When("sinh viên mở danh sách thông báo cá nhân")
    public void openPersonalNotifications() {

        resetData();

        notificationPage.open();

        System.out.println(
                "[TC_10] Da mo Thong bao ca nhan."
        );
    }

    @Then("bảng thông báo cá nhân hiển thị các cột đã xác nhận")
    public void verifyHeaders() {

        assertEquals(
                notificationPage.expectedHeaders(),
                notificationPage.headers(),
                "TC_10: Sai cau truc bang thong bao."
        );
    }

    @Then("danh sách thông báo cá nhân có dữ liệu")
    public void verifyHasData() {

        assertTrue(
                !getActual().isEmpty(),
                "TC_10: Danh sach thong bao rong."
        );

        System.out.println(
                "[TC_10] So dong: "
                        + getActual().size()
        );
    }

    @Then("danh sách thông báo khớp dữ liệu tham chiếu TC_10")
    public void verifyBaseline() {

        NotificationComparator.Result result =
                comparator.compare(
                        getExpected(),
                        getActual()
                );

        assertTrue(
                result.passed(),
                "TC_10: Du lieu khong khop baseline. "
                        + result.details()
        );

        System.out.println(
                "[TC_10] Baseline: MATCH."
        );

        System.out.println(
                "[TC_10] " + result.details()
        );
    }

    // =========================================
    // TC_11 - TIM KIEM CO KET QUA
    // =========================================

    @When("sinh viên tìm kiếm thông báo cá nhân với từ khóa {string}")
    public void searchPersonalNotifications(String keyword) {

        resetData();

        notificationPage.open();

        currentKeyword = keyword;

        notificationPage.search(keyword);

        assertEquals(
                keyword,
                notificationPage.searchKeyword(),
                "TC_11: O tim kiem khong nhan dung tu khoa."
        );
    }

    @Then("kết quả tìm kiếm thông báo khớp dữ liệu tham chiếu TC_11")
    public void verifySearchResults() {

        if (currentKeyword == null
                || currentKeyword.isBlank()) {

            throw new IllegalStateException(
                    "TC_11: Chua nhap tu khoa."
            );
        }

        List<NotificationRecord> matchingExpected =
                getExpected()
                        .stream()
                        .filter(record ->
                                record.title()
                                        .contains(currentKeyword)
                        )
                        .toList();

        assertEquals(
                3,
                matchingExpected.size(),
                "TC_11: Baseline khong khop "
                        + "voi 3 thong bao da khao sat."
        );

        List<NotificationRecord> matchingActual =
                getActual();

        NotificationComparator.Result result =
                comparator.compare(
                        matchingExpected,
                        matchingActual
                );

        assertTrue(
                result.missing().isEmpty()
                        && result.extra().isEmpty(),
                "TC_11: Ket qua tim kiem sai lech. "
                        + result.details()
        );

        assertEquals(
                matchingExpected.size(),
                matchingActual.size(),
                "TC_11: Sai so luong ket qua."
        );

        System.out.println(
                "[TC_11] Tu khoa: " + currentKeyword
        );

        System.out.println(
                "[TC_11] Du lieu tim kiem: MATCH."
        );
    }

    // =========================================
    // TC_12 - TIM KIEM KHONG CO KET QUA
    // =========================================

    @When("sinh viên tìm kiếm thông báo không tồn tại với từ khóa {string}")
    public void searchNonexistentNotification(String keyword) {

        resetData();

        notificationPage.open();

        currentKeyword = keyword;

        // Xac nhan baseline khong chua tu khoa
        boolean baselineContainsKeyword =
                getExpected()
                        .stream()
                        .anyMatch(record ->
                                record.title().contains(keyword)
                                        || record.sender().contains(keyword)
                                        || record.date().contains(keyword)
                        );

        assertTrue(
                !baselineContainsKeyword,
                "TC_12: Tu khoa da ton tai trong baseline."
        );

        notificationPage.search(keyword);

        assertEquals(
                keyword,
                notificationPage.searchKeyword(),
                "TC_12: O tim kiem khong nhan dung tu khoa."
        );
    }

    @Then("danh sách thông báo hiển thị trạng thái không có kết quả")
    public void verifyEmptySearchResults() {

        assertTrue(
                notificationPage.waitForEmptyState(),
                "TC_12: Khong hien thi dong "
                        + "trang thai rong (...)."
        );

        List<NotificationRecord> actualRecords =
                notificationPage.records();

        assertTrue(
                actualRecords.isEmpty(),
                "TC_12: Van con thong bao "
                        + "sau khi tim kiem tu khoa "
                        + currentKeyword
        );

        System.out.println(
                "[TC_12] Tu khoa: " + currentKeyword
        );

        System.out.println(
                "[TC_12] Trang thai rong: MATCH."
        );

        System.out.println(
                "[TC_12] So thong bao: 0."
        );
    }

    // =========================================
    // DUNG CHUNG
    // =========================================

    private void resetData() {

        expected = null;
        actual = null;
        currentKeyword = null;
    }

    private List<NotificationRecord> getExpected() {

        if (expected == null) {
            expected = baselineReader.readRecords();
        }

        return expected;
    }

    private List<NotificationRecord> getActual() {

        if (actual == null) {
            actual = notificationPage.records();
        }

        return actual;
    }
}
