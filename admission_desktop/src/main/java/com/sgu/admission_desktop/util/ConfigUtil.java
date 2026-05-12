package com.sgu.admission_desktop.util;

import java.io.InputStream;
import java.util.Properties;

public class ConfigUtil {

    private static final Properties properties = new Properties();

    static {
        try (
                InputStream input =
                        ConfigUtil.class
                                .getClassLoader()
                                .getResourceAsStream("app.properties")
        ) {

            if (input == null) {
                throw new RuntimeException("Không tìm thấy file app.properties");
            }

            properties.load(input);

        } catch (Exception e) {
            throw new RuntimeException("Lỗi load config", e);
        }
    }

    private ConfigUtil() {
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }
}