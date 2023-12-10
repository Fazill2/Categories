package org.categories;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
    public Properties readConfigFile(String fileName) {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream(fileName)) {
            properties.load(input);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return properties;
    }
}
