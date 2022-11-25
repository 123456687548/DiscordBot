package eu.time.discordbot.discord.features;

import eu.time.discordbot.discord.util.MessageHandler;
import net.dv8tion.jda.api.events.Event;

public abstract class AbstractFeature<T extends Event> implements Feature<T> {
    protected final MessageHandler messageHandler = MessageHandler.getInstance();
}
