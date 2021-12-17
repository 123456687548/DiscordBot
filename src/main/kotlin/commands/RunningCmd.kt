package commands

import command.Command
import data.Permission
import dev.kord.core.event.message.MessageCreateEvent
import features.BBKApi
import features.RiotApi
import features.TwitchApi

@ExperimentalStdlibApi
object RunningCmd : Command("running", Permission.ADMIN) {
    private const val enabledEmote = ":white_check_mark:"
    private const val disabledEmote = ":x:"

    override suspend fun exec(event: MessageCreateEvent, args: List<String>) {
        val message = event.message

        val msg = String.format(
            "Running APIs:\n%s        %s\n%s   %s\n%s        %s",
            RiotApi.INSTANCE.apiName,
            getEnabledEmote(RiotApi.INSTANCE.isEnabled()),
            TwitchApi.INSTANCE.apiName,
            getEnabledEmote(TwitchApi.INSTANCE.isEnabled()),
            BBKApi.INSTANCE.apiName,
            getEnabledEmote(BBKApi.INSTANCE.isEnabled())
        )

        message.channel.createMessage(msg)
    }

    private fun getEnabledEmote(enabled: Boolean) = if (enabled) enabledEmote else disabledEmote
}
