package utils;

import java.util.*;
import java.io.*;

public class ConfigManager {

    private Map<String, String> properties = new HashMap<String, String>();
    private Properties mainProperties = new Properties();

    public ConfigManager() {
        try {
            InputStream is = ConfigManager.class.getResourceAsStream("config.properties");
            this.mainProperties.load(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        properties.put("BASE_IP", mainProperties.getProperty("BASE_IP"));
        properties.put("TCP_PORT", mainProperties.getProperty("TCP_PORT"));
        properties.put("BROADCAST_PORT", mainProperties.getProperty("BROADCAST_PORT"));
        properties.put("SCREEN_WINDOW_RATIO", mainProperties.getProperty("SCREEN_WINDOW_RATIO"));
        properties.put("STATIC_FOLDER", mainProperties.getProperty("STATIC_FOLDER"));
        properties.put("DATABASE_HOST", mainProperties.getProperty("DATABASE_HOST"));
        properties.put("DATABASE_USER", mainProperties.getProperty("DATABASE_USER"));
        properties.put("DATABASE_PASSWORD", mainProperties.getProperty("DATABASE_PASSWORD"));
        properties.put("USE_DATABASE", mainProperties.getProperty("USE_DATABASE"));
        properties.put("SOCKETS_TIMEOUT", mainProperties.getProperty("SOCKETS_TIMEOUT"));
        properties.put("CHECKLIFE_TIMER", mainProperties.getProperty("CHECKLIFE_TIMER"));
        properties.put("CHECKLIFE_MAX", mainProperties.getProperty("CHECKLIFE_MAX"));
        properties.put("ALIVE_TIME", mainProperties.getProperty("ALIVE_TIME"));
    }

    public String get(String property) {
        return properties.get(property);
    }

}
