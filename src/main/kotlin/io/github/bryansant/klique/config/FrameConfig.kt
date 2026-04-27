package io.github.bryansant.klique.config

import io.github.bryansant.klique.BoxType
import io.github.bryansant.klique.FrameAlign
import io.github.bryansant.klique.parser.MarkupParser
import io.github.bryansant.klique.spi.AnsiCode

data class FrameConfig(
    val padding: Int = 2,
    val frameAlign: FrameAlign = FrameAlign.CENTER,
    val borderColor: Array<AnsiCode> = emptyArray(),
    val parser: MarkupParser = MarkupParser.DEFAULT,
    val boxType: BoxType = BoxType.ROUNDED,
) {
    companion object {
        val DEFAULT = FrameConfig()
    }

    override fun equals(other: Any?): Boolean = other is FrameConfig &&
        padding == other.padding && frameAlign == other.frameAlign &&
        borderColor.contentEquals(other.borderColor) && parser == other.parser && boxType == other.boxType

    override fun hashCode(): Int = arrayOf(padding, frameAlign, borderColor.contentHashCode(), parser, boxType).contentHashCode()
}

class FrameConfigBuilder {
    var padding: Int = 2
    var frameAlign: FrameAlign = FrameAlign.CENTER
    var borderColor: Array<AnsiCode> = emptyArray()
    var parser: MarkupParser = MarkupParser.DEFAULT
    var boxType: BoxType = BoxType.ROUNDED

    fun borderColor(vararg codes: AnsiCode) { borderColor = arrayOf(*codes) }
    fun borderColor(colorName: String) { borderColor = ParserUtils.getAnsiCodes(colorName, parser) }

    fun build() = FrameConfig(padding, frameAlign, borderColor, parser, boxType)
}

fun frameConfig(block: FrameConfigBuilder.() -> Unit): FrameConfig = FrameConfigBuilder().apply(block).build()
