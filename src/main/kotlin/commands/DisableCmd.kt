package commands

import command.Command
import commands.PingCmd.pingPong
import data.Permission
import dev.kord.core.entity.Message
import dev.kord.core.entity.ReactionEmoji
import dev.kord.core.event.message.MessageCreateEvent
import features.BBKApi
import features.RiotApi
import features.TwitchApi
import kotlinx.coroutines.delay

@ExperimentalStdlibApi
object DisableCmd : Command("disable", Permission.ADMIN) {
    override suspend fun exec(event: MessageCreateEvent, args: List<String>) {
        val message = event.message

        if (args.isEmpty()) {
            printHelp(message)
            return
        }

        when (args[0].trim().lowercase()) {
            RiotApi.INSTANCE.apiName -> RiotApi.INSTANCE.disable()
            TwitchApi.INSTANCE.apiName -> TwitchApi.INSTANCE.disable()
            BBKApi.INSTANCE.apiName -> BBKApi.INSTANCE.disable()
            else -> {
                printHelp(message)
                return
            }
        }

        val response = message.channel.createMessage("Disabled ${args[0]} API")

        delay(5000)
        message.delete()
        response.delete()
    }

    private suspend fun printHelp(message: Message) {
        message.channel.createMessage("Available APIs:\n${RiotApi.INSTANCE.apiName}\n${TwitchApi.INSTANCE.apiName}\n${BBKApi.INSTANCE.apiName}")
    }
}
