import features.DiscordBot
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread

@ExperimentalStdlibApi

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        runBlocking {
            DiscordBot.INSTANCE.initialize()
        }
    }
}