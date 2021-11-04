package features

import com.google.gson.Gson
import data.Settings
import data.bbk.DangerWarning
import data.bbk.DangerWarningItem
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.channel.TextChannel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME
import kotlin.NoSuchElementException

@ExperimentalStdlibApi
enum class BBKApi {
    INSTANCE;

    private val url = URL("https://warnung.bund.de/bbk.mowas/gefahrendurchsagen.json")

    private var initialized = false
    private lateinit var bot: Kord

    private var knownWarnings = ArrayList<DangerWarningItem>()

    fun initialize() {

        if (initialized) return

        this.bot = DiscordBot.INSTANCE.bot

        start()
    }

    private fun start() {
        GlobalScope.launch {
            while (true) {
                val dangerWarnings = sendRequest()

                evaluateWarnings(dangerWarnings)

                delay(600000L)
            }
        }.invokeOnCompletion { throwable ->
            runBlocking {
                throwable?.let { it1 -> DiscordBot.INSTANCE.sendErrorMessage(it1) }

                bot.editPresence {
                    playing("ERROR BBK")
                }
            }
            start()
        }
    }

    private fun sendRequest(): DangerWarning {
        val result = StringBuilder()

        with(url.openConnection() as HttpURLConnection) {
            requestMethod = "GET"  // optional default is GET

            println("\nSent 'GET' request to URL : $url; Response Code : $responseCode")

            inputStream.bufferedReader().use {
                it.lines().forEach { line ->
                    result.append(line)
                }
            }
        }

        return Gson().fromJson(result.toString(), DangerWarning::class.java)
    }

    private suspend fun evaluateWarnings(warning: DangerWarning) {
        val currentTime = LocalDateTime.now()

        val filteredWarnings = warning.filter { filterdWarning ->
            val dateString = filterdWarning.sent
            val sendDate = LocalDateTime.parse(dateString, ISO_OFFSET_DATE_TIME)
            currentTime.minusMinutes(30L).isBefore(sendDate)
        }.filter { filterdWarning ->
            val infos = filterdWarning.info.filter { info ->
                val areas = info.area.filter { area ->
                    val geocodes = area.geocode.filter { geocode -> geocode.valueName == "Köln" }
                    geocodes.isNotEmpty()
                }
                areas.isNotEmpty()
            }
            infos.isNotEmpty()
        }

        filteredWarnings.forEach { filteredWarning ->
            filteredWarning.info.forEach {
                val headline = it.headline;
                val description = it.description.replace("<br/>", "\n");
                var message = "";
                if (headline == "Bombenfund") {
                    message = String.format("%s\n\n%s\n\nhttps://%s", headline, description, it.web);
                } else {
                    message = if (it.web.isNullOrBlank()) it.headline else String.format("%s\nhttps://%s", it.headline, it.web)
                }
                if (!knownWarnings.contains(filteredWarning)) {
                    knownWarnings.add(filteredWarning)
                    println(message)
                    sendDiscordMessage(message)
                }
            }
        }
        cleanKnownWarnings()
    }

    private fun cleanKnownWarnings() {
        val currentTime = LocalDateTime.now()
        knownWarnings = knownWarnings.filter { warning ->
            val dateString = warning.sent
            val sendDate = LocalDateTime.parse(dateString, ISO_OFFSET_DATE_TIME)
            currentTime.minusMinutes(60L).isBefore(sendDate)
        } as ArrayList<DangerWarningItem>
    }

    private suspend fun sendDiscordMessage(message: String) {
        val settings = Settings.instance

        val server = bot.getGuild(Snowflake(settings.bbk_tracker_server_id)) ?: throw NoSuchElementException("serverId ${settings.bbk_tracker_server_id} does not exist")
        val channel = server.getChannel(Snowflake(settings.bbk_tracker_channel))

        if (channel !is TextChannel) return
        channel.createMessage(message)
    }
}
