package eu.time.discordbot.discord.command;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public abstract class TextCommand extends Command<MessageReceivedEvent> {
    private final Permission neededPermissions;

    public TextCommand(String name, Permission neededPermissions) {
        super(name);
        this.neededPermissions = neededPermissions;
    }

    @Override
    public void exec(MessageReceivedEvent event, List<String> args) {
        if (!hasPermissions(event)) {
            event.getChannel().sendMessage("You don't have the needed permissions").queue();
            return;
        }
        execIntern(event, args);
    }

    protected abstract void execIntern(MessageReceivedEvent event, List<String> args);

    private boolean hasPermissions(MessageReceivedEvent event) {
        Member member = event.getMember();
        if(member == null) return false;

        Permission memberPermissions = getPermissionLevel(member);

        return memberPermissions.compareTo(neededPermissions) >= 0;
    }

    private Permission getPermissionLevel(Member member) {
//        if (isBotOwner()) return Permission.BOT_OWNER;
        if (isOwner(member)) return Permission.SERVER_OWNER;
        if (member.hasPermission(net.dv8tion.jda.api.Permission.ADMINISTRATOR)) return Permission.ADMIN;
        return Permission.NORMAL;
    }

    private boolean isOwner(Member member) {
        Guild guild = member.getGuild();
        return guild.getOwnerIdLong() == member.getIdLong();
    }
}
