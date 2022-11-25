package eu.time.discordbot.discord.util;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class MessageHandler {
    private static final MessageHandler INSTANCE = new MessageHandler();

    private JDA jda;
    private boolean initalized = false;

    private MessageHandler() {
    }

    public void deleteMessage(MessageReceivedEvent event) {
        deleteMessage(event.getMessage());
    }

    public void deleteMessages(TextChannel channel, List<Message> messages) {
        channel.purgeMessages(messages);
    }

    public void deleteMessage(Message message) {
        message.delete().queue();
    }

    public void sendMessage(MessageReceivedEvent event, CharSequence msg) {
        sendMessage(event.getChannel().asTextChannel(), msg);
    }

    public void sendMessage(TextChannel channel, CharSequence msg) {
        if(channel == null) return;
        channel.sendMessage(msg).queue();
    }

    public static MessageHandler create(JDA jda) {
        if (INSTANCE.initalized) return INSTANCE;

        INSTANCE.jda = jda;
        INSTANCE.initalized = true;

        return INSTANCE;
    }

    public static MessageHandler getInstance() {
        return INSTANCE;
    }
}
