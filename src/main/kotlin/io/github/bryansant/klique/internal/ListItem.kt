package io.github.bryansant.klique.internal

import io.github.bryansant.klique.components.ItemList

internal data class ListItem(
    val symbol: String,
    val content: String,
    val sublist: ItemList,
)
