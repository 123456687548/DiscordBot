package eu.time.discordbot.crypto;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import eu.time.discordbot.discord.DiscordBot;
import eu.time.discordbot.http.Requester;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import static eu.time.discordbot.discord.DiscordBot.CRYPTO_CHANNEL_ID;

public class CryptoQuery {
    public static final String NAN = "NaN";
    private static final String GET_TICKERS_URL = "https://api.crypto.com/exchange/v1/public/get-tickers?instrument_name=";
    private final Gson gson = new Gson();
    private final Requester requester = new Requester();

    private final TextChannel channel;

    private Ticker currentADA_EUR;
    private Ticker startADA_EUR;

    public CryptoQuery(DiscordBot discordBot) {
        channel = discordBot.getJda().getTextChannelById(CRYPTO_CHANNEL_ID);
        getCurrentADA_EUR();

        startADA_EUR = currentADA_EUR;
    }

    public void startNewDay() {
        getCurrentADA_EUR();
        if (startADA_EUR != null) {
            MessageEmbed messageEmbed = createDiscordMessage();
            channel.sendMessageEmbeds(messageEmbed).queue();
        }
        startADA_EUR = currentADA_EUR;
    }

    private MessageEmbed createDiscordMessage() {
        EmbedBuilder embed = new EmbedBuilder();

        Boolean positiv = isPositivDay();

        BigDecimal priceChange = getPriceChange();
        String szPriceChange = priceChange == null ? NAN : priceChange.toString();
        BigDecimal openingPriceBigDec = startADA_EUR.getLatestTradeBigD();
        String openingPrice = openingPriceBigDec == null ? NAN : openingPriceBigDec.toString();
        String closingPrice = currentADA_EUR.getLatestTrade() == null ? NAN : currentADA_EUR.getLatestTrade();
        String lowest24hTrade = currentADA_EUR.getLowest24hTrade() == null ? NAN : currentADA_EUR.getLowest24hTrade();

        String szPriceChangePercentage;
        if (openingPriceBigDec == null || priceChange == null) {
            szPriceChangePercentage = NAN;
        } else {
            szPriceChangePercentage = priceChange.divide(openingPriceBigDec.abs(), RoundingMode.HALF_DOWN)
                    .multiply(new BigDecimal(100))
                    .setScale(2, RoundingMode.HALF_DOWN).toString();
        }

        LocalDateTime startDateTime = startADA_EUR.getDateTime();
        LocalDateTime endDateTime = currentADA_EUR.getDateTime();

        embed.setTitle(currentADA_EUR.getInstrumentName().replace("_", " - "));
        embed.setDescription(String.format("%tF %tT - %tF %tT", startDateTime, startDateTime, endDateTime, endDateTime));

        embed.addField("Change (€)", szPriceChange + " €", true);
        embed.addField("Opening price", openingPrice + " €", true);
        embed.addField("High", currentADA_EUR.getHighest24hTrade() + " €", true);

        embed.addField("Change (%)", szPriceChangePercentage + " %", true);
        embed.addField("Closing price", closingPrice + " €", true);
        embed.addField("Low", lowest24hTrade + " €", true);

        if (positiv == null) {
            embed.setColor(Color.GRAY);
        } else if (positiv) {
            embed.setColor(Color.GREEN);
        } else {
            embed.setColor(Color.RED);
        }

        return embed.build();
    }

    private BigDecimal getPriceChange() {
        BigDecimal priceChange24h = currentADA_EUR.getPriceChange24hBigD();

        if (priceChange24h == null) {
            BigDecimal startLatestTrade = startADA_EUR.getLatestTradeBigD();
            BigDecimal currentLatestTrade = currentADA_EUR.getLatestTradeBigD();

            if (startLatestTrade == null || currentLatestTrade == null) {
                return null;
            }

            return currentLatestTrade.subtract(startLatestTrade);
        }
        return priceChange24h;
    }


    @Nullable
    private Boolean isPositivDay() {
        BigDecimal priceChange = getPriceChange();

        if (priceChange == null) {
            return null;
        }
        return priceChange.compareTo(BigDecimal.ZERO) > 0;
    }

    public void getCurrentADA_EUR() {
        Ticker ticker = getTicker(GET_TICKERS_URL + "ADA_USD");

        if (ticker == null) {
            return;
        }

        currentADA_EUR = ticker.convertCurrency("EUR");
    }

    @Nullable
    public Ticker getTicker(String url) {
        List<Ticker> tickers = getTickers(url);

        if (tickers == null) {
            return null;
        }

        return tickers.get(0);
    }

    @Nullable
    public List<Ticker> getTickers(String url) {
        String response = requester.get(url);
        CryptoComRequest<Ticker> result = gson.fromJson(response, TypeToken.getParameterized(CryptoComRequest.class, Ticker.class).getType());

        if (result.getResult() == null) {
            return null;
        }

        List<Ticker> data = result.getResult().getData();

        if (data.isEmpty()) {
            return null;
        }

        return data;
    }
}
