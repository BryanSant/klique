package io.github.bryansant.klique.config

import io.github.bryansant.klique.parser.MarkupParser
import io.github.bryansant.klique.spi.AnsiCode

data class SpinnerConfig(
    val frames: List<String> = listOf("⠋", "⠙", "⠹", "⠸", "⠼", "⠴", "⠦", "⠧", "⠇", "⠏"),
    val frameDelayMs: Long = 80L,
    val color: Array<AnsiCode> = emptyArray(),
    val parser: MarkupParser = MarkupParser.DEFAULT,
) {
    companion object {
        val DEFAULT = SpinnerConfig()
        val STAR = SpinnerConfig(frames = listOf("·", "✶", "✸", "✹", "✺", "✹", "✷", "·"))
    }

    override fun equals(other: Any?): Boolean = other is SpinnerConfig &&
        frames == other.frames && frameDelayMs == other.frameDelayMs &&
        color.contentEquals(other.color) && parser == other.parser

    override fun hashCode(): Int = arrayOf(frames, frameDelayMs, color.contentHashCode(), parser).contentHashCode()
}

class SpinnerConfigBuilder {
    var frames: List<String> = listOf("⠋", "⠙", "⠹", "⠸", "⠼", "⠴", "⠦", "⠧", "⠇", "⠏")
    var frameDelayMs: Long = 80L
    var color: Array<AnsiCode> = emptyArray()
    var parser: MarkupParser = MarkupParser.DEFAULT

    fun frames(vararg f: String) { frames = f.toList() }
    fun color(vararg codes: AnsiCode) { color = arrayOf(*codes) }
    fun color(colorName: String) { color = ParserUtils.getAnsiCodes(colorName, parser) }

    fun build() = SpinnerConfig(frames, frameDelayMs, color, parser)
}

fun spinnerConfig(block: SpinnerConfigBuilder.() -> Unit): SpinnerConfig =
    SpinnerConfigBuilder().apply(block).build()
