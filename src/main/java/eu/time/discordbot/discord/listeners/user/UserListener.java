package eu.time.discordbot.discord.listeners.user;

import eu.time.discordbot.discord.listeners.DiscordListener;
import eu.time.discordbot.discord.util.ChannelUtil;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateNameEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class UserListener extends DiscordListener {
    private final String UPDATE_NAME_TEMPLATE = ":pencil:   `%s` changed name to `%s`";

    @Override
    public void onUserUpdateName(@NotNull UserUpdateNameEvent event) {
        String oldName = event.getOldName();
        String newName = event.getNewName();

        User user = event.getUser();
        List<Guild> mutualGuilds = user.getMutualGuilds();

        mutualGuilds.forEach(guild -> {
            List<TextChannel> channels = ChannelUtil.getChannels(guild, "admin-chat");
            if (channels.isEmpty()) return;

            TextChannel adminChannel = channels.get(0);
            sendUpdateNameMessage(adminChannel, newName, oldName);
        });
    }

    @Override
    public void onGuildMemberUpdateNickname(@NotNull GuildMemberUpdateNicknameEvent event) {
        String oldName = event.getOldNickname();
        String newName = event.getNewNickname();

        Guild guild = event.getGuild();
        List<TextChannel> channels = ChannelUtil.getChannels(guild, "admin-chat");
        if (channels.isEmpty()) return;

        TextChannel adminChannel = channels.get(0);
        sendUpdateNameMessage(adminChannel, newName, oldName);
    }

    private void sendUpdateNameMessage(TextChannel adminChannel, String newName, String oldName) {
        messageHandler.sendMessage(adminChannel, String.format(UPDATE_NAME_TEMPLATE, oldName, newName));
    }
}
