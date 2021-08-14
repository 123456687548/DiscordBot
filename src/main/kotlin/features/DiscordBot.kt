package features

import command.CommandManager
import data.SecretProvider
import dev.kord.core.Kord
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.event.user.VoiceStateUpdateEvent
import dev.kord.core.on
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import util.Time
import util.Updater

@ExperimentalStdlibApi
enum class DiscordBot {
    INSTANCE;

    val VOICE_CHANNEL_JOIN_EMOTE = ":white_check_mark:"
    val VOICE_CHANNEL_LEAVE_EMOTE = ":small_red_triangle_down:"
    val VOICE_CHANNEL_CHANGE_EMOTE = ":arrow_right:"

    val VOICE_CHANNEL_JOIN_LEAVE_TEMPLATE = "%s  %s**%s** %s voice channel `%s`"
    val VOICE_CHANNEL_CHANGE_TEMPLATE = "%s  %s**%s** went from `%s` to `%s`"

    lateinit var bot: Kord
    private val commandManager = CommandManager()

    private var initialized = false

    suspend fun initialize() {
        if (initialized) return

        try {
            bot = Kord(SecretProvider.INSTANCE.get("discord-api").secret)

            addListeners()

            bot.login()
        } catch (e: NoSuchElementException) {
            e.printStackTrace()
        }
    }

    private fun addListeners() {
        bot.on<MessageCreateEvent> {
            val authorId = this.message.author?.id ?: return@on
            if (authorId == bot.selfId) return@on

            val event = this
            val message = this.message.content

            if (message.startsWith(CommandManager.PREFIX)) {
                commandManager.handleCommand(this)
            }

            if (message.matches(Regex("https?://(www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([\\w\\W()@:%_+.~#?&/=]*)")) && message.contains("amazon")) {
                this.message.channel.createMessage(String.format("From: %s\n%s", event.message.author?.mention, shortenLink(message)))
                event.message.delete()
            }
        }
        bot.on<ReadyEvent> {
            RiotApi.INSTANCE.initialize()
//            TwitchApi.INSTANCE.initialize()
            BBKApi.INSTANCE.initialize()
            initialized = true

            bot.editPresence {
                playing(" on Version ${Updater.CURRENT_VERSION_STRING}")
            }
        }

        bot.on<VoiceStateUpdateEvent> {
            val old = this.old
            val new = this.state

            val member = this.state.getMember()
            val channel = this.state.getChannelOrNull()

            val voiceLogChannel = new.getGuild().channels.first {
                (it is TextChannel && it.name == "voicelog")
            }.asChannelOrNull()

            var message: String? = null

            if (old == null) {
                if (new.channelId != null) {
                    message = createChannelJoinMessage(member.displayName, channel!!.asChannel().name)
                }
            } else {
                if (new.channelId != null) {
                    if (old.channelId == null) {
                        message = createChannelJoinMessage(member.displayName, channel!!.asChannel().name)
                    } else if (old.channelId != new.channelId) {
                        message = createChannelChangeMessage(member.displayName, this.old?.getChannelOrNull()!!.asChannel().name, channel!!.asChannel().name)
                    }
                }

                if (old.channelId != null && new.channelId == null) {
                    message = createChannelLeaveMessage(member.displayName, this.old?.getChannelOrNull()!!.asChannel().name)
                }
            }
            if (voiceLogChannel != null && voiceLogChannel is TextChannel && message != null) {
                voiceLogChannel.createMessage(message)
            }
        }.invokeOnCompletion { cause ->
            if (cause != null && cause !is CancellationException) {
                runBlocking {
                    bot.editPresence {
                        playing("ERROR")
                    }
                }
            }
        }
    }

    private fun createChannelLeaveMessage(name: String, channel: String) = String.format(VOICE_CHANNEL_JOIN_LEAVE_TEMPLATE, VOICE_CHANNEL_LEAVE_EMOTE, Time.getTime(), name, "left", channel)
    private fun createChannelJoinMessage(name: String, channel: String) = String.format(VOICE_CHANNEL_JOIN_LEAVE_TEMPLATE, VOICE_CHANNEL_JOIN_EMOTE, Time.getTime(), name, "joined", channel)
    private fun createChannelChangeMessage(name: String, fromChannel: String, toChannel: String) =
        String.format(VOICE_CHANNEL_CHANGE_TEMPLATE, VOICE_CHANNEL_CHANGE_EMOTE, Time.getTime(), name, fromChannel, toChannel)

    private fun getProductID(link: String): String {
        val idStart = if (!link.contains("dp/")) link.indexOf("product/") + 8 else link.indexOf("dp/") + 3
        val productId = link.substring(idStart)
        val idEnd = if (productId.indexOf('/') == -1) productId.indexOf('?') else productId.indexOf('/')
        return productId.substring(0, idEnd)
    }

    private fun getVideoID(link: String): String {
        val sdf = "/gp/video/detail/"
        val idStart = link.indexOf(sdf) + sdf.length
        val videoId = link.substring(idStart)
        val idEnd = if (videoId.indexOf('/') == -1) videoId.indexOf('?') else videoId.indexOf('/')
        return videoId.substring(0, idEnd)
    }

    private fun getDomain(link: String): String {
        val domain = link.split('.')[2]
        return domain.substring(0, domain.indexOf('/'))
    }

    private fun shortenLink(link: String): String {
        val domain = getDomain(link)
        return if (link.contains("/gp/")) {
            String.format("https://www.amazon.%s/gp/video/detail/%s", domain, getVideoID(link))
        } else {
            String.format("https://www.amazon.%s/dp/%s", domain, getProductID(link))
        }
    }
}