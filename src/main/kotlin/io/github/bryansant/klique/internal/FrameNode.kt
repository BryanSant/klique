package io.github.bryansant.klique.internal

import io.github.bryansant.klique.components.Component
import io.github.bryansant.klique.components.Frame.FrameAlign
import io.github.bryansant.klique.internal.utils.StringUtils
import io.github.bryansant.klique.parser.MarkupParser

internal sealed interface FrameNode {
    fun lines(): List<Cell>
    fun maxWidth(): Int
    fun align(): FrameAlign

    class ComponentNode(
        private val component: Component,
        private val frameAlign: FrameAlign,
    ) : FrameNode {
        override fun lines(): List<Cell> = splitComponentLines(component.get())
        override fun maxWidth(): Int = lines().maxOfOrNull { it.width } ?: 0
        override fun align(): FrameAlign = frameAlign
    }

    class StringNode(
        str: String,
        private val frameAlign: FrameAlign,
        parser: MarkupParser,
    ) : FrameNode {
        private val cellLines: List<Cell> = splitLines(str, parser)
        override fun lines(): List<Cell> = cellLines
        override fun maxWidth(): Int = cellLines.maxOfOrNull { it.width } ?: 0
        override fun align(): FrameAlign = frameAlign
    }

    companion object {
        private fun splitComponentLines(str: String): List<Cell> =
            str.lines().map { StringUtils.parseToCell(it, MarkupParser.DEFAULT) }

        private fun splitLines(str: String, parser: MarkupParser): List<Cell> =
            str.lines().map { StringUtils.parseToCell(it, parser) }
    }
}
