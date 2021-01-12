package commands

import command.Command
import data.Permission
import dev.kord.core.entity.ReactionEmoji
import dev.kord.core.event.message.MessageCreateEvent
import kotlinx.coroutines.delay

object PingCmd : Command("ping", Permission.NORMAL) {
    val pingPong = ReactionEmoji.Unicode("\uD83C\uDFD3")

    override suspend fun exec(event: MessageCreateEvent, args: List<String>) {
        val message = event.message

        val response = message.channel.createMessage("Pong!")
        response.addReaction(pingPong)

        delay(5000)
        message.delete()
        response.delete()
    }

}