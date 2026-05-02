package io.github.bryansant.klique.components

import io.github.bryansant.klique.components.Table.TableType
import io.github.bryansant.klique.config.TableConfig
import io.github.bryansant.klique.internal.Cell
import io.github.bryansant.klique.internal.WidthAwareList
import io.github.bryansant.klique.internal.utils.StringUtils
import io.github.bryansant.klique.internal.utils.TableUtils

sealed interface Table : Component {

    enum class CellAlign { LEFT, CENTER, RIGHT }
    enum class TableType { ASCII, COMPACT, BOX_DRAW, ROUNDED_BOX_DRAW, MARKDOWN }

    fun row(vararg cells: String?): Table
    fun row(cells: Collection<String?>): Table = row(*cells.toTypedArray())
    fun removeRow(index: Int): Table
    fun removeCell(row: Int, col: Int): Table
    fun updateCell(row: Int, col: Int, text: String): Table
}

internal abstract class AbstractTable(val config: TableConfig) : Table {
    val columns: MutableList<WidthAwareList> = mutableListOf()
    val rows: MutableList<WidthAwareList> = mutableListOf()
    var cachedString: String? = null

    fun headers(vararg headers: String): Table {
        require(headers.isNotEmpty()) { "Headers cannot be null or empty" }
        val rowList = WidthAwareList()
        rows.add(rowList)
        for ((i, h) in headers.withIndex()) {
            val header = TableUtils.handleNulls(h, config.nullReplacement)
            val cell = StringUtils.parseToCell(header, config.parser)
            rowList.add(cell)
            val colList = WidthAwareList()
            colList.add(cell)
            columns.add(i, colList)
        }
        return this
    }

    override fun row(vararg cells: String?): Table {
        val headerSize = rows.first().size()
        val rowList = WidthAwareList()
        rows.add(rowList)
        for (i in 0 until headerSize) {
            val raw = if (i >= cells.size) config.nullReplacement
            else TableUtils.handleNulls(cells[i], config.nullReplacement)
            val cell = StringUtils.parseToCell(raw, config.parser)
            rowList.add(cell)
            columns[i].add(cell)
        }
        nullCache()
        return this
    }

    override fun removeRow(index: Int): Table {
        TableUtils.validateHeaders(index)
        TableUtils.validateRowIndex(index, rows)
        val removed = rows.removeAt(index)
        for (col in columns) col.remove(col.get(index))
        nullCache()
        return this
    }

    override fun removeCell(row: Int, col: Int): Table = updateCell(row, col, config.nullReplacement)

    override fun updateCell(row: Int, col: Int, text: String): Table {
        TableUtils.validateRowIndex(row, rows)
        TableUtils.validateColumnIndex(col, columns)
        val cell = StringUtils.parseToCell(text, config.parser)
        rows[row].update(col, cell)
        columns[col].update(row, cell)
        nullCache()
        return this
    }

    protected fun nullCache() { cachedString = null }
    abstract fun colorBorders()
}

internal class AsciiTable(config: TableConfig) : AbstractTable(config) {
    private var corner = "+"
    private var hLine = "-"
    private var vLine = "|"

    init { colorBorders() }

    override fun colorBorders() {
        val color = config.borderColor
        if (color.isNotEmpty()) {
            val sb = StringBuilder()
            hLine = StringUtils.formatAndReset(sb, hLine, *color)
            vLine = StringUtils.formatAndReset(sb, vLine, *color)
            corner = StringUtils.formatAndReset(sb, corner, *color)
        }
    }

