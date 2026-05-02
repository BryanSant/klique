package io.github.bryansant.klique.internal

import io.github.bryansant.klique.components.Box.BoxType

internal class BorderChars(
    var hLine: String,
    var vLine: String,
    var topLeft: String,
    var topRight: String,
    var bottomLeft: String,
    var bottomRight: String,
) {
    companion object {
        fun from(type: BoxType): BorderChars = when (type) {
            BoxType.ASCII -> BorderChars("-", "|", "+", "+", "+", "+")
            BoxType.DOUBLE_LINE -> BorderChars("═", "║", "╔", "╗", "╚", "╝")
            BoxType.CLASSIC -> BorderChars("─", "│", "┌", "┐", "└", "┘")
            BoxType.ROUNDED -> BorderChars("─", "│", "╭", "╮", "╰", "╯")
        }
    }

    fun setCorners(edge: String) {
        topLeft = edge; topRight = edge; bottomLeft = edge; bottomRight = edge
    }
}
