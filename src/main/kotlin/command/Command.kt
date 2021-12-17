package command

import data.Permission
import data.Settings
import dev.kord.core.entity.Member
import dev.kord.core.event.message.MessageCreateEvent

abstract class Command(private val name: String, val permission: Permission) {
    abstract suspend fun exec(event: MessageCreateEvent, args: List<String>)
    suspend fun hasPermission(event: MessageCreateEvent) : Boolean{
        if(event.member == null) return false
        val memberPermission = event.member!!.getPermissionLevel()

        return memberPermission >= permission
    }

    fun getPair(): Pair<String, Command> = name to this
}

suspend fun Member.getPermissionLevel(): Permission {
    if(isBotOwner()) return Permission.BOT_OWNER
    if(isOwner()) return Permission.SERVER_OWNER
    if(getPermissions().contains(dev.kord.common.entity.Permission.Administrator)) return Permission.ADMIN
    return Permission.NORMAL
}

fun Member.isBotOwner() = id.value.toLong() == Settings.instance.owner
