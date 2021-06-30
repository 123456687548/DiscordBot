package commands

import command.Command
import data.Permission
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.event.message.MessageCreateEvent
import util.ChatManager
import java.io.InputStreamReader

import java.io.BufferedReader
import kotlin.concurrent.thread


object RestartCmd : Command("restart", Permission.SERVER_OWNER) {
    @ExperimentalStdlibApi
    override suspend fun exec(event: MessageCreateEvent, args: List<String>) {
        event.message.delete()
        if (!Main.restart()) {
            ChatManager.sendError("Bot isn't running on Linux!", event.message.channel)
        }
    }
}