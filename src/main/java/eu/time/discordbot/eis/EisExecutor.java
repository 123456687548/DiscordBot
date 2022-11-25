package eu.time.discordbot.eis;

import data.Product;
import eis.EisQuery;
import eu.time.discordbot.discord.DiscordBot;
import eu.time.discordbot.discord.util.ChannelUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EisExecutor {
    private final DiscordBot discordBot;

    private final EisQuery eisQuery = new EisQuery();
    ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    volatile boolean isStopIssued;

    public EisExecutor(DiscordBot discordBot) {
        this.discordBot = discordBot;
    }

    public void startExecutionAt(int targetHour, int targetMin, int targetSec) {
        Runnable taskWrapper = () -> {
            List<Product> products = eisQuery.getFreeProducts();

            List<Guild> guilds = discordBot.getJda().getGuilds();

            for (Guild guild : guilds) {
                List<TextChannel> channels = ChannelUtil.getChannels(guild, "admin-chat");
                for (TextChannel channel : channels) {
                    for (Product product : products) {
                        channel.sendMessage("----------------------\n" + product.toString()).queue();
                    }
                }
            }

            startExecutionAt(targetHour, targetMin, targetSec);
        };
        long delay = computeNextDelay(targetHour, targetMin, targetSec);
        executorService.schedule(taskWrapper, delay, TimeUnit.SECONDS);
    }

    private long computeNextDelay(int targetHour, int targetMin, int targetSec) {
        LocalDateTime localNow = LocalDateTime.now();
        ZoneId currentZone = ZoneId.systemDefault();
        ZonedDateTime zonedNow = ZonedDateTime.of(localNow, currentZone);
        ZonedDateTime zonedNextTarget = zonedNow.withHour(targetHour).withMinute(targetMin).withSecond(targetSec);
        if (zonedNow.compareTo(zonedNextTarget) > 0)
            zonedNextTarget = zonedNextTarget.plusDays(1);

        Duration duration = Duration.between(zonedNow, zonedNextTarget);
        return duration.getSeconds();
    }

    public void stop() {
        executorService.shutdown();
        try {
            executorService.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException ex) {
        }
    }
}
