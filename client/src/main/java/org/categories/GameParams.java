package org.categories;

import java.util.Properties;

public class GameParams {
    public static final String CONFIG_FILE_NAME = "../config.properties";
    private final String host;
    private final int port;
    private final int roundTime;

    public GameParams() {
        Config config = new Config();
        Properties properties = config.readConfigFile(CONFIG_FILE_NAME);
        host = properties.getProperty("host", "localhost");
        port = Integer.parseInt(properties.getProperty("port", "2100"));
        roundTime = Integer.parseInt(properties.getProperty("roundTime", "5"));
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public int getRoundTime() {
        return roundTime;
    }
}
