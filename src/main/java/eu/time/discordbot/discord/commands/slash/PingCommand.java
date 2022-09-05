package eu.time.discordbot.discord.commands.slash;

import eu.time.discordbot.discord.command.SlashCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.List;

public class PingCommand extends SlashCommand {
    public PingCommand() {
        super("ping", "Calculate ping of the bot");
    }

    @Override
    public void exec(SlashCommandInteractionEvent event, List<String> args) {
        long time = System.currentTimeMillis();
        event.reply("Pong!").setEphemeral(true) // reply or acknowledge
                .flatMap(v ->
                        event.getHook().editOriginalFormat("Pong: %d ms", System.currentTimeMillis() - time) // then edit original
                ).queue(); // Queue both reply and edit
    }
}
