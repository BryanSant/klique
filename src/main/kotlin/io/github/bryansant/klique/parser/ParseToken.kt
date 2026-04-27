package io.github.bryansant.klique.parser

import io.github.bryansant.klique.spi.AnsiCode

internal class ParseToken(
    val start: Int,
    var end: Int = start,
    val styles: List<AnsiCode>,
)
