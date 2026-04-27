package io.github.bryansant.klique.style

import io.github.bryansant.klique.internal.utils.StringUtils
import io.github.bryansant.klique.spi.AnsiCode
import java.io.PrintStream

@Suppress("unused")
class StyleBuilder {
    private val sb = StringBuilder()

    fun append(text: String, vararg ansiCodes: AnsiCode): StyleBuilder {
        StringUtils.style(text, sb, *ansiCodes)
        return this
    }

    fun appendAndReset(text: String, vararg ansiCodes: AnsiCode): StyleBuilder {
        append(text, *ansiCodes)
        sb.append(StyleCode.RESET.ansiSequence())
        return this
    }

    override fun toString(): String = sb.toString()

    fun print(stream: PrintStream) = stream.println(this)
    fun print() = print(System.out)

    override fun equals(other: Any?): Boolean =
        other is StyleBuilder && sb.compareTo(other.sb) == 0

    override fun hashCode(): Int = sb.hashCode()
}
