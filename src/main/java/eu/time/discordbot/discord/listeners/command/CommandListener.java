package eu.time.discordbot.discord.listeners.command;

import eu.time.discordbot.discord.command.Command;
import eu.time.discordbot.discord.listeners.DiscordListener;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class CommandListener<T> extends DiscordListener {
    protected final Map<String, Command<T>> commands;

    public CommandListener(Map<String, Command<T>> commands) {
        this.commands = commands;
    }

    protected abstract List<String> getParameters(T event);

    public Collection<Command<T>> getCommands() {
        return commands.values();
    }
}
