package io.github.bryansant.klique.style

import io.github.bryansant.klique.spi.ESC
import io.github.bryansant.klique.spi.AnsiCode

private fun bg(n: Int) = "$ESC[${n}m"

enum class BackgroundCode(private val seq: String) : AnsiCode {
    BLACK(bg(40)),
    RED(bg(41)),
    GREEN(bg(42)),
    YELLOW(bg(43)),
    BLUE(bg(44)),
    MAGENTA(bg(45)),
    CYAN(bg(46)),
    WHITE(bg(47)),
    BRIGHT_BLACK(bg(100)),
    BRIGHT_RED(bg(101)),
    BRIGHT_GREEN(bg(102)),
    BRIGHT_YELLOW(bg(103)),
    BRIGHT_BLUE(bg(104)),
    BRIGHT_MAGENTA(bg(105)),
    BRIGHT_CYAN(bg(106)),
    BRIGHT_WHITE(bg(107));

    override fun ansiSequence(): String = seq
    override fun toString(): String = seq
}
