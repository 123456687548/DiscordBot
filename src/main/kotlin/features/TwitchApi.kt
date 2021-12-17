package features

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential
import com.github.twitch4j.TwitchClient
import com.github.twitch4j.TwitchClientBuilder
import data.SecretProvider
import data.Settings
import data.TwitchUserProvider
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.channel.TextChannel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalStdlibApi::class)
enum class TwitchApi {
    INSTANCE;

    val apiName = "twitch"
    private var enabled = false

    private lateinit var bot: Kord
    private lateinit var client: TwitchClient

    var isOnline = false
    var wasOnline = false

    val streamers = TwitchUserProvider.INSTANCE.users

    private var errorCounter = 0
    private var lastError: Long = 0

    fun initialize() {
        this.bot = DiscordBot.INSTANCE.bot

        try {
            client = TwitchClientBuilder.builder()
                .withDefaultAuthToken(OAuth2Credential("twitch", SecretProvider.INSTANCE.get("twitch-api").secret))
                .withEnableHelix(true)
                .build()

            start()
        } catch (e: NoSuchElementException) {
            e.printStackTrace()
        }
    }

    private fun start() {
        if (!enabled) return

        GlobalScope.launch {
            while (enabled) {
                val streams = client.helix.getStreams(null, null, null, 1, null, null, null, streamers.keys.toList()).execute()
                streams.streams.forEach {
                    if (!isOnline) {
                        sendDiscordMessage("${it.userName} streamt\nTitle: ${it.title}")
                    }
                    isOnline = true
//                    println("${it.userName} streamt\nTitle: ${it.title}")
                }
                if (streams.streams.isEmpty()) {
                    isOnline = false
                }
                delay(60000L)
            }
        }.invokeOnCompletion { throwable ->
            runBlocking {
                throwable?.let { error ->
                    handleError(error)
                }
            }
            start()
        }
    }

    private suspend fun sendDiscordMessage(message: String) {
        val settings = Settings.instance

        val server = bot.getGuild(Snowflake(settings.league_tracker_server_id)) ?: throw NoSuchElementException("serverId ${settings.league_tracker_server_id} does not exist")
        val channel = server.getChannel(Snowflake(settings.twitch_tracker_channel))

        if (channel !is TextChannel) return
        channel.createMessage(message)
    }

    fun enable() {
        if (enabled) return
        enabled = true

        start()
    }

    fun disable() {
        if (!enabled) return
        enabled = false
    }

    fun isEnabled() = enabled

    private suspend fun handleError(error: Throwable) {
        DiscordBot.INSTANCE.sendErrorMessage(error)
        bot.editPresence {
            playing("ERROR TWITCH")
        }

        val nowTime = System.currentTimeMillis()
        val errorTimeDiff = nowTime - lastError
        if (errorTimeDiff < 60000) {
            errorCounter++
        }

        if (errorTimeDiff > 600000) {
            errorCounter = 0
        }

        if (errorCounter >= 5) {
            disable()
            DiscordBot.INSTANCE.sendAdminChannelMessage("Auto disabled $apiName")
        }

        lastError = nowTime
    }
}
