package io.github.bryansant.klique.config

import io.github.bryansant.klique.parser.MarkupParser
import io.github.bryansant.klique.parser.PredefinedStyleContext
import io.github.bryansant.klique.parser.StyleContext
import io.github.bryansant.klique.spi.AnsiCode

internal object ParserUtils {
    private val NONE: Array<AnsiCode> = emptyArray()

    fun getAnsiCodes(string: String, parser: MarkupParser = MarkupParser.DEFAULT): Array<AnsiCode> {
        if (string.isBlank()) return NONE
        return string.split(parser.config.delimiter)
            .map { PredefinedStyleContext.getOrThrow(it.trim(), parser.config.styleContext) }
            .toTypedArray()
    }
}
