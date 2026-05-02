package io.github.bryansant.klique.spi

const val ESC = "\u001B"
const val ST = "${ESC}\\"

fun interface AnsiCode {
    fun ansiSequence(): String
}
