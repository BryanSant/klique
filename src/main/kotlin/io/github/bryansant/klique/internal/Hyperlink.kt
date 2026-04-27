package io.github.bryansant.klique.internal

internal class Hyperlink(private val url: String) {
    // OSC 8 hyperlink escape: ESC ] 8 ; ; url ESC \ text ESC ] 8 ; ; ESC \
    private val esc = 27.toChar()
    private val bel = '\\'

    fun apply(text: String): String {
        val open = "${esc}]8;;${url}${esc}${bel}"
        val close = "${esc}]8;;${esc}${bel}"
        return "$open$text$close"
    }
}
