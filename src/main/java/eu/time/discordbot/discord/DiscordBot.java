package eu.time.discordbot.discord;

import eu.time.discordbot.discord.command.Command;
import eu.time.discordbot.discord.listeners.message.ChatListener;
import eu.time.discordbot.discord.listeners.user.UserListener;
import eu.time.discordbot.discord.util.MessageHandler;
import eu.time.discordbot.discord.listeners.command.SlashCommandListener;
import eu.time.discordbot.discord.listeners.command.TextCommandListener;
import eu.time.discordbot.discord.listeners.voice.VoiceListener;
import eu.time.discordbot.eis.EisExecutor;
import eu.time.discordbot.util.PropertiesUtil;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;

import java.io.IOException;
import java.util.Collection;
import java.util.EnumSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum DiscordBot {
    INSTANCE;
    public static final Logger LOG = LoggerFactory.getLogger(DiscordBot.class);

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
                .setActivity(Activity.playing("V. 1.0"))
                .build();

            Collection<Command<SlashCommandInteractionEvent>> slashCommands = slashCommandListener.getCommands();
            slashCommands.forEach(slashCommand -> jda.upsertCommand(slashCommand.getName(), slashCommand.getDescription()).addOptions(slashCommand.getOptions()).queue());
            jda.awaitReady();

            //            launchEisQuery();

            MessageHandler.create(jda);
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
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
