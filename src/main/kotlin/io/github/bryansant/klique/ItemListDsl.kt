package io.github.bryansant.klique

import io.github.bryansant.klique.components.ItemList

/**
 * Adds an item with a nested sublist configured via [block].
 */
fun ItemList.item(symbol: String, content: String, block: ItemList.() -> Unit): ItemList =
    item(symbol, content, ItemList(config).apply(block))
