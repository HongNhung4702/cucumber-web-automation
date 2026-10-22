package com.hongnhung.cucumberweb.testdata.auth;

import com.hongnhung.cucumberweb.models.auth.LoginTestData;
import com.hongnhung.cucumberweb.testdata.TestDataValueResolver;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public final class LoginTestDataRepository {

    private static final String TEST_DATA_FILE =
            "testdata/auth/login-test-data.csv";

    private final TestDataValueResolver valueResolver;

    public LoginTestDataRepository(
            TestDataValueResolver valueResolver
    ) {
        this.valueResolver = valueResolver;
    }

    public LoginTestData findByTestCaseId(
            String testCaseId
    ) {

        if (testCaseId == null || testCaseId.isBlank()) {
            throw new IllegalArgumentException(
                    "Test case ID must not be blank."
            );
        }

        try (
                InputStream inputStream =
                        getClass()
                                .getClassLoader()
                                .getResourceAsStream(
                                        TEST_DATA_FILE
                                )
        ) {

            if (inputStream == null) {
                throw new IllegalStateException(
                        "Test data file not found: "
                                + TEST_DATA_FILE
                );
            }

            try (
                    Reader reader =
                            new InputStreamReader(
                                    inputStream,
                                    StandardCharsets.UTF_8
                            )
            ) {

                Iterable<CSVRecord> records =
                        CSVFormat.DEFAULT
                                .builder()
                                .setHeader()
                                .setSkipHeaderRecord(true)
                                .setIgnoreSurroundingSpaces(true)
                                .get()
                                .parse(reader);

                for (CSVRecord record : records) {

                    String recordTestCaseId =
                            getValue(
                                    record,
                                    "testCaseId"
                            );

                    if (testCaseId.equals(
                            recordTestCaseId
                    )) {

                        return mapRecord(
                                record
                        );
                    }
                }
            }

        } catch (IOException exception) {

            throw new IllegalStateException(
                    "Cannot read test data file: "
                            + TEST_DATA_FILE,
                    exception
            );
        }

        throw new IllegalArgumentException(
                "Login test data not found for test case: "
                        + testCaseId
        );
    }

    private LoginTestData mapRecord(
            CSVRecord record
    ) {

        return new LoginTestData(
                getValue(
                        record,
                        "testCaseId"
                ),
                valueResolver.resolve(
                        getValue(
                                record,
                                "username"
                        )
                ),
                valueResolver.resolve(
                        getValue(
                                record,
                                "password"
                        )
                ),
                getValue(
                        record,
                        "expectedResult"
                ),
                getValue(
                        record,
                        "expectedMessage"
                )
        );
    }

    private String getValue(
            CSVRecord record,
            String columnName
    ) {

        String value =
                record.get(columnName);

        return value == null
                ? ""
                : value.trim();
    }
}