package io.github.bryansant.klique.config

import io.github.bryansant.klique.parser.MarkupParser
import io.github.bryansant.klique.spi.AnsiCode
import io.github.bryansant.klique.spi.RGBAnsiCode

data class SpinnerConfig(
    val frames: List<String> = listOf("⠋", "⠙", "⠹", "⠸", "⠼", "⠴", "⠦", "⠧", "⠇", "⠏"),
    val frameDelayMs: Long = 80L,
    val color: Array<AnsiCode> = emptyArray(),
    val parser: MarkupParser = MarkupParser.DEFAULT,
    val gradientFrom: RGBAnsiCode? = null,
    val gradientTo: RGBAnsiCode? = null,
) {
    companion object {
        val DEFAULT = SpinnerConfig()
        val STAR = SpinnerConfig(frames = listOf("·", "✶", "✸", "✹", "✺", "✹", "✷", "·"))
    }

    override fun equals(other: Any?): Boolean = other is SpinnerConfig &&
        frames == other.frames && frameDelayMs == other.frameDelayMs &&
        color.contentEquals(other.color) && parser == other.parser &&
        gradientFrom == other.gradientFrom && gradientTo == other.gradientTo

    override fun hashCode(): Int = arrayOf(frames, frameDelayMs, color.contentHashCode(), parser, gradientFrom, gradientTo).contentHashCode()
}

class SpinnerConfigBuilder {
    var frames: List<String> = listOf("⠋", "⠙", "⠹", "⠸", "⠼", "⠴", "⠦", "⠧", "⠇", "⠏")
    var frameDelayMs: Long = 80L
    var color: Array<AnsiCode> = emptyArray()
    var parser: MarkupParser = MarkupParser.DEFAULT
    var gradientFrom: RGBAnsiCode? = null
    var gradientTo: RGBAnsiCode? = null

    fun frames(vararg f: String) { frames = f.toList() }
    fun color(vararg codes: AnsiCode) { color = arrayOf(*codes) }
    fun color(colorName: String) { color = ParserUtils.getAnsiCodes(colorName, parser) }
    fun gradient(from: RGBAnsiCode, to: RGBAnsiCode) { gradientFrom = from; gradientTo = to }

    fun build() = SpinnerConfig(frames, frameDelayMs, color, parser, gradientFrom, gradientTo)
}

fun spinnerConfig(block: SpinnerConfigBuilder.() -> Unit): SpinnerConfig =
    SpinnerConfigBuilder().apply(block).build()
