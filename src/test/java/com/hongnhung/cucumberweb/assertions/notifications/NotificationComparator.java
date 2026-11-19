
package com.hongnhung.cucumberweb.assertions.notifications;

import com.hongnhung.cucumberweb.models.notifications.NotificationRecord;

import java.text.Normalizer;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class NotificationComparator {

    public record Result(
            int expectedCount,
            int actualCount,
            Map<NotificationRecord, Integer> missing,
            Map<NotificationRecord, Integer> extra
    ) {
        public boolean passed() {
            return missing.isEmpty();
        }

        public String details() {
            return "Expected=" + expectedCount
                    + ", Actual=" + actualCount
                    + ", Missing=" + missing
                    + ", Extra=" + extra;
        }
    }

    public Result compare(
            List<NotificationRecord> expected,
            List<NotificationRecord> actual
    ) {
        Objects.requireNonNull(expected);
        Objects.requireNonNull(actual);

        Map<NotificationRecord, Integer> expectedCounts =
                frequencies(expected);

        Map<NotificationRecord, Integer> actualCounts =
                frequencies(actual);

        return new Result(
                expected.size(),
                actual.size(),
                difference(expectedCounts, actualCounts),
                difference(actualCounts, expectedCounts)
        );
    }

    private Map<NotificationRecord, Integer> frequencies(
            List<NotificationRecord> records
    ) {
        Map<NotificationRecord, Integer> counts =
                new LinkedHashMap<>();

        for (NotificationRecord record : records) {

            NotificationRecord normalized =
                    new NotificationRecord(
                            normalize(record.title()),
                            normalize(record.sender()),
                            normalize(record.date())
                    );

            counts.merge(normalized, 1, Integer::sum);
        }

        return counts;
    }

    private Map<NotificationRecord, Integer> difference(
            Map<NotificationRecord, Integer> left,
            Map<NotificationRecord, Integer> right
    ) {
        Map<NotificationRecord, Integer> result =
                new LinkedHashMap<>();

        for (var entry : left.entrySet()) {

            int remaining =
                    entry.getValue()
                            - right.getOrDefault(
                            entry.getKey(), 0
                    );

            if (remaining > 0) {
                result.put(entry.getKey(), remaining);
            }
        }

        return result;
    }

    private String normalize(String value) {

        Objects.requireNonNull(value);

        return Normalizer.normalize(
                value.replace('\u00A0', ' ')
                        .replace('\u202F', ' ')
                        .strip()
                        .replaceAll("\\s+", " "),
                Normalizer.Form.NFC
        );
    }
}
