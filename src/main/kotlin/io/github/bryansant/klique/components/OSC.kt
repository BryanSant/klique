package io.github.bryansant.klique.components

import io.github.bryansant.klique.internal.utils.AnsiDetector
import io.github.bryansant.klique.spi.ESC
import io.github.bryansant.klique.spi.ST
import java.io.PrintStream
import java.util.Base64

object OSC {

    enum class ProgressState(internal val code: Int) {
        INACTIVE(0), IN_PROGRESS(1), ERROR(2), INDETERMINATE(3), PAUSED(4)
    }

    fun setSystemTitle(title: String) {
        if (!AnsiDetector.ansiEnabled()) return
        print("${ESC}]2;$title$ST")
        System.out.flush()
    }

    fun sendSystemNotification(subject: String, message: String) {
        if (!AnsiDetector.ansiEnabled()) return
        print("${ESC}]9;$subject: $message$ST")
        print("${ESC}]777;notify;$subject;$message$ST")
        print("${ESC}]99;;$subject: $message$ST")
        System.out.flush()
    }

    fun copyToClipboard(text: String) {
        if (!AnsiDetector.ansiEnabled()) return
        val encoded = Base64.getEncoder().encodeToString(text.toByteArray())
        print("${ESC}]52;c;$encoded$ST")
        System.out.flush()
    }

    internal fun emitOsc94(stream: PrintStream, state: ProgressState, value: Int = 0) {
        if (!AnsiDetector.ansiEnabled()) return
        stream.print("${ESC}]9;4;${state.code};$value$ST")
        stream.flush()
    }
}
