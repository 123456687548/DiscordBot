package features

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential
import com.github.twitch4j.TwitchClient
import com.github.twitch4j.TwitchClientBuilder
import com.merakianalytics.orianna.Orianna
import com.merakianalytics.orianna.types.common.Region
import data.PlayerProvider
import data.SecretProvider
import data.Settings
import data.TwitchUserProvider
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.Guild
import dev.kord.core.entity.channel.TextChannel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread

@ExperimentalStdlibApi
enum class TwitchApi {
    INSTANCE;

    private lateinit var bot: Kord
    private lateinit var client: TwitchClient

    var isOnline = false
    var wasOnline = false

    val streamers = TwitchUserProvider.INSTANCE.users

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
        GlobalScope.launch {
            while (true) {
                val streams = client.helix.getStreams(null, null, null, 1, null, null, null, streamers.keys.toList()).execute()
                streams.streams.forEach {
                    if(!isOnline) {
                        sendDiscordMessage("${it.userName} streamt\nTitle: ${it.title}")
                    }
                    isOnline = true
//                    println("${it.userName} streamt\nTitle: ${it.title}")
                }
                if(streams.streams.isEmpty()){
                    isOnline = false
                }
                delay(60000L)
            }
        }.invokeOnCompletion {
            runBlocking {
                bot.editPresence {
                    playing("ERROR TWITCH")
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
}