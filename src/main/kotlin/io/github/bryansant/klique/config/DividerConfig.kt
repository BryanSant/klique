package io.github.bryansant.klique.config

import io.github.bryansant.klique.parser.MarkupParser
import io.github.bryansant.klique.spi.AnsiCode

data class DividerConfig(
    val dividerChar: Char = '─',
    val dividerColor: Array<AnsiCode> = emptyArray(),
    val parser: MarkupParser = MarkupParser.DEFAULT,
) {
    companion object {
        val DEFAULT = DividerConfig()
    }

    override fun equals(other: Any?): Boolean = other is DividerConfig &&
        dividerChar == other.dividerChar && dividerColor.contentEquals(other.dividerColor) && parser == other.parser

    override fun hashCode(): Int = arrayOf(dividerChar, dividerColor.contentHashCode(), parser).contentHashCode()
}

class DividerConfigBuilder {
    var dividerChar: Char = '─'
    var dividerColor: Array<AnsiCode> = emptyArray()
    var parser: MarkupParser = MarkupParser.DEFAULT

    fun dividerColor(vararg codes: AnsiCode) { dividerColor = arrayOf(*codes) }
    fun dividerColor(colorName: String) { dividerColor = ParserUtils.getAnsiCodes(colorName, parser) }

    fun build() = DividerConfig(dividerChar, dividerColor, parser)
}

fun dividerConfig(block: DividerConfigBuilder.() -> Unit): DividerConfig =
    DividerConfigBuilder().apply(block).build()
