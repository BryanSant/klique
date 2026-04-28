package io.github.bryansant.klique.config

import io.github.bryansant.klique.ProgressBarPreset
import io.github.bryansant.klique.parser.MarkupParser
import io.github.bryansant.klique.spi.AnsiCode
import io.github.bryansant.klique.spi.RGBAnsiCode

data class ProgressBarConfig(
    val length: Int = 40,
    val glyphs: String = "█░",
    val format: String = ":bar :percent% [:elapsed/:remaining]",
    val parser: MarkupParser = MarkupParser.DEFAULT,
    val styles: List<ProgressBarPredicate> = emptyList(),
    val easing: EasingConfig = EasingConfig.DEFAULT,
    val barColor: Array<AnsiCode> = emptyArray(),
    val barGradientFrom: RGBAnsiCode? = null,
    val barGradientTo: RGBAnsiCode? = null,
) {
    init {
        require(glyphs.length >= 2) { "glyphs must contain at least 2 characters" }
    }

    companion object {
        val DEFAULT = ProgressBarConfig()

        fun fromPreset(preset: ProgressBarPreset): ProgressBarConfigBuilder = ProgressBarConfigBuilder().also {
            it.length = preset.length
            it.glyphs = preset.glyphs
            it.format = preset.format
        }
    }

    fun getFormatForPercent(percent: Int): String =
        styles.firstOrNull { it.matches(percent) }?.format ?: format

    override fun equals(other: Any?): Boolean = other is ProgressBarConfig &&
        length == other.length && glyphs == other.glyphs && format == other.format &&
        parser == other.parser && styles == other.styles && easing == other.easing &&
        barColor.contentEquals(other.barColor) &&
        barGradientFrom == other.barGradientFrom && barGradientTo == other.barGradientTo

    override fun hashCode(): Int = arrayOf(length, glyphs, format, parser, styles, easing, barColor.contentHashCode(), barGradientFrom, barGradientTo).contentHashCode()
}

data class ProgressBarPredicate(val predicate: (Int) -> Boolean, val format: String) {
    fun matches(value: Int): Boolean = predicate(value)
}

class ProgressBarConfigBuilder {
    var length: Int = 40
    var glyphs: String = "█░"
    var format: String = ":bar :percent% [:elapsed/:remaining]"
    var parser: MarkupParser = MarkupParser.DEFAULT
    var easing: EasingConfig = EasingConfig.DEFAULT
    var barColor: Array<AnsiCode> = emptyArray()
    var barGradientFrom: RGBAnsiCode? = null
    var barGradientTo: RGBAnsiCode? = null
    private val styleList = mutableListOf<ProgressBarPredicate>()

    fun barColor(vararg codes: AnsiCode) { barColor = arrayOf(*codes) }
    fun barColor(colorName: String) { barColor = ParserUtils.getAnsiCodes(colorName, parser) }
    fun barGradient(from: RGBAnsiCode, to: RGBAnsiCode) { barGradientFrom = from; barGradientTo = to }

    fun styleWhen(condition: (Int) -> Boolean, format: String) {
        styleList.add(ProgressBarPredicate(condition, format))
    }

    fun styleRange(min: Int, max: Int, format: String) {
        styleWhen({ p -> p in min..max }, format)
    }

    fun build() = ProgressBarConfig(length, glyphs, format, parser, styleList.toList(), easing, barColor, barGradientFrom, barGradientTo)
}

fun progressBarConfig(block: ProgressBarConfigBuilder.() -> Unit): ProgressBarConfig =
    ProgressBarConfigBuilder().apply(block).build()
