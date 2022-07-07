package com.hsbc.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigProperties {

    private static ConfigProperties configProps = null;

    private ConfigProperties() {

    }

    public static ConfigProperties getInstance() {
        if (configProps == null) {
            configProps = new ConfigProperties();
            configProps.loadPropertiesFile();
        }
        return configProps;
    }

    private int tokenExpireSeconds;

    private String hostname;

    private int port;

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    public int getTokenExpireSeconds() {
        return tokenExpireSeconds;
    }

    private void loadPropertiesFile() {
        Properties prop = new Properties();

        try (InputStream is = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            prop.load(is);
        } catch (IOException ex) {
            System.out.println("Error loading config file");
        }
        tokenExpireSeconds = Integer.parseInt(prop.getProperty("token.expire.time.seconds"));
        hostname = prop.getProperty("host.name");
        port = Integer.parseInt(prop.getProperty("port"));
    }
}
