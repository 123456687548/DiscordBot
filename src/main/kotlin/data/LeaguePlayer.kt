package data

import com.google.gson.Gson
import java.io.File
import kotlin.jvm.Throws

@ExperimentalStdlibApi
enum class PlayerProvider {
    INSTANCE;

    val players: Map<String, LeaguePlayer>

    init {
        val jsonFile = File("user.json")
        val json = String(jsonFile.readBytes())
        val playerList = Gson().fromJson<List<LeaguePlayer>>(json)
        players = buildMap {
            playerList.forEach { player ->
                this[player.summonerName] = player
            }
        }
    }

    @Throws(NoSuchElementException::class)
    fun get(name: String) = players[name] ?: throw NoSuchElementException("\"$name\" is not specified in user.json")
}

data class LeaguePlayer(val summonerName: String, val realName: String, var ingame: Boolean = false, var lastMatchId: Long = -1L)
