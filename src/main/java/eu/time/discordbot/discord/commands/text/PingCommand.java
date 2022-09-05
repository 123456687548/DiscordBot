package eu.time.discordbot.discord.commands.text;

import eu.time.discordbot.discord.command.Permission;
import eu.time.discordbot.discord.command.TextCommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class PingCommand extends TextCommand {
    public PingCommand() {
        super("ping", Permission.NORMAL);
    }

    @Override
    protected void execIntern(MessageReceivedEvent event, List<String> args) {
        messageHandler.sendMessage(event, "pong");
    }
}
