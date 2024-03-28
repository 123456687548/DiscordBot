package eu.time.discordbot.discord;

import eu.time.discordbot.crypto.BitcointHalving;
import eu.time.discordbot.crypto.CryptoExecutor;
import eu.time.discordbot.discord.command.Command;
import eu.time.discordbot.discord.listeners.command.SlashCommandListener;
import eu.time.discordbot.discord.listeners.command.TextCommandListener;
import eu.time.discordbot.discord.listeners.message.ChatListener;
import eu.time.discordbot.discord.listeners.user.UserListener;
import eu.time.discordbot.discord.listeners.voice.VoiceListener;
import eu.time.discordbot.discord.util.MessageHandler;
import eu.time.discordbot.eis.EisExecutor;
import eu.time.discordbot.util.PropertiesUtil;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.util.Collection;
import java.util.EnumSet;

public enum DiscordBot {
    INSTANCE;
    public static final Logger LOG = LoggerFactory.getLogger(DiscordBot.class);
    public static final boolean IS_DEBUG = ManagementFactory.getRuntimeMXBean().getInputArguments().toString().contains("-agentlib:jdwp");

    public static final long EINGANGSHALLEN_CHANNEL_ID = 362947456490668033L;
    public static final long CRYPTO_CHANNEL_ID = 914822335146651660L;
    public static final long ADMIN_CHANNEL_ID = 362948120658575360L;
    private JDA jda;
    private static String version = "V. 1.3.1";

    static {
        if (IS_DEBUG) {
            version = version + " - DEBUG";
        }
    }

    DiscordBot() {
    }

    public void launch() {
        try {
            SlashCommandListener slashCommandListener = new SlashCommandListener();

            jda = JDABuilder.createDefault(PropertiesUtil.getProperty("discord-api"))
                    .addEventListeners(new TextCommandListener())
                    .addEventListeners(slashCommandListener)
                    .addEventListeners(new VoiceListener())
                    .addEventListeners(new UserListener())
                    .addEventListeners(new ChatListener())
                    .setEnabledIntents(EnumSet.allOf(GatewayIntent.class))
                    .setActivity(Activity.playing(version))
                    .build();

            Collection<Command<SlashCommandInteractionEvent>> slashCommands = slashCommandListener.getCommands();
            slashCommands.forEach(slashCommand -> jda.upsertCommand(slashCommand.getName(), slashCommand.getDescription()).addOptions(slashCommand.getOptions()).queue());
            jda.awaitReady();

            new BitcointHalving().startTimer(this);

            launchCryptoQuery();
            //            launchEisQuery();

            MessageHandler.create(jda);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void launchCryptoQuery() {
        CryptoExecutor cryptoExecutor = new CryptoExecutor(this);
        cryptoExecutor.startExecutionAt(23, 59, 59);
    }

    private void launchEisQuery() {
        EisExecutor eisExecutor = new EisExecutor(this);
        eisExecutor.startExecutionAt(10, 0, 0);
    }

    public JDA getJda() {
        return jda;
    }
}
