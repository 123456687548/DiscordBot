package eu.time.discordbot.discord;

import eu.time.discordbot.crypto.BitcointHalvin;
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

import java.util.Collection;
import java.util.EnumSet;

public enum DiscordBot {
    INSTANCE;
    public static final Logger LOG = LoggerFactory.getLogger(DiscordBot.class);

    public static final long EINGANGSHALLEN_CHANNEL_ID = 362947456490668033L;

    private boolean initalized = false;
    private JDA jda;

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
                    .setActivity(Activity.playing("V. 1.2"))
                    .build();

            Collection<Command<SlashCommandInteractionEvent>> slashCommands = slashCommandListener.getCommands();
            slashCommands.forEach(slashCommand -> jda.upsertCommand(slashCommand.getName(), slashCommand.getDescription()).addOptions(slashCommand.getOptions()).queue());
            jda.awaitReady();

            new BitcointHalvin(this).startTimer();

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

    public static DiscordBot getInstance() {
        if (!INSTANCE.initalized) {
            INSTANCE.launch();
            INSTANCE.initalized = true;
        }

        return INSTANCE;
    }
}
