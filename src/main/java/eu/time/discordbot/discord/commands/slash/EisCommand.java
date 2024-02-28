package eu.time.discordbot.discord.commands.slash;

import eu.time.discordbot.discord.command.SlashCommand;
import eu.time.discordbot.eis.EisHandler;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.List;

public class EisCommand extends SlashCommand {
    public EisCommand() {
        super("eis", "Returns the currently free eis.de products. Use \"/eis true\" to list them inside the current channel");
        addOption(OptionType.BOOLEAN, "show", "Use \"/eis true\" to list them inside the current channel");
    }

    @Override
    public void exec(SlashCommandInteractionEvent event, List<String> args) {
        List<MessageEmbed> freeDiscordEmbeds = EisHandler.createFreeDiscordEmbeds();
        OptionMapping showOption = event.getOption("show");
        boolean show = showOption != null && showOption.getAsBoolean();

        event.replyEmbeds(freeDiscordEmbeds).setEphemeral(!show).queue();
    }
}
