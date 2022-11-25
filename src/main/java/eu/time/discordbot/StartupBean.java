package eu.time.discordbot;

import eu.time.discordbot.discord.DiscordBot;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Startup
@Singleton
public class StartupBean {
    @PostConstruct
    public void startup() {
        DiscordBot.getInstance();
    }
}
