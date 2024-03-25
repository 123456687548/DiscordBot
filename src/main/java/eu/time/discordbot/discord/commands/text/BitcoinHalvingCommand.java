package eu.time.discordbot.discord.commands.text;

import eu.time.discordbot.crypto.BitcointHalvin;
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
        MessageEmbed untilHalvingMessage = new BitcointHalvin().getUntilHalvingMessage();
        messageHandler.sendEmbed(event, untilHalvingMessage);
        event.getMessage().delete().queue();
    }
}
