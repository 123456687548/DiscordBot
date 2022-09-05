package eu.time.discordbot.util;

import java.io.IOException;
import java.util.Properties;

public class PropertiesUtil {
    public static String getProperty(String key) throws IOException {
        Properties properties = new Properties();
        properties.load(PropertiesUtil.class.getClassLoader().getResourceAsStream("secrets.properties"));
        return properties.getProperty(key);
    }
}
