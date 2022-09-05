package eu.time.discordbot;

import eu.time.discordbot.discord.DiscordBot;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/api")
public class Entry extends Application {
    public Entry() {
        super();
        DiscordBot.getInstance();
    }
}
