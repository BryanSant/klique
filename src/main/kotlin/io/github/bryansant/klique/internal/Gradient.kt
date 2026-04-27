package io.github.bryansant.klique.internal

import io.github.bryansant.klique.internal.utils.StringUtils
import io.github.bryansant.klique.spi.RGBAnsiCode
import io.github.bryansant.klique.style.StyleCode

internal class Gradient(private val from: RGBAnsiCode, private val to: RGBAnsiCode) {

    fun apply(text: String): String {
        var colorIdx = 0
        var i = 0
        var inEscape = false
        val sb = StringBuilder()

        while (i < text.length) {
            val ch = text[i]
            when {
                ch == Constants.ESC && StringUtils.nextCharEquals(text, i + 1, Constants.LBRACKET) -> {
                    inEscape = true
                    sb.append(ch)
                }
                inEscape -> {
                    sb.append(ch)
                    if (ch == Constants.ANSI_END) inEscape = false
                }
                else -> {
                    val t = colorIdx.toDouble() / maxOf(text.length - 1, 1).toDouble()
                    val r = (from.red() + t * (to.red() - from.red())).toInt()
                    val g = (from.green() + t * (to.green() - from.green())).toInt()
                    val b = (from.blue() + t * (to.blue() - from.blue())).toInt()
                    sb.append(RGBColor(r, g, b, false).ansiSequence()).append(ch)
                    colorIdx++
                }
            }
            i++
        }

        return sb.append(StyleCode.RESET).toString()
    }
}
