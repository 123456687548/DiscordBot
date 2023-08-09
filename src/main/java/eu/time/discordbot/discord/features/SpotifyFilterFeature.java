package eu.time.discordbot.discord.features;

import eu.time.discordbot.discord.util.URLChecker;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SpotifyFilterFeature extends AbstractFeature<MessageReceivedEvent> {

    @Override
    public void execute(MessageReceivedEvent event) {
        String contentRaw = event.getMessage().getContentRaw();

        if (URLChecker.isURL(contentRaw) && contentRaw.contains("spotify")) {
            messageHandler.sendMessage(event, String.format("From: %s\n%s", event.getAuthor().getAsMention(), removeShit(contentRaw)));

            messageHandler.deleteMessage(event);
        }
    }

    private String removeShit(String msg) {
        String trackString = "/track/";
        String trackId = msg.substring(msg.indexOf(trackString) + trackString.length());

        if (trackId.contains("?")) {
            trackId = trackId.substring(0, trackId.indexOf('?'));
        }

        return String.format("https://open.spotify.com/track/%s", trackId);
    }
}
