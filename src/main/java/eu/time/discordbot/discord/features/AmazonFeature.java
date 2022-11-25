package eu.time.discordbot.discord.features;

import eu.time.discordbot.discord.util.URLChecker;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class AmazonFeature extends AbstractFeature<MessageReceivedEvent> {
    @Override
    public void execute(MessageReceivedEvent event) {
        String contentRaw = event.getMessage().getContentRaw();

        if (URLChecker.isURL(contentRaw) && contentRaw.contains("amazon")) {
            messageHandler.sendMessage(event, String.format("From: %s\n%s", event.getAuthor().getAsMention(), shortenLink(contentRaw)));

            messageHandler.deleteMessage(event);
        }
    }

    private String shortenLink(String link) {
        String domain = getDomain(link);

        String result;

        if (link.contains("/gp/")) {
            result = String.format("https://www.amazon.%s/gp/video/detail/%s", domain, getVideoID(link));
        } else {
            result = String.format("https://www.amazon.%s/dp/%s", domain, getProductID(link));
        }

        return result;
    }

    private String getDomain(String link) {
        String domain = link.split("\\.")[2];
        return domain.substring(0, domain.indexOf('/'));
    }

    private String getVideoID(String link) {
        String sdf = "/gp/video/detail/";
        int idStart = link.indexOf(sdf) + sdf.length();
        String videoId = link.substring(idStart);
        int idEnd = videoId.indexOf('/') == -1 ? videoId.indexOf('?') : videoId.indexOf('/');
        return videoId.substring(0, idEnd);
    }

    private String getProductID(String link) {
        int idStart = !link.contains("dp/") ? link.indexOf("product/") + 8 : link.indexOf("dp/") + 3;
        String productId = link.substring(idStart);
        int idEnd = productId.indexOf('/') == -1 ? productId.indexOf('?') : productId.indexOf('/');
        return productId.substring(0, idEnd);
    }
}
