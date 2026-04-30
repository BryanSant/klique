package io.github.bryansant.klique.spi

const val ESC = "\u001B"
const val BEL = "\u0007"

fun interface AnsiCode {
    fun ansiSequence(): String
}
