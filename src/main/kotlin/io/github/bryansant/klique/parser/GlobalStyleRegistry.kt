package io.github.bryansant.klique.parser

import io.github.bryansant.klique.spi.AnsiCode

object GlobalStyleRegistry {
    fun registerStyle(name: String, code: AnsiCode) {
        requireNotNull(name) { "Style name cannot be null" }
        requireNotNull(code) { "Ansi code cannot be null" }
        PredefinedStyleContext.CUSTOM_STYLE_CODES[name] = code
    }

    fun registerStyles(codes: Map<String, AnsiCode>) {
        requireNotNull(codes) { "Style map cannot be null" }
        PredefinedStyleContext.CUSTOM_STYLE_CODES.putAll(codes)
    }
}
