package util

import com.google.gson.GsonBuilder
import data.github.request.latestRelease.LatestRelease
import data.version.Version
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

object Updater {
    const val CURRENT_VERSION_STRING = "1.2"
    private val CURRENT_VERSION = Version.getVersionFromString(CURRENT_VERSION_STRING)
    private const val LATEST_RELEASE_URL = "https://api.github.com/repos/123456687548/DiscordBot/releases/latest"

    private const val fileName = "DiscordBot.zip"

    private var updateFileURL: String? = null

    @ExperimentalStdlibApi
    fun checkForUpdate() {
        if (!isUpdateAvailable()) return
        if (!downloadNewRelease()) return
        unzipNewRelease()
        Main.restart()
    }

    private fun unzipNewRelease() {
        val os = System.getProperty("os.name")
        if (os.contains("Linux")) {
            try {
                val process = Runtime.getRuntime().exec("unzip -o $fileName")
                process.waitFor()
                val buf = BufferedReader(InputStreamReader(process.inputStream))
                var line: String

                while (buf.readLine().also { line = it } != null) {
                    println(line)
                }
            } catch (e: Exception) {

            }
        }
    }

    fun downloadNewRelease(): Boolean {
        try {
            BufferedInputStream(URL(updateFileURL).openStream()).use { `in` ->
                FileOutputStream(fileName).use { fileOutputStream ->
                    val dataBuffer = ByteArray(1024)
                    var bytesRead: Int
                    while (`in`.read(dataBuffer, 0, 1024).also { bytesRead = it } != -1) {
                        fileOutputStream.write(dataBuffer, 0, bytesRead)
                    }
                }
            }
        } catch (e: IOException) {
            return false
        }
        return true
    }


    fun isUpdateAvailable(): Boolean {
        try {
            val url = URL(LATEST_RELEASE_URL)
            val con = url.openConnection() as HttpURLConnection
            con.requestMethod = "GET"
            val bufferedReader = BufferedReader(InputStreamReader(con.inputStream))
            var inputLine: String?
            val content = StringBuilder()
            while (bufferedReader.readLine().also { inputLine = it } != null) {
                content.append(inputLine)
            }
            bufferedReader.close()
            con.disconnect()
            val gson = GsonBuilder().setPrettyPrinting().create()
            val latestRelease = gson.fromJson(content.toString(), LatestRelease::class.java)
            if (latestRelease.assets?.isEmpty() == true) return false
            updateFileURL = latestRelease.assets?.get(0)?.browser_download_url
            val latestVersionString = latestRelease.name ?: return false
            val latestVersion = Version.getVersionFromString(latestVersionString)
            return CURRENT_VERSION.isOlder(latestVersion)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return false
    }
}
