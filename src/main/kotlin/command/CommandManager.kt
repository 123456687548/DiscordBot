package command

import commands.*
import dev.kord.common.Color
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.event.message.MessageCreateEvent
import util.ChatManager

@ExperimentalStdlibApi
class CommandManager {
    companion object {
        val PREFIX = '!'
    }

    private val commands = mapOf(
        PingCmd.getPair(),
        ClearCmd.getPair(),
        RestartCmd.getPair(),
        StopCmd.getPair(),
        EnableCmd.getPair(),
        DisableCmd.getPair(),
        RunningCmd.getPair()
    )

    suspend fun handleCommand(event: MessageCreateEvent) {
        val message = event.message.content.trim().toLowerCase()
        if (!message.startsWith(PREFIX)) return
        val messageWithoutPrefix = message.substring(1)

        val command = parseCommand(messageWithoutPrefix)

        if (command == null) {
            ChatManager.sendError("Command does not exist!", event.message.channel)
            return
        }

        val args = messageWithoutPrefix.split(' ').drop(1)

        if (!command.hasPermission(event)) {
            ChatManager.sendError("You don't have the needed permissions", event.message.channel)
            return
        }

        command.exec(event, args)
    }

    private fun parseCommand(message: String): Command? {
        val commandName = message.split(' ')[0]
        return commands[commandName]
    }

}
