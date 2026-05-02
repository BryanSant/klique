package io.github.bryansant.klique.parser

import io.github.bryansant.klique.internal.RGBColor
import io.github.bryansant.klique.spi.AnsiCode
import io.github.bryansant.klique.spi.RGBAnsiCode
import io.github.bryansant.klique.style.BackgroundCode
import io.github.bryansant.klique.style.ColorCode
import io.github.bryansant.klique.style.StyleCode
import java.util.concurrent.ConcurrentHashMap

internal object PredefinedStyleContext {

    val COLOR_CODES: StyleContext = StyleContext.from(
        mapOf(
            "black" to ColorCode.BLACK,
            "red" to ColorCode.RED,
            "green" to ColorCode.GREEN,
            "yellow" to ColorCode.YELLOW,
            "blue" to ColorCode.BLUE,
            "magenta" to ColorCode.MAGENTA,
            "cyan" to ColorCode.CYAN,
            "white" to ColorCode.WHITE,
            "*black" to ColorCode.BRIGHT_BLACK,
            "*red" to ColorCode.BRIGHT_RED,
            "*green" to ColorCode.BRIGHT_GREEN,
            "*yellow" to ColorCode.BRIGHT_YELLOW,
            "*blue" to ColorCode.BRIGHT_BLUE,
            "*magenta" to ColorCode.BRIGHT_MAGENTA,
            "*cyan" to ColorCode.BRIGHT_CYAN,
            "*white" to ColorCode.BRIGHT_WHITE,
        )
    )

    val BACKGROUND_CODES: StyleContext = StyleContext.from(
        mapOf(
            "bg_black" to BackgroundCode.BLACK,
            "bg_red" to BackgroundCode.RED,
            "bg_green" to BackgroundCode.GREEN,
            "bg_yellow" to BackgroundCode.YELLOW,
            "bg_blue" to BackgroundCode.BLUE,
            "bg_magenta" to BackgroundCode.MAGENTA,
            "bg_cyan" to BackgroundCode.CYAN,
            "bg_white" to BackgroundCode.WHITE,
            "*bg_black" to BackgroundCode.BRIGHT_BLACK,
            "*bg_red" to BackgroundCode.BRIGHT_RED,
            "*bg_green" to BackgroundCode.BRIGHT_GREEN,
            "*bg_yellow" to BackgroundCode.BRIGHT_YELLOW,
            "*bg_blue" to BackgroundCode.BRIGHT_BLUE,
            "*bg_magenta" to BackgroundCode.BRIGHT_MAGENTA,
            "*bg_cyan" to BackgroundCode.BRIGHT_CYAN,
            "*bg_white" to BackgroundCode.BRIGHT_WHITE,
        )
    )

    val STYLE_CODES: StyleContext = StyleContext.from(
        mapOf(
            "bold" to StyleCode.BOLD,
            "dim" to StyleCode.DIM,
            "italic" to StyleCode.ITALIC,
            "ul" to StyleCode.UNDERLINE,
            "rv" to StyleCode.REVERSE_VIDEO,
            "inv" to StyleCode.INVISIBLE_TEXT,
            "/" to StyleCode.RESET,
            "dbl_ul" to StyleCode.DOUBLE_UNDERLINE,
            "strike" to StyleCode.STRIKETHROUGH,
        )
    )

    val CUSTOM_STYLE_CODES: ConcurrentHashMap<String, AnsiCode> = ConcurrentHashMap()

    // Resolution order: local context → global custom → predefined → hex color
    fun get(name: String, ctx: StyleContext): AnsiCode? =
        ctx.get(name)
            ?: CUSTOM_STYLE_CODES[name]
            ?: COLOR_CODES.get(name)
            ?: BACKGROUND_CODES.get(name)
            ?: STYLE_CODES.get(name)
            ?: parseHexColor(name)

    fun getOrThrow(name: String, ctx: StyleContext): AnsiCode =
        get(name, ctx) ?: throw IllegalArgumentException("Unidentified style: '$name'")

    private fun parseHexColor(name: String): RGBAnsiCode? {
        val background = name.startsWith("bg_#")
        val spec = if (background) name.removePrefix("bg_") else name
        if (!spec.startsWith("#")) return null
        val hex = spec.removePrefix("#").lowercase()
        return when (hex.length) {
            3 -> {
                val r = "${hex[0]}${hex[0]}".toIntOrNull(16) ?: return null
                val g = "${hex[1]}${hex[1]}".toIntOrNull(16) ?: return null
                val b = "${hex[2]}${hex[2]}".toIntOrNull(16) ?: return null
                RGBColor(r, g, b, background)
            }
            6 -> {
                val r = hex.substring(0, 2).toIntOrNull(16) ?: return null
                val g = hex.substring(2, 4).toIntOrNull(16) ?: return null
                val b = hex.substring(4, 6).toIntOrNull(16) ?: return null
                RGBColor(r, g, b, background)
            }
            else -> null
        }
    }
}
