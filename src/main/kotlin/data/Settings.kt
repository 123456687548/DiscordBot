package data

import com.google.gson.Gson
import java.io.File

class Settings {
    private constructor()

    companion object {
        val instance : Settings = create()
        private fun create() : Settings{
            val jsonFile = File("settings.json")
            val json = String(jsonFile.readBytes())
            return Gson().fromJson<Settings>(json)
        }
    }

    val owner: Long = -1
    val league_tracker_server_id: Long = -1L
    val league_tracker_channel: Long = -1L
    val twitch_tracker_channel: Long = -1L
    val bbk_tracker_server_id: Long = -1L
    val bbk_tracker_channel: Long = -1L
}