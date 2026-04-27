package io.github.bryansant.klique.components

import io.github.bryansant.klique.config.DividerConfig
import io.github.bryansant.klique.internal.Constants
import io.github.bryansant.klique.internal.utils.StringUtils
import io.github.bryansant.klique.style.StyleBuilder

class Divider(val width: Int, val config: DividerConfig = DividerConfig.DEFAULT) : Component {
    init {
        require(width > 0) { "Width must be greater than 0" }
    }

    private var titleText: String? = null

    fun title(title: String): Divider {
        val visible = config.parser.getOriginalString(title)
        require(visible.length < width) {
            "Title's visible length must be less than divider width"
        }
        titleText = title
        return this
    }

    override fun get(): String {
        val divChar = config.dividerChar.toString()
        val title = titleText

        if (title.isNullOrEmpty()) {
            return StyleBuilder()
                .appendAndReset(divChar.repeat(width), *config.dividerColor)
                .toString()
        }

        val contentLength = config.parser.getOriginalString(title).length + 2
        val content = Constants.BLANK + StringUtils.parse(title, config.parser) + Constants.BLANK
        val remaining = width - contentLength
        val left = remaining / 2
        val right = remaining - left

        return StyleBuilder()
            .appendAndReset(divChar.repeat(left), *config.dividerColor)
            .appendAndReset(content)
            .appendAndReset(divChar.repeat(right), *config.dividerColor)
            .toString()
    }
}
