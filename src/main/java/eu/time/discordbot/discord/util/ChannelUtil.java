package eu.time.discordbot.discord.util;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;
import java.util.function.Consumer;

public class ChannelUtil {

    public static void getOrCreateChannel(Guild guild, String channelName, Consumer<TextChannel> response) {
        List<TextChannel> channels = getChannels(guild, channelName);

        if (channels.isEmpty()) {
            guild.createTextChannel(channelName).queue(response);
            return;
        }

        response.accept(channels.get(0));
    }

    public static List<TextChannel> getChannels(Guild guild, String channelName) {
        return guild.getTextChannelsByName(channelName, true);
    }
}
