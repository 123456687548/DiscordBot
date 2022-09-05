package eu.time.discordbot.discord.command;

import eu.time.discordbot.discord.util.MessageHandler;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

public abstract class Command<T> {
    protected final MessageHandler messageHandler = MessageHandler.getInstance();
    private final String name;
    private String description;

    public Command(String name) {
        this.name = name;
        this.description = "";
    }

    public Command(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public abstract void exec(T event, List<String> args);

    public Map.Entry<String, Command<T>> getAsPair() {
        return new AbstractMap.SimpleEntry<>(name, this);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
