package io.github.bryansant.klique.internal.utils

import io.github.bryansant.klique.CellAlign
import io.github.bryansant.klique.internal.Constants
import io.github.bryansant.klique.internal.WidthAwareList

internal object TableUtils {

    fun align(
        align: CellAlign,
        sb: StringBuilder,
        offset: Int,
        styled: String,
        vLine: String,
    ): String {
        val spaces = Constants.BLANK.repeat(offset)
        return when (align) {
            CellAlign.LEFT -> sb.append(styled).append(spaces).append(vLine).toString()
            CellAlign.RIGHT -> sb.append(spaces).append(styled).append(vLine).toString()
            CellAlign.CENTER -> {
                val len = spaces.length
                val rem = len % 2
                val leftOffset = (len - rem) - (len / 2)
                val rightOffset = len - leftOffset
                sb.append(Constants.BLANK.repeat(leftOffset)).append(styled)
                    .append(Constants.BLANK.repeat(rightOffset)).append(vLine).toString()
            }
        }
    }

    fun handleNulls(value: String?, nullReplacement: String): String = value ?: nullReplacement

    fun validateRowIndex(rowIdx: Int, rows: List<WidthAwareList>) {
        if (rowIdx > rows.size - 1)
            throw IllegalArgumentException("Row $rowIdx does not exist")
    }

    fun validateHeaders(idx: Int) {
        if (idx == 0) throw IllegalArgumentException("Cannot remove a header from a table")
    }

    fun validateColumnIndex(colIdx: Int, cols: List<WidthAwareList>) {
        if (colIdx > cols.size - 1)
            throw IllegalArgumentException("Column $colIdx does not exist")
    }

    fun chooseColAlignment(
        colIdx: Int,
        defAlign: CellAlign,
        cAlign: Map<Int, CellAlign>,
    ): CellAlign = cAlign[colIdx] ?: defAlign
}
