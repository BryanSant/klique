package io.github.klique

import io.github.kusoroadeolu.clique.Clique as JClique
import io.github.kusoroadeolu.clique.components.ItemList

// ── ItemList extensions ───────────────────────────────────────────────────────

/**
 * Adds an item with a nested sublist configured via [block].
 *
 * ```kotlin
 * itemList {
 *     item("✓", "Auth service")
 *     item("~", "Notifications") {
 *         item("!", "Waiting on design")
 *         item("!", "API spec pending")
 *     }
 * }
 * ```
 */
fun ItemList.item(symbol: String, content: String, block: ItemList.() -> Unit): ItemList =
    item(symbol, content, JClique.list().apply(block))

