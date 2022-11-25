package eu.time.discordbot.discord.command;

import eu.time.discordbot.discord.util.MessageHandler;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class Command<T> {
    protected final MessageHandler messageHandler = MessageHandler.getInstance();
    private final String name;
    private String description;

    protected final List<OptionData> options = new ArrayList<>();

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

    protected void addOption(@NotNull OptionType type, @NotNull String name, @NotNull String description) {
        addOption(type, name, description, false);
    }

    protected void addOption(@NotNull OptionType type, @NotNull String name, @NotNull String description, boolean isRequired) {
        addOption(type, name, description, isRequired, false);
    }

    protected void addOption(@NotNull OptionType type, @NotNull String name, @NotNull String description, boolean isRequired, boolean isAutoComplete) {
        options.add(new OptionData(type, name, description, isRequired, isAutoComplete));
    }

    public List<OptionData> getOptions() {
        return options;
    }
}
