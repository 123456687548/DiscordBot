package util

import java.text.SimpleDateFormat
import java.util.*

object Time {
    fun getTime(): String {
        val date = Date()
        val df = SimpleDateFormat("[ HH:mm:ss - dd.MM ]")
        return df.format(date.time)
    }
}