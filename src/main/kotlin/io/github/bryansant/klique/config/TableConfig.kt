package io.github.bryansant.klique.config

import io.github.bryansant.klique.components.Table.CellAlign
import io.github.bryansant.klique.components.Table.TableType
import io.github.bryansant.klique.parser.MarkupParser
import io.github.bryansant.klique.spi.AnsiCode

data class TableConfig(
    val padding: Int = 1,
    val alignment: CellAlign = CellAlign.LEFT,
    val nullReplacement: String = "",
    val borderColor: Array<AnsiCode> = emptyArray(),
    val columnAlignment: Map<Int, CellAlign> = emptyMap(),
    val parser: MarkupParser = MarkupParser.DEFAULT,
    val tableType: TableType = TableType.BOX_DRAW,
) {
    companion object {
        val DEFAULT = TableConfig()
    }

    override fun equals(other: Any?): Boolean = other is TableConfig &&
        padding == other.padding && alignment == other.alignment &&
        nullReplacement == other.nullReplacement &&
        borderColor.contentEquals(other.borderColor) &&
        columnAlignment == other.columnAlignment && parser == other.parser && tableType == other.tableType

    override fun hashCode(): Int = arrayOf(
        padding, alignment, nullReplacement, borderColor.contentHashCode(), columnAlignment, parser, tableType
    ).contentHashCode()
}

class TableConfigBuilder {
    var padding: Int = 1
    var alignment: CellAlign = CellAlign.LEFT
    var nullReplacement: String = ""
    var borderColor: Array<AnsiCode> = emptyArray()
    val columnAlignment: MutableMap<Int, CellAlign> = mutableMapOf()
    var parser: MarkupParser = MarkupParser.DEFAULT
    var tableType: TableType = TableType.BOX_DRAW

    fun borderColor(vararg codes: AnsiCode) { borderColor = arrayOf(*codes) }
    fun borderColor(colorName: String) { borderColor = ParserUtils.getAnsiCodes(colorName, parser) }
    fun columnAlignment(col: Int, align: CellAlign) { columnAlignment[col] = align }

    fun build() = TableConfig(padding, alignment, nullReplacement, borderColor, columnAlignment.toMap(), parser, tableType)
}

fun tableConfig(block: TableConfigBuilder.() -> Unit): TableConfig = TableConfigBuilder().apply(block).build()
