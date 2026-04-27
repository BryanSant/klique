package io.github.bryansant.klique.internal

internal object Constants {
    const val NEWLINE = "\n"
    const val EMPTY = ""
    const val BLANK = " "
    const val ZERO = 0
    val ESC = 27.toChar()
    const val ANSI_END = 'm'
    const val LBRACKET = '['

    // Environment / system property keys used by AnsiDetector
    const val TERM = "TERM"
    const val PLAIN = "plain"
    const val DUMB = "dumb"
    const val NO_COLOR = "NO_COLOR"
    const val CLIQUE_COLOR = "clique.color"
    const val ALWAYS = "always"
    const val NEVER = "never"
    const val OS_NAME = "os.name"
    const val WIN = "win"
    const val CLI_COLOR_FORCE = "CLICOLOR_FORCE"
    const val COLOR_TERM = "COLORTERM"
    const val WT_SESSION = "WT_SESSION"
    const val FORCE_COLOR = "FORCE_COLOR"
}
