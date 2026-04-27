package io.github.bryansant.klique.config

import io.github.bryansant.klique.BoxType
import io.github.bryansant.klique.TextAlign
import io.github.bryansant.klique.parser.MarkupParser
import io.github.bryansant.klique.spi.AnsiCode

data class BoxConfig(
    val padding: Int = 2,
    val textAlign: TextAlign = TextAlign.CENTER,
    val borderColor: Array<AnsiCode> = emptyArray(),
    val parser: MarkupParser = MarkupParser.DEFAULT,
    val boxType: BoxType = BoxType.ROUNDED,
) {
    companion object {
        val DEFAULT = BoxConfig()
    }

    override fun equals(other: Any?): Boolean = other is BoxConfig &&
        padding == other.padding && textAlign == other.textAlign &&
        borderColor.contentEquals(other.borderColor) && parser == other.parser && boxType == other.boxType

    override fun hashCode(): Int = arrayOf(padding, textAlign, borderColor.contentHashCode(), parser, boxType).contentHashCode()
}

class BoxConfigBuilder {
    var padding: Int = 2
    var textAlign: TextAlign = TextAlign.CENTER
    var borderColor: Array<AnsiCode> = emptyArray()
    var parser: MarkupParser = MarkupParser.DEFAULT
    var boxType: BoxType = BoxType.ROUNDED

    fun borderColor(vararg codes: AnsiCode) { borderColor = arrayOf(*codes) }
    fun borderColor(colorName: String) { borderColor = ParserUtils.getAnsiCodes(colorName, parser) }

    fun build() = BoxConfig(padding, textAlign, borderColor, parser, boxType)
}

fun boxConfig(block: BoxConfigBuilder.() -> Unit): BoxConfig = BoxConfigBuilder().apply(block).build()
