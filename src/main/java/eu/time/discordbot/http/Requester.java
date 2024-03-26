package eu.time.discordbot.http;

import eu.time.discordbot.discord.DiscordBot;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static eu.time.discordbot.discord.DiscordBot.ADMIN_CHANNEL_ID;

public class Requester {
    public static final Logger LOG = LoggerFactory.getLogger(Requester.class);
    private final HttpClient client;
    private final TextChannel logChannel;

    public Requester() {
        this.logChannel = DiscordBot.INSTANCE.getJda().getTextChannelById(ADMIN_CHANNEL_ID);
        client = HttpClient.newHttpClient();
    }

    public String get(String url) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(5))
                .build();
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .exceptionally(e -> {
                    LOG.warn(e.getMessage());
                    logChannel.sendMessage(String.format("%s%n%s",e.getMessage(), url)).queue();
                    return null;
                })
                .join();
    }
}
