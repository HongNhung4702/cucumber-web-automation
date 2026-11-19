
package com.hongnhung.cucumberweb.steps.lecturers;

import com.hongnhung.cucumberweb.driver.DriverManager;
import com.hongnhung.cucumberweb.pages.lecturers.LecturerInvalidSearchPage;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class LecturerInvalidSearchSteps {

    private final LecturerInvalidSearchPage page;

    public LecturerInvalidSearchSteps(DriverManager manager) {
        this.page = new LecturerInvalidSearchPage(manager);
    }

    @When("người dùng tra cứu giảng viên với từ khóa không hợp lệ TC_39")
    public void searchInvalidLecturer() {

        page.search();

        System.out.println(
                "[TC_39] Da loc voi tu khoa @$#ff."
        );
    }

    @Then("bộ lọc giảng viên TC_39 giữ đúng dữ liệu đã chọn")
    public void verifyFilters() {

        assertTrue(
                page.filtersMatch(),
                "TC_39: Bo loc khong khop du lieu test."
        );
    }

    @Then("TC_39 không có lịch giảng dạy và không hiển thị lỗi")
    public void verifyEmptyResult() {

        assertTrue(
                page.hasEmptyResult(),
                "TC_39: Khong hien thi Chua co lich hoc."
        );

        assertEquals(
                0,
                page.dataRowCount(),
                "TC_39: Van co dong lich giang day."
        );

        assertEquals(
                0,
                page.visibleErrorCount(),
                "TC_39: Co thong bao loi tren giao dien."
        );

        System.out.println(
                "[TC_39] Chua co lich hoc: MATCH."
        );
    }
}
