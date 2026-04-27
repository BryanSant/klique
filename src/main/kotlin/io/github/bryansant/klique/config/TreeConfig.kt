package io.github.bryansant.klique.config

import io.github.bryansant.klique.parser.MarkupParser
import io.github.bryansant.klique.spi.AnsiCode

data class TreeConfig(
    val connectorColor: Array<AnsiCode> = emptyArray(),
    val parser: MarkupParser = MarkupParser.DEFAULT,
) {
    companion object {
        val DEFAULT = TreeConfig()
    }

    override fun equals(other: Any?): Boolean = other is TreeConfig &&
        connectorColor.contentEquals(other.connectorColor) && parser == other.parser

    override fun hashCode(): Int = arrayOf(connectorColor.contentHashCode(), parser).contentHashCode()
}

class TreeConfigBuilder {
    var connectorColor: Array<AnsiCode> = emptyArray()
    var parser: MarkupParser = MarkupParser.DEFAULT

    fun connectorColor(vararg codes: AnsiCode) { connectorColor = arrayOf(*codes) }
    fun connectorColor(colorName: String) { connectorColor = ParserUtils.getAnsiCodes(colorName, parser) }

    fun build() = TreeConfig(connectorColor, parser)
}

fun treeConfig(block: TreeConfigBuilder.() -> Unit): TreeConfig = TreeConfigBuilder().apply(block).build()
