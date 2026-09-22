package com.hongnhung.cucumberweb.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigManager {

    private static final String CONFIG_FILE = "config.properties";
    private static final Properties PROPERTIES = loadProperties();

    private ConfigManager() {
    }

    public static String get(String key) {
        String systemValue = System.getProperty(key);

        if (hasText(systemValue)) {
            return systemValue.trim();
        }

        String environmentValue =
                System.getenv(toEnvironmentVariable(key));

        if (hasText(environmentValue)) {
            return environmentValue.trim();
        }

        String propertyValue = PROPERTIES.getProperty(key);

        if (!hasText(propertyValue)) {
            throw new IllegalStateException(
                    "Missing configuration property: " + key
            );
        }

        return propertyValue.trim();
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }

    public static int getInt(String key) {
        try {
            return Integer.parseInt(get(key));
        } catch (NumberFormatException exception) {
            throw new IllegalStateException(
                    "Configuration property '" + key
                            + "' must be an integer.",
                    exception
            );
        }
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();

        try (InputStream inputStream =
                     ConfigManager.class
                             .getClassLoader()
                             .getResourceAsStream(CONFIG_FILE)) {

            if (inputStream == null) {
                throw new IllegalStateException(
                        "Configuration file not found: "
                                + CONFIG_FILE
                );
            }

            properties.load(inputStream);
            return properties;

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Cannot load configuration file: "
                            + CONFIG_FILE,
                    exception
            );
        }
    }

    private static String toEnvironmentVariable(String key) {
        return key
                .toUpperCase()
                .replace('.', '_');
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}