package io.github.bryansant.klique.style

import io.github.bryansant.klique.spi.AnsiCode

private val ESC = 27.toChar()
private fun fg(n: Int) = "$ESC[${n}m"

enum class ColorCode(private val seq: String) : AnsiCode {
    BLACK(fg(30)),
    RED(fg(31)),
    GREEN(fg(32)),
    YELLOW(fg(33)),
    BLUE(fg(34)),
    MAGENTA(fg(35)),
    CYAN(fg(36)),
    WHITE(fg(37)),
    BRIGHT_BLACK(fg(90)),
    BRIGHT_RED(fg(91)),
    BRIGHT_GREEN(fg(92)),
    BRIGHT_YELLOW(fg(93)),
    BRIGHT_BLUE(fg(94)),
    BRIGHT_MAGENTA(fg(95)),
    BRIGHT_CYAN(fg(96)),
    BRIGHT_WHITE(fg(97));

    override fun ansiSequence(): String = seq
    override fun toString(): String = seq
}
