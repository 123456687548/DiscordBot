package eu.time.discordbot.crypto;

import com.google.gson.annotations.SerializedName;
import eu.time.discordbot.currency.CurrencyExchange;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class Ticker {
    @SerializedName("h")
    private String highest24hTrade;
    @Nullable
    @SerializedName("l")
    private String lowest24hTrade;
    @Nullable
    @SerializedName("a")
    private String latestTrade;
    @SerializedName("i")
    private String instrumentName;
    @SerializedName("v")
    private String total24hTradedVolume;
    @SerializedName("vv")
    private String total24hTradedVolumeUSD;
    @SerializedName("oi")
    private String openInterest;
    @Nullable
    @SerializedName("c")
    private String priceChange24h;
    @Nullable
    @SerializedName("b")
    private String bestBidPrice;
    @Nullable
    @SerializedName("k")
    private String bestAskPrice;
    @SerializedName("t")
    private long timestamp;

    public Ticker() {
    }

    public Ticker(String highest24hTrade, @Nullable String lowest24hTrade, @Nullable String latestTrade, String instrumentName, String total24hTradedVolume, String total24hTradedVolumeUSD, String openInterest, @Nullable String priceChange24h, @Nullable String bestBidPrice, @Nullable String bestAskPrice, long timestamp) {
        this.highest24hTrade = highest24hTrade;
        this.lowest24hTrade = lowest24hTrade;
        this.latestTrade = latestTrade;
        this.instrumentName = instrumentName;
        this.total24hTradedVolume = total24hTradedVolume;
        this.total24hTradedVolumeUSD = total24hTradedVolumeUSD;
        this.openInterest = openInterest;
        this.priceChange24h = priceChange24h;
        this.bestBidPrice = bestBidPrice;
        this.bestAskPrice = bestAskPrice;
        this.timestamp = timestamp;
    }

    public String getHighest24hTrade() {
        return highest24hTrade;
    }

    @Nullable
    public String getLowest24hTrade() {
        return lowest24hTrade;
    }

    @Nullable
    public String getLatestTrade() {
        return latestTrade;
    }

    public String getInstrumentName() {
        return instrumentName;
    }

    public String getTotal24hTradedVolume() {
        return total24hTradedVolume;
    }

    public String getTotal24hTradedVolumeUSD() {
        return total24hTradedVolumeUSD;
    }

    public String getOpenInterest() {
        return openInterest;
    }

    @Nullable
    public String getPriceChange24h() {
        return priceChange24h;
    }

    @Nullable
    public String getBestBidPrice() {
        return bestBidPrice;
    }

    @Nullable
    public String getBestAskPrice() {
        return bestAskPrice;
    }

    public BigDecimal getHighest24hTradeBigD() {
        if (highest24hTrade == null) {
            return null;
        }
        return new BigDecimal(highest24hTrade);
    }

    public BigDecimal getLowest24hTradeBigD() {
        if (lowest24hTrade == null) {
            return null;
        }
        return new BigDecimal(lowest24hTrade);
    }

    public BigDecimal getLatestTradeBigD() {
        if (latestTrade == null) {
            return null;
        }
        return new BigDecimal(latestTrade);
    }

    public BigDecimal getTotal24hTradedVolumeBigD() {
        if (total24hTradedVolume == null) {
            return null;
        }
        return new BigDecimal(total24hTradedVolume);
    }

    public BigDecimal getTotal24hTradedVolumeUSDBigD() {
        if (total24hTradedVolumeUSD == null) {
            return null;
        }
        return new BigDecimal(total24hTradedVolumeUSD);
    }

    public BigDecimal getOpenInterestBigD() {
        if (openInterest == null) {
            return null;
        }
        return new BigDecimal(openInterest);
    }

    public BigDecimal getPriceChange24hBigD() {
        if (priceChange24h == null) {
            return null;
        }
        return new BigDecimal(priceChange24h);
    }

    public BigDecimal getBestBidPriceBigD() {
        if (bestBidPrice == null) {
            return null;
        }
        return new BigDecimal(bestBidPrice);
    }

    public BigDecimal getBestAskPriceBigD() {
        if (bestAskPrice == null) {
            return null;
        }
        return new BigDecimal(bestAskPrice);
    }

    public LocalDateTime getDateTime(){
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
    }

    public Ticker convertCurrency(String targetCurrency) {
        if (instrumentName.endsWith(targetCurrency)) {
            return this;
        }

        CurrencyExchange exchange = CurrencyExchange.getInstance();

        String newInstrumentName = instrumentName.substring(0, instrumentName.indexOf("_") + 1) + targetCurrency;
        String newHighest24hTrade = exchange.convertToCurrencyString(highest24hTrade, targetCurrency);
        String newLowest24hTrade = exchange.convertToCurrencyString(lowest24hTrade, targetCurrency);
        String newLatestTrade = exchange.convertToCurrencyString(latestTrade, targetCurrency);
        String newTotal24hTradedVolume = exchange.convertToCurrencyString(total24hTradedVolume, targetCurrency);
        String newTotal24hTradedVolumeUSD = exchange.convertToCurrencyString(total24hTradedVolumeUSD, targetCurrency);
        String newOpenInterest = exchange.convertToCurrencyString(openInterest, targetCurrency);
        String newPriceChange24h = exchange.convertToCurrencyString(priceChange24h, targetCurrency);
        String newBestBidPrice = exchange.convertToCurrencyString(bestBidPrice, targetCurrency);
        String newBestAskPrice = exchange.convertToCurrencyString(bestAskPrice, targetCurrency);

        return new Ticker(
                newHighest24hTrade,
                newLowest24hTrade,
                newLatestTrade,
                newInstrumentName,
                newTotal24hTradedVolume,
                newTotal24hTradedVolumeUSD,
                newOpenInterest,
                newPriceChange24h,
                newBestBidPrice,
                newBestAskPrice,
                timestamp
        );
    }
}
