package eu.time.discordbot.discord.commands.text;

import eu.time.discordbot.crypto.BitcointHalving;
import eu.time.discordbot.discord.command.Permission;
import eu.time.discordbot.discord.command.TextCommand;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class BitcoinHalvingCommand extends TextCommand {
    public BitcoinHalvingCommand() {
        super("bitcoin", Permission.NORMAL);
    }

    @Override
    protected void execIntern(MessageReceivedEvent event, List<String> args) {
        MessageEmbed untilHalvingMessage = new BitcointHalving().getUntilHalvingMessage();

        if (untilHalvingMessage == null) {
            event.getMessage().reply("ERROR: Could not get last Block").queue();
            return;
        }

        messageHandler.sendEmbed(event, untilHalvingMessage);
        event.getMessage().delete().queue();
    }
}
