package eu.time.discordbot.discord.listeners.message;

import eu.time.discordbot.discord.features.AmazonFeature;
import eu.time.discordbot.discord.features.AntiTikTok;
import eu.time.discordbot.discord.features.SpotifyFilterFeature;
import eu.time.discordbot.discord.listeners.DiscordListener;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ChatListener extends DiscordListener {
    public ChatListener() {
        super(List.of(
            new AntiTikTok(),
            new AmazonFeature(),
            new SpotifyFilterFeature()
        ));
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        executeFeatures(event);
    }
}
