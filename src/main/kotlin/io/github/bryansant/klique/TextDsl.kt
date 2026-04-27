package io.github.bryansant.klique

import io.github.bryansant.klique.parser.MarkupParser
import io.github.bryansant.klique.parser.ParserConfig
import io.github.bryansant.klique.spi.AnsiCode
import io.github.bryansant.klique.style.StyleBuilder

/** Parses Klique markup tags (e.g. `[red, bold]text[/]`) in this string and returns the ANSI-styled result. */
fun String.parseMarkup(config: ParserConfig? = null): String =
    if (config != null) MarkupParser(config).parse(this) else MarkupParser.DEFAULT.parse(this)

/** Parses and prints this marked-up string to stdout. */
fun String.printMarkup(config: ParserConfig? = null) {
    val result = parseMarkup(config)
    println(result)
}

/** Appends styled [text] using the given [codes]. Styles remain active until reset. */
fun StyleBuilder.append(text: String, vararg codes: AnsiCode): StyleBuilder = append(text, *codes)

/** Appends styled [text] and then emits a reset so the next append starts clean. */
fun StyleBuilder.appendReset(text: String, vararg codes: AnsiCode): StyleBuilder =
    appendAndReset(text, *codes)

/** Renders this [StyleBuilder] as a string and prints it to stdout. */
fun StyleBuilder.printStyled() = print()
