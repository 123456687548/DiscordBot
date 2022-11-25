package eu.time.discordbot.discord.features;

import net.dv8tion.jda.api.events.Event;

public interface Feature<T extends Event> {
    void execute(T event);
}
