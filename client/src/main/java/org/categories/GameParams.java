package org.categories;

import java.util.Properties;

public class GameParams {
    public static final String CONFIG_FILE_NAME = "./config.properties";
    private final String host;
    private final int port;

    public GameParams() {
        Config config = new Config();
        Properties properties = config.readConfigFile(CONFIG_FILE_NAME);
        host = properties.getProperty("host", "localhost");

        port = Integer.parseInt(properties.getProperty("port", "2100"));
        System.out.println(port);
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

}
