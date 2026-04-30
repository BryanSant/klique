package io.github.bryansant.klique

import io.github.bryansant.klique.internal.utils.AnsiDetector
import io.github.bryansant.klique.spi.BEL
import io.github.bryansant.klique.spi.ESC
import java.io.PrintStream

/**
 * Sets the terminal window/tab title via OSC 2.
 *
 * Compatible with xterm, Ghostty, Ptyxis, and most modern terminal emulators.
 * The title persists until changed again; the shell typically restores its own
 * title (via PROMPT_COMMAND or precmd) when the process exits.
 *
 * ```kotlin
 * setSystemTitle("Build — my-project")
 * ```
 */
fun setSystemTitle(title: String) {
    if (!AnsiDetector.ansiEnabled()) return
    print("${ESC}]2;$title$BEL")
    System.out.flush()
}

fun sendSystemNotification(title: String, message: String) {
    if (!AnsiDetector.ansiEnabled()) return
    // OSC 9: iTerm2 / Windows Terminal / ConEmu
    // Usually just takes the message
    print("${ESC}]9;$title: $message$BEL")

    // OSC 777: rxvt-unicode / VTE terminals
    print("${ESC}]777;notify;$title;$message$BEL")

    // OSC 99: Kitty / Ghostty
    // Simple version (no chunks)
    print("${ESC}]99;;$title: $message$BEL")
}

/**
 * Emits an OSC 9;4 terminal progress sequence to [stream].
 *
 * States: 0 = hide, 1 = normal (0–100 value), 3 = indeterminate.
 * No-op when ANSI is disabled.
 */
internal fun emitOsc94(stream: PrintStream, state: Int, value: Int = 0) {
    if (!AnsiDetector.ansiEnabled()) return
    stream.print("${ESC}]9;4;$state;$value$BEL")
    stream.flush()
}
