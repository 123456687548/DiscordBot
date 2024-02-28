package eu.time.discordbot.crypto;

import eu.time.discordbot.discord.DiscordBot;
import eu.time.discordbot.executor.TimedExecutor;

public class CryptoExecutor extends TimedExecutor {
    private final CryptoQuery cryptoQuery;

    public CryptoExecutor(DiscordBot discordBot) {
        this.cryptoQuery = new CryptoQuery(discordBot);
    }

    @Override
    protected void runTask() {
        cryptoQuery.startNewDay();
    }
}
