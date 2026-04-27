package io.github.bryansant.klique.internal

import io.github.bryansant.klique.spi.AnsiCode

internal class CompositeColor(codes: Collection<AnsiCode>) : AnsiCode {
    constructor(vararg codes: AnsiCode) : this(codes.toList())

    private val seq: String = codes.joinToString("") { it.ansiSequence() }

    override fun ansiSequence(): String = seq
}
