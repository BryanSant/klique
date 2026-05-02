package io.github.bryansant.klique.components

import io.github.bryansant.klique.config.FrameConfig
import io.github.bryansant.klique.internal.BorderChars
import io.github.bryansant.klique.internal.Cell
import io.github.bryansant.klique.internal.FrameNode
import io.github.bryansant.klique.internal.utils.BoxUtils
import io.github.bryansant.klique.internal.utils.StringUtils
import io.github.bryansant.klique.style.StyleCode

class Frame(val config: FrameConfig = FrameConfig.DEFAULT) : Component {

    enum class FrameAlign { LEFT, CENTER, RIGHT }


    private val nodes = mutableListOf<FrameNode>()
    private var titleText: String = ""
    private var titleAlign: FrameAlign = FrameAlign.CENTER
    private var fixedWidth: Int = 0

    private val borderChars: BorderChars = BorderChars.from(config.boxType).also { chars ->
        if (config.borderColor.isNotEmpty()) BoxUtils.applyAnsiToBorders(chars, config.borderColor)
    }

    fun title(text: String, align: FrameAlign = FrameAlign.CENTER): Frame {
        titleText = text
        titleAlign = align
        return this
    }

    fun width(width: Int): Frame {
        require(width > 0) { "Frame width must be greater than zero" }
        fixedWidth = width
        return this
    }

    fun nest(str: String, align: FrameAlign = config.frameAlign): Frame {
        nodes.add(FrameNode.StringNode(str + StyleCode.RESET, align, config.parser))
        return this
    }

    fun nest(component: Component, align: FrameAlign = config.frameAlign): Frame {
        require(component !== this) { "Cannot nest a Frame in itself" }
        nodes.add(FrameNode.ComponentNode(component, align))
        return this
    }

    override fun get(): String {
        val appendedTitle = if (titleText.isNotEmpty()) titleText + StyleCode.RESET else titleText

        val parsedTitle = StringUtils.parseToCell(appendedTitle, config.parser)
        val titleWidth = parsedTitle.width + 2
        val nodesMaxWidth = nodes.maxOfOrNull { it.maxWidth() } ?: 0

        var givenWidth = if (fixedWidth == 0) nodesMaxWidth + (config.padding * 2) else fixedWidth
        if (fixedWidth == 0 && !parsedTitle.isEmpty) {
            givenWidth = maxOf(givenWidth, titleWidth + 1)
        }

        val availableWidth = givenWidth - (config.padding * 2)

        if (!parsedTitle.isEmpty && titleWidth > givenWidth) error(
            "Title width ($titleWidth) exceeds frame width ($givenWidth)"
        )
        if (nodesMaxWidth > availableWidth) error(
            "Content width ($nodesMaxWidth) exceeds available frame width ($availableWidth)"
        )

        val sb = StringBuilder()
        appendTitle(parsedTitle, givenWidth, titleWidth, sb)

        for (node in nodes) alignNode(node, availableWidth, sb)

        sb.append(borderChars.bottomLeft)
            .append(borderChars.hLine.repeat(givenWidth))
            .append(borderChars.bottomRight)
            .append("\n")

        return sb.toString()
    }

    private fun alignNode(node: FrameNode, availableWidth: Int, sb: StringBuilder) {
        val padding = config.padding
        val rem = maxOf(0, availableWidth - node.maxWidth())
        val fixed = " ".repeat(padding)

        for (line in node.lines()) {
            val lineWidth = line.width
            val content = line.styledText
            when (node.align()) {
                FrameAlign.RIGHT -> sb.append(borderChars.vLine).append(fixed)
                    .append(" ".repeat(rem)).append(content).append(fixed)
                    .append(" ".repeat(maxOf(0, availableWidth - lineWidth - rem)))
                    .append(borderChars.vLine).append("\n")

                FrameAlign.LEFT -> sb.append(borderChars.vLine).append(fixed).append(content)
                    .append(" ".repeat(maxOf(0, availableWidth - lineWidth)))
                    .append(fixed).append(borderChars.vLine).append("\n")

                FrameAlign.CENTER -> {
                    val leftPad = rem / 2
                    val rightPad = (availableWidth - lineWidth) - leftPad
                    sb.append(borderChars.vLine).append(fixed)
                        .append(" ".repeat(leftPad)).append(content)
                        .append(" ".repeat(rightPad)).append(fixed)
                        .append(borderChars.vLine).append("\n")
                }
            }
        }
    }

    private fun appendTitle(parsedTitle: Cell, givenWidth: Int, titleWidth: Int, sb: StringBuilder) {
        if (!parsedTitle.isEmpty) {
            val leftWidth = when (titleAlign) {
                FrameAlign.LEFT -> 1
                FrameAlign.RIGHT -> (givenWidth - titleWidth) - 1
                FrameAlign.CENTER -> (givenWidth - titleWidth) / 2
            }
            sb.append(borderChars.topLeft)
                .append(borderChars.hLine.repeat(leftWidth))
                .append(" ").append(parsedTitle.styledText).append(" ")
                .append(borderChars.hLine.repeat(givenWidth - titleWidth - leftWidth))
                .append(borderChars.topRight).append("\n")
        } else {
            sb.append(borderChars.topLeft)
                .append(borderChars.hLine.repeat(givenWidth))
                .append(borderChars.topRight).append("\n")
        }
    }
}
