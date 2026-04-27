package io.github.klique

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
    print("]2;$title")
    System.out.flush()
}
