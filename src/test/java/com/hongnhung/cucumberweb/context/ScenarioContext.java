package com.hongnhung.cucumberweb.context;

public final class ScenarioContext {

    private String testCaseId;

    public void setTestCaseId(String testCaseId) {

        if (testCaseId == null || testCaseId.isBlank()) {
            throw new IllegalArgumentException(
                    "Test case ID must not be blank."
            );
        }

        this.testCaseId = testCaseId.trim();
    }

    public String getTestCaseId() {

        if (testCaseId == null || testCaseId.isBlank()) {
            throw new IllegalStateException(
                    "Test case ID has not been initialized for this scenario."
            );
        }

        return testCaseId;
    }

    public boolean hasTestCaseId() {
        return testCaseId != null
                && !testCaseId.isBlank();
    }
}