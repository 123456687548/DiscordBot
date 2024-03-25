package eu.time.discordbot.crypto;

import eu.time.discordbot.discord.DiscordBot;
import eu.time.discordbot.http.Requester;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Timer;
import java.util.TimerTask;

import static eu.time.discordbot.discord.DiscordBot.EINGANGSHALLEN_CHANNEL_ID;

public class BitcointHalvin {
    private static final String LAST_BLOCK_URL = "https://www.satochi.co//latest-block";
    private final Requester requester = new Requester();
    private TextChannel channel;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG);
    private final double BLOCK_TIME = 9.4;
    private long lastReportedBlock = Long.MAX_VALUE;

    private long getTilHalving() {
        long lastBlock = getLastBlock();

        return 840000 - lastBlock;
    }

    public MessageEmbed getUntilHalvingMessage() {
        long tilHalving = getTilHalving();

        UntilHalving untilHalving = calcUntilHalving(tilHalving);
        return createEmbedMsg(untilHalving);
    }

    private void doTimer() {
        long tilHalving = getTilHalving();
        if (tilHalving == lastReportedBlock) {
            return;
        }

        if (tilHalving > 100 && tilHalving % 100 != 0) {
            return;
        }

        UntilHalving untilHalving = calcUntilHalving(tilHalving);
        MessageEmbed embed = createEmbedMsg(untilHalving);
        channel.sendMessageEmbeds(embed).queue();

        lastReportedBlock = tilHalving;
    }

    private UntilHalving calcUntilHalving(long tilHalving) {
        double minutesTilHalving = tilHalving * BLOCK_TIME;
        double daysTilHalving = minutesTilHalving / (24 * 60);
        double daysRemainder = daysTilHalving - Math.floor(daysTilHalving);
        double minutes = daysRemainder - Math.floor(daysRemainder);
        double secondsTilHalving = Math.max(Math.round((minutesTilHalving - Math.floor(minutesTilHalving)) * 60), 0);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime halvingDate = now.plusMinutes((int) minutesTilHalving);

        int daysCalc = (int) Math.floor(daysTilHalving);
        int hoursCalc = (int) Math.floor(daysRemainder * 24);
        int minutesCalc = (int) (minutes * 60);
        int secondsCalc = (int) secondsTilHalving;

        return new UntilHalving(tilHalving, daysCalc, hoursCalc, minutesCalc, secondsCalc, halvingDate);
    }

    private MessageEmbed createEmbedMsg(UntilHalving untilHalving) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Bitcoin Halving Timer");
        embed.setUrl("https://bitbo.io/de/halving/");
        embed.addField("Halving in:", String.format("%d Days %d Hours %d Minutes %d Seconds", untilHalving.getDaysCalc(), untilHalving.getHoursCalc(), untilHalving.getMinutesCalc(), untilHalving.getSecondsCalc()), false);
        embed.addField("Halving date:", DateTimeFormatter.ISO_LOCAL_DATE.format(untilHalving.getHalvingDate()), false);
        embed.addField("Blocks remaining:", String.valueOf(untilHalving.getTilHalving()), false);
        return embed.build();
    }

    private long getLastBlock() {
        String szlastBlock = requester.get(LAST_BLOCK_URL);
        return Long.parseLong(szlastBlock);
    }

    public void startTimer(DiscordBot discordBot) {
        this.channel = discordBot.getJda().getTextChannelById(EINGANGSHALLEN_CHANNEL_ID);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                doTimer();
            }
        }, 0L, 1000L * 30);
    }
}
