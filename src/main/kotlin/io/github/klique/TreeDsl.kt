package io.github.klique

import io.github.kusoroadeolu.clique.components.Tree

// ── Tree extensions ───────────────────────────────────────────────────────────
//
// Tree.add(label) returns the newly created CHILD node, not the parent.
// These extensions restore Kotlin DSL ergonomics where the block receiver
// is always the node you're currently building.

/**
 * Adds a child node with the given [label] and applies [block] to configure
 * its children. Returns `this` (the parent) so multiple branches can be chained.
 *
 * ```kotlin
 * tree("project/") {
 *     branch("src/") {
 *         leaf("Main.kt")
 *         leaf("App.kt")
 *     }
 *     leaf("build.gradle.kts")
 * }
 * ```
 */
fun Tree.branch(label: String, block: Tree.() -> Unit): Tree {
    add(label).apply(block)
    return this
}

/**
 * Adds a terminal (leaf) child node and returns `this` for chaining.
 * Equivalent to [Tree.add] but returns the parent instead of the new child.
 */
fun Tree.leaf(label: String): Tree = also { add(label) }

