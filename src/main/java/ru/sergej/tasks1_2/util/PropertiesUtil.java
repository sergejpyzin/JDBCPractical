package ru.sergej.tasks1_2.util;

import java.io.IOException;
import java.util.Properties;

public class PropertiesUtil {

    private static final Properties PROPERTIES = new Properties();

    static {
        loadProperties();
    }

    private static void loadProperties() {

        try (var InputStream = PropertiesUtil.class
                .getClassLoader()
                .getResourceAsStream("database.properties")) {
            PROPERTIES.load(InputStream);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static String get(String key) {
        return PROPERTIES.getProperty(key);
    }

    private PropertiesUtil() {}

}
