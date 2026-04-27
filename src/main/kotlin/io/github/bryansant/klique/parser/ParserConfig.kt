package io.github.bryansant.klique.parser

import io.github.bryansant.klique.spi.AnsiCode

class ParserConfig private constructor(
    val delimiter: String,
    val strictParsing: Boolean,
    val autoReset: Boolean,
    val styleContext: StyleContext,
) {
    companion object {
        val DEFAULT = ParserConfig(
            delimiter = ",",
            strictParsing = false,
            autoReset = false,
            styleContext = StyleContext.NONE,
        )

        fun builder(): Builder = Builder()
    }

    class Builder {
        private var delimiter = ","
        private var strictParsing = false
        private var autoReset = false
        private val contextBuilder = StyleContext.builder()

        fun delimiter(c: Char): Builder { delimiter = c.toString(); return this }
        fun delimiter(s: String): Builder { delimiter = s; return this }
        fun enableStrictParsing(): Builder { strictParsing = true; return this }
        fun enableAutoReset(): Builder { autoReset = true; return this }
        fun disableAutoReset(): Builder { autoReset = false; return this }

        fun addStyle(name: String, code: AnsiCode): Builder {
            contextBuilder.add(name, code)
            return this
        }

        fun styleContext(ctx: StyleContext): Builder {
            contextBuilder.add(ctx)
            return this
        }

        fun build(): ParserConfig = ParserConfig(
            delimiter = delimiter,
            strictParsing = strictParsing,
            autoReset = autoReset,
            styleContext = contextBuilder.build(),
        )
    }

    override fun equals(other: Any?): Boolean = other is ParserConfig &&
        delimiter == other.delimiter && strictParsing == other.strictParsing &&
        autoReset == other.autoReset && styleContext == other.styleContext

    override fun hashCode(): Int = arrayOf(delimiter, strictParsing, autoReset, styleContext).contentHashCode()
    override fun toString(): String = "ParserConfig[delimiter='$delimiter', strict=$strictParsing, autoReset=$autoReset]"
}
