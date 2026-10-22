package com.hongnhung.cucumberweb.testdata;

import com.hongnhung.cucumberweb.config.ConfigManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TestDataValueResolver {

    private static final Pattern CONFIG_PLACEHOLDER =
            Pattern.compile("^\\$\\{([^{}]+)}$");

    public String resolve(String value) {

        if (value == null || value.isBlank()) {
            return "";
        }

        String trimmedValue =
                value.trim();

        Matcher matcher =
                CONFIG_PLACEHOLDER.matcher(
                        trimmedValue
                );

        if (!matcher.matches()) {
            return trimmedValue;
        }

        String configKey =
                matcher.group(1).trim();

        if (configKey.isBlank()) {
            throw new IllegalArgumentException(
                    "Test data placeholder contains an empty configuration key."
            );
        }

        return ConfigManager.get(
                configKey
        );
    }
}