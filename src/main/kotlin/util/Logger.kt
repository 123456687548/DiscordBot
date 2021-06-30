package util

import org.apache.commons.io.output.TeeOutputStream
import java.io.FileOutputStream
import java.io.PrintStream

fun splitOutStream() {
    val fos = FileOutputStream("log.txt")
    Runtime.getRuntime().addShutdownHook(Thread({
        try {
            fos.flush()
        } catch (t: Throwable) {
            // Ignore
        }
    }, "Shutdown hook Thread flushing log.txt"))
    val myOut = TeeOutputStream(System.out, fos)
    val ps = PrintStream(myOut, true)
    System.setOut(ps)
}