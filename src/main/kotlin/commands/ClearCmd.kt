package commands

import command.Command
import data.Permission
import dev.kord.common.Color
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.GuildMessageChannelBehavior
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.entity.Message
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.event.message.MessageCreateEvent
import kotlinx.coroutines.flow.*
import util.ChatManager
import java.lang.Integer.max
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalUnit
import java.time.temporal.WeekFields
import kotlin.math.min

object ClearCmd : Command("clear", Permission.ADMIN) {
    override suspend fun exec(event: MessageCreateEvent, args: List<String>) {
        val amountToDelete = Integer.valueOf(args[0])
        event.message.delete()

        val toDelete = arrayListOf<Snowflake>()

        event.message.channel.getMessagesBefore(event.message.id, min(amountToDelete, 100))
            .filterNotNull()
            .onEach {
                toDelete.add(it.id)
//                it.delete()
            }.catch {
                it.printStackTrace()
                if (it.message != null) {
                    ChatManager.sendError(it.message!!, event.message.channel)
                }
            }.collect()

        val channel = event.message.getChannel() as TextChannel
        channel.bulkDelete(toDelete)
    }
}