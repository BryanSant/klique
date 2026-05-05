package io.github.bryansant.klique.style

import io.github.bryansant.klique.internal.Gradient
import io.github.bryansant.klique.internal.Link
import io.github.bryansant.klique.internal.RGBColor
import io.github.bryansant.klique.parser.PredefinedStyleContext
import io.github.bryansant.klique.parser.StyleContext
import io.github.bryansant.klique.spi.AnsiCode
import io.github.bryansant.klique.spi.RGBAnsiCode

class Ink private constructor(
    private val codes: List<AnsiCode>,
    private val context: StyleContext,
    private val link: Link?,
    private val gradient: Gradient?,
) {
    constructor(context: StyleContext = StyleContext.NONE) : this(emptyList(), context, null, null)

    private fun with(code: AnsiCode): Ink = Ink(codes + code, context, link, gradient)
    private fun with(lnk: Link): Ink = Ink(ArrayList(codes), context, lnk, gradient)
    private fun with(grad: Gradient): Ink = Ink(ArrayList(codes), context, link, grad)

    fun on(value: String): String {
        if (codes.isEmpty() && gradient == null && link == null) return value
        val sb = StringBuilder()
        for (code in codes) sb.append(code.ansiSequence())
        var styled = sb.append(value).append(StyleCode.RESET).toString()
        if (gradient != null) styled = gradient.apply(styled)
        if (link != null) styled = link.apply(styled)
        return styled
    }

    fun on(value: Any): String = on(value.toString())

    fun of(style: String): Ink = with(PredefinedStyleContext.getOrThrow(style, context))
    fun of(code: AnsiCode): Ink = with(code)
    fun link(url: String): Ink = with(Link(url))
    fun gradient(from: RGBAnsiCode, to: RGBAnsiCode): Ink = with(Gradient(from, to))

    fun black(): Ink = with(ColorCode.BLACK)
    fun red(): Ink = with(ColorCode.RED)
    fun green(): Ink = with(ColorCode.GREEN)
    fun yellow(): Ink = with(ColorCode.YELLOW)
    fun blue(): Ink = with(ColorCode.BLUE)
    fun magenta(): Ink = with(ColorCode.MAGENTA)
    fun cyan(): Ink = with(ColorCode.CYAN)
    fun white(): Ink = with(ColorCode.WHITE)
    fun brightBlack(): Ink = with(ColorCode.BRIGHT_BLACK)
    fun brightRed(): Ink = with(ColorCode.BRIGHT_RED)
    fun brightGreen(): Ink = with(ColorCode.BRIGHT_GREEN)
    fun brightYellow(): Ink = with(ColorCode.BRIGHT_YELLOW)
    fun brightBlue(): Ink = with(ColorCode.BRIGHT_BLUE)
    fun brightMagenta(): Ink = with(ColorCode.BRIGHT_MAGENTA)
    fun brightCyan(): Ink = with(ColorCode.BRIGHT_CYAN)
    fun brightWhite(): Ink = with(ColorCode.BRIGHT_WHITE)
    fun rgb(r: Int, g: Int, b: Int): Ink = with(RGBColor(r, g, b))

    fun bgBlack(): Ink = with(BackgroundCode.BLACK)
    fun bgRed(): Ink = with(BackgroundCode.RED)
    fun bgGreen(): Ink = with(BackgroundCode.GREEN)
    fun bgYellow(): Ink = with(BackgroundCode.YELLOW)
    fun bgBlue(): Ink = with(BackgroundCode.BLUE)
    fun bgMagenta(): Ink = with(BackgroundCode.MAGENTA)
    fun bgCyan(): Ink = with(BackgroundCode.CYAN)
    fun bgWhite(): Ink = with(BackgroundCode.WHITE)
    fun bgRgb(r: Int, g: Int, b: Int): Ink = with(RGBColor(r, g, b, background = true))
    fun brightBgBlack(): Ink = with(BackgroundCode.BRIGHT_BLACK)
    fun brightBgRed(): Ink = with(BackgroundCode.BRIGHT_RED)
    fun brightBgGreen(): Ink = with(BackgroundCode.BRIGHT_GREEN)
    fun brightBgYellow(): Ink = with(BackgroundCode.BRIGHT_YELLOW)
    fun brightBgBlue(): Ink = with(BackgroundCode.BRIGHT_BLUE)
    fun brightBgMagenta(): Ink = with(BackgroundCode.BRIGHT_MAGENTA)
    fun brightBgCyan(): Ink = with(BackgroundCode.BRIGHT_CYAN)
    fun brightBgWhite(): Ink = with(BackgroundCode.BRIGHT_WHITE)

    fun bold(): Ink = with(StyleCode.BOLD)
    fun dim(): Ink = with(StyleCode.DIM)
    fun italic(): Ink = with(StyleCode.ITALIC)
    fun underline(): Ink = with(StyleCode.UNDERLINE)
    fun doubleUnderline(): Ink = with(StyleCode.DOUBLE_UNDERLINE)
    fun strikethrough(): Ink = with(StyleCode.STRIKETHROUGH)
    fun reverseVideo(): Ink = with(StyleCode.REVERSE_VIDEO)
    fun invisible(): Ink = with(StyleCode.INVISIBLE_TEXT)

    override fun equals(other: Any?): Boolean =
        other is Ink && codes == other.codes && context == other.context

    override fun hashCode(): Int = 31 * codes.hashCode() + context.hashCode()
}
