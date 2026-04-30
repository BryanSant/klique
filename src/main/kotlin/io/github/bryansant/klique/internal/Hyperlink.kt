package io.github.bryansant.klique.internal

import io.github.bryansant.klique.spi.ESC

internal class Hyperlink(private val url: String) {
    // OSC 8 hyperlink escape: ESC ] 8 ; ; url ESC \ text ESC ] 8 ; ; ESC \
    fun apply(text: String): String {
        val open = "${ESC}]8;;${url}${ESC}\\"
        val close = "${ESC}]8;;${ESC}\\"
        return "$open$text$close"
    }
}
