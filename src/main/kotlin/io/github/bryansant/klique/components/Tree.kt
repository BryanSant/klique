package io.github.bryansant.klique.components

import io.github.bryansant.klique.config.TreeConfig
import io.github.bryansant.klique.internal.utils.StringUtils
import io.github.bryansant.klique.style.StyleBuilder

class Tree private constructor(
    val label: String,
    val config: TreeConfig,
    private var parent: Tree?,
) : Component {
    constructor(label: String, config: TreeConfig = TreeConfig.DEFAULT) : this(label, config, null)

    private val children: MutableList<Tree> = mutableListOf()

    companion object {
        const val CONNECTOR = "├─ "
        const val END_CONNECTOR = "└─ "
        const val SPACE = "   "
        const val CONNECTING_LINE = "│  "
    }

    fun add(label: String): Tree {
        val child = Tree(label, config, this)
        children.add(child)
        return child
    }

    fun add(tree: Tree): Tree {
        require(tree !== this) { "Cannot nest a tree within itself" }
        tree.parent = this
        children.add(tree)
        return tree
    }

    fun parent(): Tree? = parent

    override fun get(): String {
        val sb = StyleBuilder()
        sb.appendAndReset(label).appendAndReset("\n")
        for (i in children.indices) {
            buildTree(children[i], "", i == children.lastIndex, sb)
        }
        return StringUtils.parse(sb.toString(), config.parser)
    }

    private fun buildTree(node: Tree, prefix: String, isLast: Boolean, sb: StyleBuilder) {
        val connector = if (isLast) END_CONNECTOR else CONNECTOR
        sb.append(prefix, *config.connectorColor)
            .appendAndReset(connector)
            .appendAndReset(node.label)
            .appendAndReset("\n")

        val childPrefix = prefix + if (isLast) SPACE else CONNECTING_LINE
        for (i in node.children.indices) {
            buildTree(node.children[i], childPrefix, i == node.children.lastIndex, sb)
        }
    }
}
