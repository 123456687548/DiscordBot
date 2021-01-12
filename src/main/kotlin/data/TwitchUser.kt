package data

import com.google.gson.Gson
import dev.kord.common.entity.DiscordChannel
import java.io.File
import kotlin.jvm.Throws

@ExperimentalStdlibApi
enum class TwitchUserProvider {
    INSTANCE;

    val users: Map<String, TwitchUser>

    init {
        val jsonFile = File("streams.json")
        val json = String(jsonFile.readBytes())
        val userList = Gson().fromJson<List<TwitchUser>>(json)
        users = buildMap {
            userList.forEach { user ->
                this[user.username] = user
            }
        }
    }

    @Throws(NoSuchElementException::class)
    fun get(name: String) = users[name] ?: throw NoSuchElementException("\"$name\" is not specified in streams.json")
}

data class TwitchUser(val username: String, val realName: String, val showLink: Boolean = false)
