package io.github.bryansant.klique

import io.github.bryansant.klique.components.Tree

/**
 * Adds a child node with the given [label] and applies [block] to configure its children.
 * Returns `this` (the parent) so multiple branches can be chained.
 */
fun Tree.branch(label: String, block: Tree.() -> Unit): Tree {
    add(label).apply(block)
    return this
}

/**
 * Adds a terminal (leaf) child node and returns `this` for chaining.
 */
fun Tree.leaf(label: String): Tree = also { add(label) }
