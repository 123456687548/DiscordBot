package commands

import command.Command
import data.Permission
import dev.kord.core.entity.Message
import dev.kord.core.event.message.MessageCreateEvent
import features.BBKApi
import features.RiotApi
import features.TwitchApi
import kotlinx.coroutines.delay

@ExperimentalStdlibApi
object EnableCmd : Command("enable", Permission.ADMIN) {
    override suspend fun exec(event: MessageCreateEvent, args: List<String>) {
        val message = event.message

        if (args.isEmpty()) {
            printHelp(message)
            return
        }

        when (args[0].trim().lowercase()) {
            RiotApi.INSTANCE.apiName -> RiotApi.INSTANCE.enable()
            TwitchApi.INSTANCE.apiName -> TwitchApi.INSTANCE.enable()
            BBKApi.INSTANCE.apiName -> BBKApi.INSTANCE.enable()
            else -> {
                printHelp(message)
                return
            }
        }

        val response = message.channel.createMessage("Enabled ${args[0]} API")

        delay(5000)
        message.delete()
        response.delete()
    }

    private suspend fun printHelp(message: Message) {
        message.channel.createMessage("Available APIs:\n${RiotApi.INSTANCE.apiName}\n${TwitchApi.INSTANCE.apiName}\n${BBKApi.INSTANCE.apiName}")
    }
}
