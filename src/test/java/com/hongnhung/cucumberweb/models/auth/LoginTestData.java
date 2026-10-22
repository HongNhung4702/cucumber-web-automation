package com.hongnhung.cucumberweb.models.auth;

public final class LoginTestData {

    private final String testCaseId;
    private final String username;
    private final String password;
    private final String expectedResult;
    private final String expectedMessage;

    public LoginTestData(
            String testCaseId,
            String username,
            String password,
            String expectedResult,
            String expectedMessage
    ) {
        this.testCaseId = testCaseId;
        this.username = username;
        this.password = password;
        this.expectedResult = expectedResult;
        this.expectedMessage = expectedMessage;
    }

    public String getTestCaseId() {
        return testCaseId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getExpectedResult() {
        return expectedResult;
    }

    public String getExpectedMessage() {
        return expectedMessage;
    }
}