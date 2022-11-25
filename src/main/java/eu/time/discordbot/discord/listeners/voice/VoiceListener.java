package eu.time.discordbot.discord.listeners.voice;

import eu.time.discordbot.discord.listeners.DiscordListener;
import eu.time.discordbot.discord.util.ChannelUtil;
import eu.time.discordbot.util.TimeUtil;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class VoiceListener extends DiscordListener {
    private final String VOICE_CHANNEL_JOIN_EMOTE = ":white_check_mark:";
    private final String VOICE_CHANNEL_LEAVE_EMOTE = ":small_red_triangle_down:";
    private final String VOICE_CHANNEL_CHANGE_EMOTE = ":arrow_right:";

    private final String VOICE_CHANNEL_JOIN_LEAVE_TEMPLATE = "%s  %s**%s** %s voice channel `%s`";
    private final String VOICE_CHANNEL_CHANGE_TEMPLATE = "%s  %s**%s** went from `%s` to `%s`";

    private static final String VOICE_LOG_CHANNEL_NAME = "voicelog";
    private static final String ADMIN_CHANNEL_NAME = "Admin Raum";

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        AudioChannelUnion channelJoined = event.getChannelJoined();
        AudioChannelUnion channelLeft = event.getChannelLeft();

        if(channelLeft != null && channelJoined != null){
            onGuildVoiceMove(event);
            return;
        }

        if(channelLeft != null) onGuildVoiceLeave(event);
        if(channelJoined != null) onGuildVoiceJoin(event);
    }

    private void onGuildVoiceJoin(@NotNull GuildVoiceUpdateEvent event) {
        AudioChannel channelJoined = event.getChannelJoined();
        String username = event.getMember().getEffectiveName();

        if (isAdminChannel(channelJoined)) return;

        sendJoinMessage(event.getGuild(), channelJoined, username);
    }

    private void onGuildVoiceLeave(@NotNull GuildVoiceUpdateEvent event) {
        AudioChannel channelLeft = event.getChannelLeft();
        String username = event.getMember().getEffectiveName();
        if (isAdminChannel(channelLeft)) return;

        sendLeaveMessage(event.getGuild(), channelLeft, username);
    }

    private void onGuildVoiceMove(@NotNull GuildVoiceUpdateEvent event) {
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
        return channel.getName().compareToIgnoreCase(ADMIN_CHANNEL_NAME) == 0;
    }

    private void getOrCreateVoiceLogChannel(Guild guild, Consumer<TextChannel> response) {
        ChannelUtil.getOrCreateChannel(guild, VOICE_LOG_CHANNEL_NAME, response);
    }
}
