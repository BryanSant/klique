package io.github.bryansant.klique.components

import io.github.bryansant.klique.TextAlign
import io.github.bryansant.klique.config.BoxConfig
import io.github.bryansant.klique.internal.BorderChars
import io.github.bryansant.klique.internal.BoxWrapper
import io.github.bryansant.klique.internal.WidthAwareList
import io.github.bryansant.klique.internal.utils.BoxUtils
import io.github.bryansant.klique.internal.utils.StringUtils

class Box(val config: BoxConfig = BoxConfig.DEFAULT) : Component {

    private var contentText: String? = null
    private var alignOverride: TextAlign? = null
    private var fixedWidth: Int = 0
    private var fixedHeight: Int = 0
    private var cachedString: String? = null

    private val borderChars: BorderChars = BorderChars.from(config.boxType).also { chars ->
        if (config.borderColor.isNotEmpty()) BoxUtils.applyAnsiToBorders(chars, config.borderColor)
    }

    fun content(text: String): Box {
        contentText = text
        cachedString = null
        return this
    }

    fun content(text: String, align: TextAlign): Box {
        contentText = text
        alignOverride = align
        cachedString = null
        return this
    }

    fun dimensions(width: Int, height: Int): Box {
        require(width > 0 && height > 0) {
            "Width and height must be greater than 0"
        }
        fixedWidth = width
        fixedHeight = height
        return this
    }

    override fun get(): String {
        cachedString?.let { return it }
        val cells = resolveLines()
        val (width, height) = resolveDimensions(cells)
        val ta = alignOverride ?: config.textAlign
        val sb = StringBuilder()
        val wrapper = BoxWrapper(
            width, height, config, cells.cells(),
            borderChars.hLine, borderChars.vLine,
            borderChars.topLeft, borderChars.topRight,
            borderChars.bottomRight, borderChars.bottomLeft,
        )
        BoxUtils.drawBox(sb, wrapper, ta)
        return sb.toString().also { cachedString = it }
    }

    private fun resolveLines(): WidthAwareList {
        val text = contentText ?: return WidthAwareList()
        if (text.isEmpty()) return WidthAwareList()
        return WidthAwareList(text.lines().map { StringUtils.parseToCell(it, config.parser) })
    }

    private fun resolveDimensions(cells: WidthAwareList): Pair<Int, Int> {
        return if (fixedWidth == 0 && fixedHeight == 0) {
            val w = cells.longest + (config.padding * 2)
            val h = cells.size()
            Pair(w, h)
        } else {
            val usableWidth = fixedWidth - (config.padding * 2)
            if (cells.longest > usableWidth) error(
                "Content overflows: content is ${cells.longest} wide but usable inner width is only $usableWidth"
            )
            if (cells.size() > fixedHeight) error(
                "Content overflows: ${cells.size()} lines cannot fit in box height $fixedHeight"
            )
            Pair(fixedWidth, fixedHeight)
        }
    }
}
