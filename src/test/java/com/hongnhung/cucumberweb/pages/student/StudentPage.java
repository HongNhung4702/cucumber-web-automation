package com.hongnhung.cucumberweb.pages.student;

import com.hongnhung.cucumberweb.driver.DriverManager;
import com.hongnhung.cucumberweb.pages.BasePage;

public final class StudentPage extends BasePage {

    public StudentPage(DriverManager driverManager) {
        super(driverManager);
    }

    public boolean isAtStudentPage() {
        return waitUntilUrlContains("/student");
    }
}