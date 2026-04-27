package io.github.bryansant.klique.config

import io.github.bryansant.klique.ProgressBarPreset
import io.github.bryansant.klique.parser.MarkupParser

data class ProgressBarConfig(
    val length: Int = 40,
    val glyphs: String = "█░",
    val format: String = ":bar :percent% [:elapsed/:remaining]",
    val parser: MarkupParser = MarkupParser.DEFAULT,
    val styles: List<ProgressBarPredicate> = emptyList(),
    val easing: EasingConfig = EasingConfig.DEFAULT,
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
    private val styleList = mutableListOf<ProgressBarPredicate>()

    fun styleWhen(condition: (Int) -> Boolean, format: String) {
        styleList.add(ProgressBarPredicate(condition, format))
    }

    fun styleRange(min: Int, max: Int, format: String) {
        styleWhen({ p -> p in min..max }, format)
    }

    fun build() = ProgressBarConfig(length, glyphs, format, parser, styleList.toList(), easing)
}

fun progressBarConfig(block: ProgressBarConfigBuilder.() -> Unit): ProgressBarConfig =
    ProgressBarConfigBuilder().apply(block).build()
