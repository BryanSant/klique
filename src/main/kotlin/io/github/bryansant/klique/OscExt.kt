package io.github.bryansant.klique

import io.github.bryansant.klique.internal.utils.AnsiDetector
import java.io.PrintStream

/**
 * Sets the terminal window/tab title via OSC 2.
 *
 * Compatible with xterm, Ghostty, Ptyxis, and most modern terminal emulators.
 * The title persists until changed again; the shell typically restores its own
 * title (via PROMPT_COMMAND or precmd) when the process exits.
 *
 * ```kotlin
 * setWindowTitle("Build — my-project")
 * ```
 */
fun setWindowTitle(title: String) {
    if (!AnsiDetector.ansiEnabled()) return
    print("${ESC_CHAR}]2;$title$BEL")
    System.out.flush()
}

private val BEL = 7.toChar()
private val ESC_CHAR = 27.toChar()

/**
 * Emits an OSC 9;4 terminal progress sequence to [stream].
 *
 * States: 0 = hide, 1 = normal (0–100 value), 3 = indeterminate.
 * No-op when ANSI is disabled.
 */
internal fun emitOsc94(stream: PrintStream, state: Int, value: Int = 0) {
    if (!AnsiDetector.ansiEnabled()) return
    stream.print("${ESC_CHAR}]9;4;$state;$value$BEL")
    stream.flush()
}
