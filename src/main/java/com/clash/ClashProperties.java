package com.clash;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public enum ClashProperties {
    INSTANCE;

    private static final String PATH_PROP = "prop.path";
    private static final String DEFAULT_PATH = "clash.properties";

    private Properties properties = initProps();

    private Properties initProps() {
        try {
            Properties properties = new Properties();
            InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(getFilePath());
            properties.load(stream);
            return properties;
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private String getFilePath() {
        String path = System.getProperty(PATH_PROP);
        return null == path || path.isEmpty() ? DEFAULT_PATH : path;
    }

    public int getIntVal(String key) {
        return Integer.parseInt(properties.getProperty(key));
    }
}
