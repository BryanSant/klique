package io.github.bryansant.klique.parser

import io.github.bryansant.klique.style.StyleBuilder

internal object StyleResolver {

    fun resolve(tokens: List<ParseToken>, string: String, autoReset: Boolean): String {
        if (tokens.isEmpty()) return string

        val sb = StyleBuilder()
        if (tokens.first().start != 0) {
            sb.append(string.substring(0, tokens.first().start))
        }

        for (i in tokens.indices) {
            val curr = tokens[i]
            val next = if (i != tokens.lastIndex) tokens[i + 1]
            else ParseToken(string.length, string.length, emptyList())

            val codes = curr.styles.toTypedArray()
            val text = string.substring(curr.end + 1, next.start)

            if (autoReset) sb.appendAndReset(text, *codes)
            else sb.append(text, *codes)
        }

        return sb.toString()
    }
}
