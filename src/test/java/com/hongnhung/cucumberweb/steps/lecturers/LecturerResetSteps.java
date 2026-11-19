
package com.hongnhung.cucumberweb.steps.lecturers;

import com.hongnhung.cucumberweb.driver.DriverManager;
import com.hongnhung.cucumberweb.pages.lecturers.LecturerResetPage;
import com.hongnhung.cucumberweb.pages.lecturers.LecturerResetPage.FilterState;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public final class LecturerResetSteps {

    private final LecturerResetPage page;
    private FilterState initial;

    public LecturerResetSteps(DriverManager manager) {
        page = new LecturerResetPage(manager);
    }

    @When("người dùng tra cứu lịch giảng viên TC_40")
    public void search() {

        page.open();

        initial = page.filters();

        assertEquals(
                page.defaultFilters(),
                initial,
                "TC_40: Trang ban dau khong dung baseline."
        );

        page.searchTeacher();

        assertEquals(
                "Khoa Công nghệ thông tin",
                page.filters().faculty()
        );

        assertEquals(
                "Phạm Trần Thiện",
                page.filters().teacher()
        );

        System.out.println(
                "[TC_40] Tim kiem giang vien: MATCH."
        );
    }

    @When("người dùng nhấn Làm mới TC_40")
    public void reset() {
        page.reset();
    }

    @Then("bộ lọc TC_40 trở về giá trị mặc định")
    public void verifyFilters() {

        assertEquals(
                initial,
                page.filters(),
                "TC_40: Bo loc khong tro ve mac dinh."
        );
    }

    @Then("bảng lịch TC_40 trở về trạng thái rỗng")
    public void verifyTable() {

        assertTrue(
                page.emptyResult(),
                "TC_40: Bang khong hien thi Chua co lich hoc."
        );

        System.out.println(
                "[TC_40] Lam moi: MATCH."
        );
    }
}
