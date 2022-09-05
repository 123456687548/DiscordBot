package eu.time.discordbot.discord.listeners.voice;

import eu.time.discordbot.discord.listeners.DiscordListener;
import eu.time.discordbot.discord.util.ChannelUtil;
import eu.time.discordbot.util.TimeUtil;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.voice.*;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class VoiceListener extends DiscordListener {
    private final String VOICE_CHANNEL_JOIN_EMOTE = ":white_check_mark:";
    private final String VOICE_CHANNEL_LEAVE_EMOTE = ":small_red_triangle_down:";
    private final String VOICE_CHANNEL_CHANGE_EMOTE = ":arrow_right:";

    private final String VOICE_CHANNEL_JOIN_LEAVE_TEMPLATE = "%s  %s**%s** %s voice channel `%s`";
    private final String VOICE_CHANNEL_CHANGE_TEMPLATE = "%s  %s**%s** went from `%s` to `%s`";

    private final String VOICE_LOG_CHANNEL_NAME = "voicelog";

    @Override
    public void onGuildVoiceJoin(@Nonnull GuildVoiceJoinEvent event) {
        AudioChannel channelJoined = event.getChannelJoined();
        String username = event.getMember().getEffectiveName();

        if (isAdminChannel(channelJoined)) return;

        sendJoinMessage(event.getGuild(), channelJoined, username);
    }

    @Override
    public void onGuildVoiceLeave(@Nonnull GuildVoiceLeaveEvent event) {
        AudioChannel channelLeft = event.getChannelLeft();
        String username = event.getMember().getEffectiveName();
        if (isAdminChannel(channelLeft)) return;

        sendLeaveMessage(event.getGuild(), channelLeft, username);
    }

    @Override
    public void onGuildVoiceMove(@Nonnull GuildVoiceMoveEvent event) {
        AudioChannel channelLeft = event.getChannelLeft();
        AudioChannel channelJoined = event.getChannelJoined();
        Guild guild = event.getGuild();
        String username = event.getMember().getEffectiveName();

        if (isAdminChannel(channelJoined)) {
            sendLeaveMessage(guild, channelLeft, username);
            return;
        }

        if (isAdminChannel(channelLeft)) {
            sendJoinMessage(guild, channelJoined, username);
            return;
        }

        sendMoveMessage(guild, channelJoined, channelLeft, username);
    }

    private void sendJoinMessage(Guild guild, AudioChannel channelJoined, String username) {
        getOrCreateVoiceLogChannel(guild, voiceLogChannel -> {
            messageHandler.sendMessage(voiceLogChannel, String.format(VOICE_CHANNEL_JOIN_LEAVE_TEMPLATE, VOICE_CHANNEL_JOIN_EMOTE, TimeUtil.getTime(), username, "joined", channelJoined.getName()));
        });
    }

    private void sendLeaveMessage(Guild guild, AudioChannel channelLeft, String username) {
        getOrCreateVoiceLogChannel(guild, voiceLogChannel -> {
            messageHandler.sendMessage(voiceLogChannel, String.format(VOICE_CHANNEL_JOIN_LEAVE_TEMPLATE, VOICE_CHANNEL_LEAVE_EMOTE, TimeUtil.getTime(), username, "left", channelLeft.getName()));
        });
    }

    private void sendMoveMessage(Guild guild, AudioChannel channelJoined, AudioChannel channelLeft, String username) {
        getOrCreateVoiceLogChannel(guild, voiceLogChannel -> {
            messageHandler.sendMessage(voiceLogChannel, String.format(VOICE_CHANNEL_CHANGE_TEMPLATE, VOICE_CHANNEL_CHANGE_EMOTE, TimeUtil.getTime(), username, channelLeft.getName(), channelJoined.getName()));
        });
    }

    private boolean isAdminChannel(AudioChannel channel) {
        return channel.getName().compareToIgnoreCase("Admin Raum") == 0;
    }

    private void getOrCreateVoiceLogChannel(Guild guild, Consumer<TextChannel> response) {
        ChannelUtil.getOrCreateChannel(guild, VOICE_LOG_CHANNEL_NAME, response);
    }
}
