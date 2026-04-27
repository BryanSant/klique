package io.github.bryansant.klique.parser

internal data class ParseResult(val tokens: List<ParseToken>) {
    val isPresent: Boolean get() = tokens.isNotEmpty()
}
