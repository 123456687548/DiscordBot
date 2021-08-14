import features.DiscordBot
import kotlinx.coroutines.runBlocking
import util.Updater.checkForUpdate
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.ArrayList
import kotlin.concurrent.thread

@ExperimentalStdlibApi
object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        checkForUpdate()

        runBlocking {
            DiscordBot.INSTANCE.initialize()
        }
    }

    fun closeEveryOtherScreen() {
        val os = System.getProperty("os.name")
        if (os.contains("Linux")) {
            try {
                val process = Runtime.getRuntime().exec("screen -list")
                process.waitFor()
                val buf = BufferedReader(InputStreamReader(process.inputStream))
                var line: String
                var first = true
                val procIds: MutableList<String> = ArrayList()
                while (buf.readLine().also { line = it } != null) {
                    if (line.contains("fritzScrape")) {
                        if (first) {
                            first = false
                        } else {
                            procIds.add(line.trim { it <= ' ' }.substring(0, line.indexOf('.') - 1))
                        }
                    }
                }
                if (!procIds.isEmpty()) {
                    for (procId in procIds) {
                        Runtime.getRuntime().exec(String.format("screen -XS %s quit", procId))
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    fun restart(): Boolean {
        if (System.getProperty("os.name").contains("Linux")) {
            thread(name = "restart thread", start = true) {
                Runtime.getRuntime().run {
                    try {
                        val pr: Process = exec("screen -list")
                        pr.waitFor()
                        val buf = BufferedReader(InputStreamReader(pr.inputStream))
                        var line: String? = ""
                        var procId: String? = null
                        while (buf.readLine().also { line = it } != null) {
                            if (line != null) {
                                if (line!!.contains("discord_bot")) {
                                    procId = line!!.trim().substring(0, line!!.indexOf('.') - 1)
                                }
                            }
                        }

                        exec("~/start")

                        if (procId != null) {
                            exec("screen -XS $procId quit")
                        }
                    } catch (t: Throwable) {
                        t.printStackTrace()
                    }
                }
            }
            return true
        } else {
            return false
        }
    }
}

