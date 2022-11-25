package eu.time.discordbot.discord.util;

import java.net.MalformedURLException;
import java.net.URL;

public class URLChecker {
    public static boolean isURL(String string) {
        try {
            new URL(string);
        } catch (MalformedURLException e) {
            return false;
        }
        return true;
    }
}
