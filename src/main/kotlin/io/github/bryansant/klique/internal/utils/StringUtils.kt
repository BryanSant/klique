package io.github.bryansant.klique.internal.utils

import io.github.bryansant.klique.spi.ESC
import io.github.bryansant.klique.internal.Cell
import io.github.bryansant.klique.parser.MarkupParser
import io.github.bryansant.klique.spi.AnsiCode
import io.github.bryansant.klique.style.StyleCode

internal object StringUtils {

    fun parseToCell(text: String, parser: MarkupParser): Cell =
        Cell(parser.getOriginalString(text), parser.parse(text))

    fun parse(text: String, parser: MarkupParser): String = parser.parse(text)

    fun stripAnsi(styled: String): String {
        var i = 0
        var inEscape = false
        val clean = StringBuilder()
        while (i < styled.length) {
            val c = styled[i]
            when {
                c == ESC[0] && nextCharEquals(styled, i + 1, '[') -> inEscape = true
                inEscape && c == 'm' -> inEscape = false
                !inEscape -> clean.append(c)
            }
            i++
        }
        return clean.toString()
    }

    fun nextCharEquals(s: String, pos: Int, ch: Char): Boolean =
        pos < s.length && s[pos] == ch

    fun format(sb: StringBuilder, text: String, vararg ansiCodes: AnsiCode): String {
        style(text, sb, *ansiCodes)
        val result = sb.toString()
        sb.setLength(0)
        return result
    }

    fun formatAndReset(sb: StringBuilder, text: String, vararg ansiCodes: AnsiCode?): String {
        styleNullable(text, sb, *ansiCodes)
        sb.append(StyleCode.RESET.ansiSequence())
        val result = sb.toString()
        sb.setLength(0)
        return result
    }

    fun style(text: String, sb: StringBuilder, vararg ansiCodes: AnsiCode): StringBuilder {
        if (AnsiDetector.ansiEnabled()) {
            for (code in ansiCodes) sb.append(code.ansiSequence())
        }
        return sb.append(text)
    }

    private fun styleNullable(text: String, sb: StringBuilder, vararg ansiCodes: AnsiCode?): StringBuilder {
        if (AnsiDetector.ansiEnabled()) {
            for (code in ansiCodes) if (code != null) sb.append(code.ansiSequence())
        }
        return sb.append(text)
    }
}
