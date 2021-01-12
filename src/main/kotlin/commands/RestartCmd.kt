package commands

import command.Command
import data.Permission
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.event.message.MessageCreateEvent
import util.ChatManager
import java.io.InputStreamReader

import java.io.BufferedReader
import kotlin.concurrent.thread


object RestartCmd : Command("restart", Permission.SERVER_OWNER) {
    override suspend fun exec(event: MessageCreateEvent, args: List<String>) {
        event.message.delete()
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

                        exec("/opt/DiscordBot/start")

                        if (procId != null) {
                            exec("screen -XS $procId quit")
                        }

                    } catch (t: Throwable) {
                        t.printStackTrace()
                    }
                }
            }
        } else {
            ChatManager.sendError("Bot isn't running on Linux!", event.message.channel)
        }
    }
}