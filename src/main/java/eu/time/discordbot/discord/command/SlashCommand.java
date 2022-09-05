package eu.time.discordbot.discord.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public abstract class SlashCommand extends Command<SlashCommandInteractionEvent> {
    public SlashCommand(String name, String description) {
        super(name, description);
    }
}
