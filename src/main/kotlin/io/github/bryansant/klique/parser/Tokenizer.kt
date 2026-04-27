package io.github.bryansant.klique.parser

import io.github.bryansant.klique.internal.Constants
import io.github.bryansant.klique.spi.AnsiCode

internal object Tokenizer {

    private const val FORM_START = '['
    private const val FORM_CLOSE = ']'
    private const val ESCAPE_SEQUENCE = '\\'

    fun tokenize(
        input: String?,
        delimiter: String,
        strictParsing: Boolean,
        context: StyleContext = StyleContext.NONE,
    ): ParseResult {
        if (input.isNullOrEmpty()) return ParseResult(emptyList())

        val tokens = mutableListOf<ParseToken>()
        val delimiterPattern = Regex.fromLiteral(delimiter)
        val len = input.length
        var idx = 0
        var fsDepth = 0
        var fcDepth = 0

        for (i in 0 until len) {
            val c = input[i]
            if (c == FORM_START &&
                charNotEquals(input, i - 1, Constants.ESC) &&
                charNotEquals(input, i - 1, ESCAPE_SEQUENCE)
            ) {
                fcDepth = 0
                idx = i
                fsDepth++
            }

            if (c == FORM_CLOSE) {
                fcDepth++
                if (fsDepth == 1 && fcDepth == 1) {
                    val fullTag = input.substring(idx, i + 1)
                    val validStyles = getValidStyles(fullTag, delimiterPattern, context, strictParsing)
                    if (validStyles.isNotEmpty()) {
                        tokens.add(ParseToken(idx, i, validStyles))
                    }
                }
                fsDepth = maxOf(0, fsDepth - 1)
            }
        }

        return ParseResult(tokens)
    }

    private fun charNotEquals(input: String, i: Int, c: Char): Boolean {
        if (i < 0) return true
        return input[i] != c
    }

    private fun getValidStyles(
        tag: String,
        delimiterPattern: Regex,
        context: StyleContext,
        strictParsing: Boolean,
    ): List<AnsiCode> {
        if (tag.length <= 2) return emptyList()
        val inner = tag.substring(1, tag.length - 1)
        val parts = inner.split(delimiterPattern)
        val validStyles = mutableListOf<AnsiCode>()
        for (part in parts) {
            val s = part.trim().lowercase()
            val code = PredefinedStyleContext.get(s, context)
            if (code != null) {
                validStyles.add(code)
            } else if (strictParsing) {
                throw IllegalArgumentException("Unidentified style: '$s'")
            }
        }
        return validStyles
    }
}
