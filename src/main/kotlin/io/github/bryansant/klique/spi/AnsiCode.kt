package io.github.bryansant.klique.spi

const val ESC = "\u001B"
const val ST = "\\u001b\\u005c"

fun interface AnsiCode {
    fun ansiSequence(): String
}
