package eu.time.discordbot.discord.listeners;

import eu.time.discordbot.discord.util.MessageHandler;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public abstract class DiscordListener extends ListenerAdapter {
    protected final MessageHandler messageHandler = MessageHandler.getInstance();
}
