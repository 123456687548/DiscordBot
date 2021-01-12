package commands

import command.Command
import data.Permission
import dev.kord.common.entity.PresenceStatus
import dev.kord.core.event.message.MessageCreateEvent
import features.DiscordBot

@ExperimentalStdlibApi
object StopCmd : Command("stop", Permission.BOT_OWNER) {
    override suspend fun exec(event: MessageCreateEvent, args: List<String>) {
        val bot = DiscordBot.INSTANCE.bot

        event.message.delete()

        bot.editPresence {
            status = PresenceStatus.Offline
        }
        bot.shutdown()
    }
}