    override fun get(): String {
        cachedString?.let { return it }
        val tableBuilder = StringBuilder()
        val sb = StringBuilder()
        val headerAndFooter = appendBorder(sb)
        sb.setLength(0)

        tableBuilder.append(headerAndFooter).append("\n")

        for (list in rows) {
            tableBuilder.append(vLine)
            for (j in 0 until list.size()) {
                var cellAlign = config.alignment
                val styledCell = list.getStyledText(j)
                val displayWidth = list.get(j).width
                val longest = columns[j].longest
                val offset = (longest - displayWidth) + config.padding
                cellAlign = TableUtils.chooseColAlignment(j, cellAlign, config.columnAlignment)
                tableBuilder.append(TableUtils.align(cellAlign, sb, offset, styledCell, vLine))
                sb.setLength(0)
            }
            tableBuilder.append("\n")
            tableBuilder.append(headerAndFooter).append("\n")
        }

        return tableBuilder.toString().also { cachedString = it }
    }

    private fun appendBorder(sb: StringBuilder): String {
        for (col in columns) {
            sb.append(corner)
            repeat(col.longest + config.padding) { sb.append(hLine) }
        }
        sb.append(corner)
        return sb.toString()
    }
}

internal open class BoxDrawTable(config: TableConfig) : AbstractTable(config) {
    protected var topLeft = "┌"; protected var topRight = "┐"
    protected var bottomLeft = "└"; protected var bottomRight = "┘"
    private var hLine = "─"; private var vLine = "│"
    private var topJoin = "┬"; private var bottomJoin = "┴"
    private var leftJoin = "├"; private var rightJoin = "┤"
    private var cross = "┼"

    init { colorBorders() }

    override fun colorBorders() {
        val sb = StringBuilder()
        val color = config.borderColor
        hLine = StringUtils.formatAndReset(sb, hLine, *color)
        topJoin = StringUtils.formatAndReset(sb, topJoin, *color)
        bottomJoin = StringUtils.formatAndReset(sb, bottomJoin, *color)
        cross = StringUtils.formatAndReset(sb, cross, *color)
        vLine = StringUtils.formatAndReset(sb, vLine, *color)
        leftJoin = StringUtils.formatAndReset(sb, leftJoin, *color)
        rightJoin = StringUtils.formatAndReset(sb, rightJoin, *color)
        topLeft = StringUtils.formatAndReset(sb, topLeft, *color)
        topRight = StringUtils.formatAndReset(sb, topRight, *color)
        bottomLeft = StringUtils.formatAndReset(sb, bottomLeft, *color)
        bottomRight = StringUtils.formatAndReset(sb, bottomRight, *color)
    }

    override fun get(): String {
        cachedString?.let { return it }
        val tableBuilder = StringBuilder()
        val helperBuilder = StringBuilder()
        val header = drawEdges(helperBuilder, topLeft, topJoin, topRight)
        helperBuilder.setLength(0)
        val footer = drawEdges(helperBuilder, bottomLeft, bottomJoin, bottomRight)
        helperBuilder.setLength(0)
        val headerEnd = drawEdges(helperBuilder, leftJoin, cross, rightJoin)
        helperBuilder.setLength(0)

        tableBuilder.append(header).append("\n")

        for (i in rows.indices) {
            val list = rows[i]
            tableBuilder.append(vLine)
            for (j in 0 until list.size()) {
                var cellAlign = config.alignment
                val styledCell = list.getStyledText(j)
                val displayWidth = list.get(j).width
                val longest = columns[j].longest
                val offset = (longest - displayWidth) + config.padding
                cellAlign = TableUtils.chooseColAlignment(j, cellAlign, config.columnAlignment)
                tableBuilder.append(TableUtils.align(cellAlign, helperBuilder, offset, styledCell, vLine))
                helperBuilder.setLength(0)
            }
            if (i == 0) tableBuilder.append("\n").append(headerEnd)
            tableBuilder.append("\n")
        }
        tableBuilder.append(footer)
        return tableBuilder.toString().also { cachedString = it }
    }

    private fun drawEdges(sb: StringBuilder, left: String, join: String, right: String): String {
        sb.append(left)
        for (i in columns.indices) {
            repeat(columns[i].longest + config.padding) { sb.append(hLine) }
            if (i < columns.size - 1) sb.append(join)
        }
        sb.append(right)
        return sb.toString()
    }
}

