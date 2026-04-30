package io.github.bryansant.klique.style

import io.github.bryansant.klique.spi.ESC
import io.github.bryansant.klique.spi.AnsiCode

private fun ansi(code: String) = "$ESC[$code"

enum class StyleCode(private val seq: String) : AnsiCode {
    RESET(ansi("0m")),
    BOLD(ansi("1m")),
    DIM(ansi("2m")),
    ITALIC(ansi("3m")),
    UNDERLINE(ansi("4m")),
    REVERSE_VIDEO(ansi("7m")),
    INVISIBLE_TEXT(ansi("8m")),
    STRIKETHROUGH(ansi("9m")),
    DOUBLE_UNDERLINE(ansi("21m"));

    override fun ansiSequence(): String = seq
    override fun toString(): String = seq
}
