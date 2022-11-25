package eu.time.discordbot.discord.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;

public abstract class SlashCommand extends Command<SlashCommandInteractionEvent> {
    public SlashCommand(String name, String description) {
        super(name, description);
    }
}
