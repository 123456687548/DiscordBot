package data

import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.io.File
import kotlin.jvm.Throws

@ExperimentalStdlibApi
enum class SecretProvider {
    INSTANCE;

    private val secrets: Map<String, Secret>

    init {
        val jsonFile = File("secrets.json")
        val json = String(jsonFile.readBytes())
        val secretsList = Gson().fromJson<List<Secret>>(json)
        secrets = buildMap {
            secretsList.forEach { secret ->
                this[secret.name] = secret
            }
        }
    }

    @Throws(NoSuchElementException::class)
    fun get(name: String) = secrets[name] ?: throw NoSuchElementException("\"$name\" is not specified in secrets.json")
}

data class Secret(val name: String, val secret: String)

inline fun <reified T> Gson.fromJson(json: String) = fromJson<T>(json, object : TypeToken<T>() {}.type)
