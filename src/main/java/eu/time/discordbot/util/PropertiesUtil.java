package eu.time.discordbot.util;

import java.io.IOException;
import java.util.Properties;

public class PropertiesUtil {
    public static String getProperty(String key)  {
        Properties properties = new Properties();
        try {
            properties.load(PropertiesUtil.class.getClassLoader().getResourceAsStream("secrets.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties.getProperty(key);
    }
}
