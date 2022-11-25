package eu.time.discordbot.discord.listeners.message;

import eu.time.discordbot.discord.features.AmazonFeature;
import eu.time.discordbot.discord.features.AntiTikTok;
import eu.time.discordbot.discord.listeners.DiscordListener;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nonnull;
import java.util.List;

public class ChatListener extends DiscordListener {
    public ChatListener() {
        super(List.of(
                new AntiTikTok(),
                new AmazonFeature()
        ));
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        executeFeatures(event);
    }
}
