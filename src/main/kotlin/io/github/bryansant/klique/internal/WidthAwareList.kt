package io.github.bryansant.klique.internal

internal class WidthAwareList(cells: List<Cell> = emptyList()) {
    private val list: MutableList<Cell> = cells.toMutableList()
    var longest: Int = if (cells.isEmpty()) 0 else cells.maxOf { it.width }
        private set

    fun add(c: Cell) {
        if (c.width > longest) longest = c.width
        list.add(c)
    }

    fun update(i: Int, c: Cell) {
        if (c.width > longest) longest = c.width
        list[i] = c
    }

    fun remove(c: Cell) {
        list.remove(c)
        longest = if (list.isEmpty()) 0 else list.maxOf { it.width }
    }

    fun get(pos: Int): Cell = list[pos]
    fun getStyledText(pos: Int): String = list[pos].styledText
    fun size(): Int = list.size
    fun cells(): List<Cell> = list.toList()
    fun list(): List<String> = list.map { it.styledText }
}
