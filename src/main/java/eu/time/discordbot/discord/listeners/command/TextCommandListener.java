package eu.time.discordbot.discord.listeners.command;

import eu.time.discordbot.discord.command.Command;
import eu.time.discordbot.discord.commands.text.BitcoinHalvingCommand;
import eu.time.discordbot.discord.commands.text.ClearCommand;
import eu.time.discordbot.discord.commands.text.PingCommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TextCommandListener extends CommandListener<MessageReceivedEvent> {
    private final String PREFIX = "!";

    public TextCommandListener() {
        super(Map.ofEntries(
                new PingCommand().getAsPair(),
                new ClearCommand().getAsPair(),
                new BitcoinHalvingCommand().getAsPair()
        ));
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String msg = event.getMessage().getContentStripped().trim().toLowerCase();
        if(!msg.startsWith(PREFIX)) return;

        String msgWithoutPrefix = msg.substring(1);

        Command<MessageReceivedEvent> command = getCommand(msgWithoutPrefix);

        if(command == null){
            messageHandler.sendMessage(event, "Command does not exist");
            return;
        }

        command.exec(event, getParameters(event));
    }

    @Override
    protected List<String> getParameters(MessageReceivedEvent event) {
        return Arrays.stream(event.getMessage().getContentStripped().split(" ")).skip(1).toList();
    }

    private Command<MessageReceivedEvent> getCommand(String msg) {
        String commandName = msg.split(" ")[0];
        return commands.get(commandName);
    }
}