internal class RoundedBoxDrawTable(config: TableConfig) : BoxDrawTable(config) {
    init {
        topLeft = "╭"; topRight = "╮"; bottomLeft = "╰"; bottomRight = "╯"
        colorBorders()
    }
}

internal class CompactTable(config: TableConfig) : AbstractTable(config) {
    private val vLine: String = " ".repeat(config.padding)
    private var hLine = "-"

    init { colorBorders() }

    override fun colorBorders() {
        hLine = StringUtils.formatAndReset(StringBuilder(), hLine, *config.borderColor)
    }

    override fun get(): String {
        cachedString?.let { return it }
        val tableBuilder = StringBuilder()
        val sb = StringBuilder()

        for (i in rows.indices) {
            val list = rows[i]
            for (j in 0 until list.size()) {
                var cellAlign = config.alignment
                val styledCell = list.getStyledText(j)
                val displayWidth = list.get(j).width
                val longest = columns[j].longest
                val offset = longest - displayWidth
                cellAlign = TableUtils.chooseColAlignment(j, cellAlign, config.columnAlignment)
                tableBuilder.append(TableUtils.align(cellAlign, sb, offset, styledCell, ""))
                if (j < list.size() - 1) tableBuilder.append(vLine)
                sb.setLength(0)
            }
            if (i == 0) {
                tableBuilder.append("\n").append(appendHeader(sb))
                sb.setLength(0)
            }
            tableBuilder.append("\n")
        }
        return tableBuilder.toString().also { cachedString = it }
    }

    private fun appendHeader(sb: StringBuilder): String {
        for (i in columns.indices) {
            repeat(columns[i].longest) { sb.append(hLine) }
            if (i < columns.size - 1) sb.append(vLine)
        }
        return sb.toString()
    }
}

internal class MarkdownTable(config: TableConfig) : AbstractTable(config) {
    private var vLine = "|"
    private var hLine = "-"

    init { colorBorders() }

    override fun colorBorders() {
        val color = config.borderColor
        if (color.isNotEmpty()) {
            val sb = StringBuilder()
            hLine = StringUtils.formatAndReset(sb, hLine, *color)
            vLine = StringUtils.formatAndReset(sb, vLine, *color)
        }
    }

    override fun get(): String {
        cachedString?.let { return it }
        val tableBuilder = StringBuilder()
        val sb = StringBuilder()

        for (i in rows.indices) {
            val list = rows[i]
            sb.append(vLine)
            for (j in 0 until list.size()) {
                var cellAlign = config.alignment
                val styledCell = list.getStyledText(j)
                val displayWidth = list.get(j).width
                val longest = columns[j].longest
                val offset = (longest - displayWidth) + config.padding
                cellAlign = TableUtils.chooseColAlignment(j, cellAlign, config.columnAlignment)
                tableBuilder.append(TableUtils.align(cellAlign, sb, offset, styledCell, vLine))
                sb.setLength(0)
            }
            if (i == 0) {
                tableBuilder.append("\n")
                sb.append(vLine)
                for (col in columns) {
                    repeat(col.longest + config.padding) { sb.append(hLine) }
                    sb.append(vLine)
                }
                tableBuilder.append(sb.toString())
                sb.setLength(0)
            }
            tableBuilder.append("\n")
        }
        return tableBuilder.toString().also { cachedString = it }
    }
}

internal fun createTable(type: TableType, config: TableConfig): AbstractTable = when (type) {
    TableType.ASCII -> AsciiTable(config)
    TableType.COMPACT -> CompactTable(config)
    TableType.BOX_DRAW -> BoxDrawTable(config)
    TableType.ROUNDED_BOX_DRAW -> RoundedBoxDrawTable(config)
    TableType.MARKDOWN -> MarkdownTable(config)
}
