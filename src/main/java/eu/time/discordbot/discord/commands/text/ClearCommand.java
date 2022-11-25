package eu.time.discordbot.discord.commands.text;

import eu.time.discordbot.discord.command.Permission;
import eu.time.discordbot.discord.command.TextCommand;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class ClearCommand extends TextCommand {
    public ClearCommand() {
        super("clear", Permission.ADMIN);
    }

    @Override
    protected void execIntern(MessageReceivedEvent event, List<String> args) {
        if (args.isEmpty()) {
            messageHandler.sendMessage(event, "Missing amount to delete!");
            return;
        }

        try {
            int amountToDelete = Integer.parseInt(args.get(0));

            TextChannel channel = event.getChannel().asTextChannel();

//            channel.history
//
//            channel.getHistoryAfter(event.getMessage(), amountToDelete).queue(result -> {
//                result
//            }, Throwable::printStackTrace);

            channel.getHistoryBefore(event.getMessageId(), amountToDelete).queue(result -> {
                messageHandler.deleteMessages(channel, result.getRetrievedHistory());
                messageHandler.deleteMessage(event);
            });
        } catch (NumberFormatException numberFormatException) {
            messageHandler.sendMessage(event, "Argument is not a number!");
        }
    }
}
