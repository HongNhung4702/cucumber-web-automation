
package com.hongnhung.cucumberweb.testdata.notifications;

import com.hongnhung.cucumberweb.models.notifications.NotificationRecord;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class NotificationBaselineReader {

    private static final String RESOURCE =
            "baselines/notifications/TC_10_expected.csv";

    private static final List<String> HEADERS =
            List.of("title", "sender", "date");

    public List<NotificationRecord> readRecords() {

        InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream(RESOURCE);

        if (input == null) {
            throw new IllegalStateException(
                    "Không tìm thấy Baseline: " + RESOURCE
            );
        }

        try (
                InputStream stream = input;
                InputStreamReader reader =
                        new InputStreamReader(
                                stream,
                                StandardCharsets.UTF_8
                        );
                CSVParser parser =
                        CSVFormat.DEFAULT.builder()
                                .setHeader()
                                .setSkipHeaderRecord(true)
                                .get()
                                .parse(reader)
        ) {

            List<String> headers =
                    new ArrayList<>(parser.getHeaderNames());

            if (!headers.isEmpty()) {
                headers.set(
                        0,
                        headers.get(0).replace("\uFEFF", "")
                );
            }

            if (!HEADERS.equals(headers)) {
                throw new IllegalStateException(
                        "TC_10: Header CSV không đúng. "
                                + "Expected=" + HEADERS
                                + ", Actual=" + headers
                );
            }

            List<NotificationRecord> records =
                    new ArrayList<>();

            for (CSVRecord row : parser) {

                if (!row.isConsistent() || row.size() != 3) {
                    throw new IllegalStateException(
                            "TC_10: Sai số cột tại dòng "
                                    + row.getRecordNumber()
                    );
                }

                records.add(new NotificationRecord(
                        row.get(0).strip(),
                        row.get(1).strip(),
                        row.get(2).strip()
                ));
            }

            if (records.isEmpty()) {
                throw new IllegalStateException(
                        "TC_10: Baseline không có dữ liệu."
                );
            }

            return List.copyOf(records);

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "Không đọc được Baseline TC_10: "
                            + RESOURCE,
                    exception
            );
        }
    }
}
