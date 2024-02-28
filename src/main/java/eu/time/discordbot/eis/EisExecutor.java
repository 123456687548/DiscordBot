package eu.time.discordbot.eis;

import eis.EisQuery;
import eu.time.discordbot.discord.DiscordBot;
import eu.time.discordbot.discord.util.ChannelUtil;
import eu.time.discordbot.executor.TimedExecutor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.List;

public class EisExecutor extends TimedExecutor {
    private final DiscordBot discordBot;

    private final EisQuery eisQuery = EisHandler.EIS;

    public EisExecutor(DiscordBot discordBot) {
        this.discordBot = discordBot;
    }

    @Override
    protected void runTask() {
        List<MessageEmbed> freeDiscordEmbeds = EisHandler.createFreeDiscordEmbeds();

        List<Guild> guilds = discordBot.getJda().getGuilds();

        for (Guild guild : guilds) {
            List<TextChannel> channels = ChannelUtil.getChannels(guild, "admin-chat");
            for (TextChannel channel : channels) {
                channel.sendMessageEmbeds(freeDiscordEmbeds).queue();
            }
        }
    }
}
