package eu.time.discordbot.currency;

import com.google.gson.Gson;
import eu.time.discordbot.http.Requester;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Map;

import static eu.time.discordbot.util.PropertiesUtil.getProperty;

public class CurrencyExchange {
    private static CurrencyExchange INSTANCE = null;
    private static final String CURRENCY_EXCHANGE_RATE_URI = String.format("https://v6.exchangerate-api.com/v6/%s/latest/", getProperty("exchangerateKey"));
    private final Gson gson = new Gson();
    private Requester requester = new Requester();

    private Map<String, Double> currentUSDExchangeRateMap = getCurrentExchangeRate("USD").getConversion_rates();

    public static CurrencyExchange getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CurrencyExchange();
        }
        return INSTANCE;
    }

    public Map<String, Double> getCurrentUSDExchangeRateMap() {
        return currentUSDExchangeRateMap;
    }

    public void updateUSDRate() {
        currentUSDExchangeRateMap = getCurrentExchangeRate("USD").getConversion_rates();
    }

    private ExchangerateResponse getCurrentExchangeRate(String baseCurrency) {
        String response = requester.get(CURRENCY_EXCHANGE_RATE_URI + baseCurrency);
        return gson.fromJson(response, ExchangerateResponse.class);
    }

    public BigDecimal convertToCurrency(@Nullable String value) {
        return convertToCurrency(value, "EUR");
    }

    public BigDecimal convertToCurrency(@Nullable String value, String targetCurrency) {
        if (value == null) {
            return null;
        }
        Double exchangeRate = getCurrentUSDExchangeRateMap().get(targetCurrency);
        return new BigDecimal(value).multiply(BigDecimal.valueOf(exchangeRate), MathContext.DECIMAL64).setScale(5, RoundingMode.HALF_DOWN);
    }

    public String convertToCurrencyString(@Nullable String value, String targetCurrency) {
        BigDecimal result = convertToCurrency(value, targetCurrency);
        if (result == null) {
            return null;
        }
        return result.toString();
    }

    private CurrencyExchange() {
    }
}
