package io.github.bryansant.klique.internal

import io.github.bryansant.klique.internal.utils.CharWidth

internal class Cell(val text: String, val styledText: String) {
    val width: Int = CharWidth.of(text)
    val isEmpty: Boolean get() = width == 0

    override fun toString() = "Cell[styledText='$styledText', text='$text', width=$width]"
}
