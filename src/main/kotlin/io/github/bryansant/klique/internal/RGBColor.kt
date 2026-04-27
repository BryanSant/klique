package io.github.bryansant.klique.internal

import io.github.bryansant.klique.spi.RGBAnsiCode

internal class RGBColor(
    private val r: Int,
    private val g: Int,
    private val b: Int,
    private val background: Boolean = false,
) : RGBAnsiCode {

    init {
        require(r in 0..255) { "Red must be 0-255" }
        require(g in 0..255) { "Green must be 0-255" }
        require(b in 0..255) { "Blue must be 0-255" }
    }

    private val seq: String = run {
        val type = if (background) 48 else 38
        val esc = 27.toChar()
        "$esc[${type};2;${r};${g};${b}m"
    }

    override fun ansiSequence(): String = seq
    override fun red(): Int = r
    override fun green(): Int = g
    override fun blue(): Int = b
    override fun isBackground(): Boolean = background
}
