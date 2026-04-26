package io.github.klique

import io.github.kusoroadeolu.clique.Clique as JClique
import io.github.kusoroadeolu.clique.configuration.ParserConfiguration
import io.github.kusoroadeolu.clique.parser.MarkupParser
import io.github.kusoroadeolu.clique.spi.AnsiCode
import io.github.kusoroadeolu.clique.style.StyleBuilder

// ── Markup parser extensions ──────────────────────────────────────────────────

/**
 * Parses Clique markup tags (e.g. `[red, bold]text[/]`) in this string and
 * returns the ANSI-styled result.
 *
 * ```kotlin
 * "[red, bold]Error:[/] Something went wrong".parseMarkup()
 * ```
 */
fun String.parseMarkup(config: ParserConfiguration? = null): String =
    if (config != null) JClique.parser(config).parse(this) else JClique.parser().parse(this)

/**
 * Parses and prints this marked-up string to stdout.
 */
fun String.printMarkup(config: ParserConfiguration? = null) {
    val parser = if (config != null) JClique.parser(config) else JClique.parser()
    parser.print(this)
}

// ── StyleBuilder DSL ──────────────────────────────────────────────────────────

/**
 * Appends styled [text] using the given [codes]. The codes remain active for
 * subsequent appends until explicitly reset.
 */
fun StyleBuilder.append(text: String, vararg codes: AnsiCode): StyleBuilder = append(text, *codes)

/**
 * Appends styled [text] and then emits a reset, so the next append starts clean.
 */
fun StyleBuilder.appendReset(text: String, vararg codes: AnsiCode): StyleBuilder =
    appendAndReset(text, *codes)

/** Renders this [StyleBuilder] as a string and prints it to stdout. */
fun StyleBuilder.printStyled() = print()
