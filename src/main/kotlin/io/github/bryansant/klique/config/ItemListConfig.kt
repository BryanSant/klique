package io.github.bryansant.klique.config

import io.github.bryansant.klique.parser.MarkupParser

data class ItemListConfig(
    val indentSize: Int = 2,
    val symbolSpacing: Int = 1,
    val parser: MarkupParser = MarkupParser.DEFAULT,
) {
    companion object {
        val DEFAULT = ItemListConfig()
    }
}

class ItemListConfigBuilder {
    var indentSize: Int = 2
    var symbolSpacing: Int = 1
    var parser: MarkupParser = MarkupParser.DEFAULT

    fun build() = ItemListConfig(indentSize, symbolSpacing, parser)
}

fun itemListConfig(block: ItemListConfigBuilder.() -> Unit): ItemListConfig =
    ItemListConfigBuilder().apply(block).build()
