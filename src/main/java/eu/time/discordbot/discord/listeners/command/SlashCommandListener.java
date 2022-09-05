package eu.time.discordbot.discord.listeners.command;

import eu.time.discordbot.discord.command.Command;
import eu.time.discordbot.discord.commands.slash.PingCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SlashCommandListener extends CommandListener<SlashCommandInteractionEvent> {
    public SlashCommandListener() {
        super(Map.ofEntries(
                new PingCommand().getAsPair()
        ));
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String commandName = event.getName();

        Command<SlashCommandInteractionEvent> command = commands.get(commandName);

        if (command == null) return;

        command.exec(event, getParameters(event));
    }

    @Override
    protected List<String> getParameters(SlashCommandInteractionEvent event) {
        return Arrays.stream(event.getCommandPath().split("/")).skip(1).toList();
    }
}
