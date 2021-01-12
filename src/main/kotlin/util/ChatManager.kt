package util

import dev.kord.common.Color
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.entity.channel.TextChannel

object ChatManager {
    suspend fun sendError(message: String, channel: MessageChannelBehavior) {
        channel.createEmbed {
            description = message
            color = Color(255, 0, 0)
        }
    }
}