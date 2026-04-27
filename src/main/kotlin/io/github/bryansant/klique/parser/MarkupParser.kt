package io.github.bryansant.klique.parser

import io.github.bryansant.klique.internal.utils.StringUtils

class MarkupParser(val config: ParserConfig = ParserConfig.DEFAULT) {

    companion object {
        val DEFAULT = MarkupParser()
    }

    fun parse(string: String?): String {
        if (string.isNullOrBlank()) return string ?: ""
        val result = getParseResult(string)
        val styled = StyleResolver.resolve(result.tokens, string, config.autoReset)
        return postProcess(styled)
    }

    fun print(string: String?) = println(parse(string))

    fun getOriginalString(tokenedString: String?): String {
        if (tokenedString.isNullOrBlank()) return tokenedString ?: ""
        val result = getParseResult(tokenedString)
        if (!result.isPresent) return StringUtils.stripAnsi(postProcess(tokenedString))

        val sb = StringBuilder(tokenedString.length)
        var cursor = 0
        for (token in result.tokens) {
            sb.append(tokenedString, cursor, token.start)
            cursor = token.end + 1
        }
        sb.append(tokenedString, cursor, tokenedString.length)
        return StringUtils.stripAnsi(postProcess(sb.toString()))
    }

    internal fun getParseResult(input: String): ParseResult =
        Tokenizer.tokenize(input, config.delimiter, config.strictParsing, config.styleContext)

    private fun postProcess(input: String): String = input.replace("\\[", "[")

    override fun equals(other: Any?): Boolean = other is MarkupParser && config == other.config
    override fun hashCode(): Int = config.hashCode()
}
