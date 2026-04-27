package io.github.bryansant.klique.components

import io.github.bryansant.klique.config.ItemListConfig
import io.github.bryansant.klique.internal.Constants
import io.github.bryansant.klique.internal.ListItem
import io.github.bryansant.klique.internal.utils.StringUtils
import io.github.bryansant.klique.style.StyleCode

class ItemList(var config: ItemListConfig = ItemListConfig.DEFAULT) : Component {

    private val items: MutableList<ListItem> = mutableListOf()

    companion object {
        internal val NONE = ItemList()
    }

    fun item(symbol: String, content: String, sublist: ItemList): ItemList {
        require(sublist !== this) { "Cannot nest list within itself" }
        items.add(ListItem(symbol, content, sublist.apply { config = this@ItemList.config }))
        return this
    }

    fun item(symbol: String, content: String): ItemList {
        items.add(ListItem(symbol, content, NONE))
        return this
    }

    override fun get(): String {
        val sb = StringBuilder()
        buildList(sb, 0, this)
        return StringUtils.parse(sb.toString(), config.parser)
    }

    private fun buildList(sb: StringBuilder, depth: Int, root: ItemList) {
        for (item in root.items) {
            sb.append(Constants.BLANK.repeat(depth * config.indentSize))
                .append(item.symbol)
                .append(Constants.BLANK.repeat(config.symbolSpacing))
                .append(item.content)
                .append(StyleCode.RESET)
                .append(Constants.NEWLINE)
            if (item.sublist !== NONE) {
                buildList(sb, depth + 1, item.sublist)
            }
        }
    }
}
