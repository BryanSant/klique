package io.github.bryansant.klique.internal

import io.github.bryansant.klique.internal.utils.AnsiDetector
import io.github.bryansant.klique.spi.AnsiCode
import io.github.bryansant.klique.spi.ESC

internal class LinkOpenCode(private val url: String) : AnsiCode {
    override fun ansiSequence(): String = "${ESC}]8;;${url}${ESC}\\"
}

internal object LinkCloseCode : AnsiCode {
    override fun ansiSequence(): String = "${ESC}]8;;${ESC}\\"
}

internal class Link(private val url: String) {
    // OSC 8 hyperlink: ESC ] 8 ; ; url ESC \ text ESC ] 8 ; ; ESC \
    fun apply(text: String): String {
        if (!AnsiDetector.ansiEnabled()) return text
        return "${LinkOpenCode(url).ansiSequence()}$text${LinkCloseCode.ansiSequence()}"
    }
}
