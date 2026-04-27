package io.github.bryansant.klique.internal.utils

import io.github.bryansant.klique.TextAlign
import io.github.bryansant.klique.internal.BorderChars
import io.github.bryansant.klique.internal.BoxWrapper
import io.github.bryansant.klique.internal.Cell
import io.github.bryansant.klique.internal.Constants
import io.github.bryansant.klique.spi.AnsiCode
import io.github.bryansant.klique.style.StyleCode

internal object BoxUtils {

    private val RESET = StyleCode.RESET.ansiSequence()

    fun alignText(
        sb: StringBuilder,
        idx: Int,
        textAlign: TextAlign,
        spaces: String,
        wordWrap: List<Cell>,
        vLine: String,
        padding: Int,
    ) {
        val cell = wordWrap[idx]
        val ss = cell.styledText
        val fixed = Constants.BLANK.repeat(padding)
        val fillSpace = spaces.length - cell.width - (padding * 2)

        when (textAlign) {
            TextAlign.TOP_LEFT, TextAlign.CENTER_LEFT, TextAlign.BOTTOM_LEFT ->
                sb.append(vLine).append(fixed).append(ss).append(RESET)
                    .append(Constants.BLANK.repeat(maxOf(0, fillSpace))).append(fixed).append(vLine).append(Constants.NEWLINE)

            TextAlign.TOP_RIGHT, TextAlign.CENTER_RIGHT, TextAlign.BOTTOM_RIGHT ->
                sb.append(vLine).append(fixed).append(Constants.BLANK.repeat(maxOf(0, fillSpace)))
                    .append(ss).append(RESET).append(fixed).append(vLine).append(Constants.NEWLINE)

            TextAlign.TOP_CENTER, TextAlign.CENTER, TextAlign.BOTTOM_CENTER -> {
                val leftFill = fillSpace / 2
                val rightFill = fillSpace - leftFill
                sb.append(vLine).append(fixed).append(Constants.BLANK.repeat(maxOf(0, leftFill)))
                    .append(ss).append(RESET).append(Constants.BLANK.repeat(maxOf(0, rightFill)))
                    .append(fixed).append(vLine).append(Constants.NEWLINE)
            }
        }
    }

    fun drawBox(sb: StringBuilder, boxWrapper: BoxWrapper, textAlign: TextAlign) {
        val width = boxWrapper.width
        val padding = boxWrapper.configuration.padding
        val spaces = Constants.BLANK.repeat(width)
        val hLines = boxWrapper.hLine.repeat(width)
        sb.setLength(0)
        sb.append(boxWrapper.tLeft).append(hLines).append(boxWrapper.tRight).append(Constants.NEWLINE)

        val textHeight = boxWrapper.wordWrap.size
        val availableLines = boxWrapper.height
        var startLine = when (textAlign) {
            TextAlign.CENTER, TextAlign.CENTER_LEFT, TextAlign.CENTER_RIGHT -> (availableLines - textHeight) / 2
            TextAlign.BOTTOM_LEFT, TextAlign.BOTTOM_CENTER, TextAlign.BOTTOM_RIGHT -> availableLines - textHeight
            else -> 0
        }

        for (i in 0 until boxWrapper.height) {
            if (i >= startLine && i < startLine + textHeight) {
                alignText(sb, i - startLine, textAlign, spaces, boxWrapper.wordWrap, boxWrapper.vLine, padding)
            } else {
                sb.append(boxWrapper.vLine).append(spaces).append(boxWrapper.vLine).append(Constants.NEWLINE)
            }
        }

        sb.append(boxWrapper.bLeft).append(hLines).append(boxWrapper.bRight)
    }

    fun applyAnsiToBorders(borderChar: BorderChars, borderColor: Array<AnsiCode>) {
        if (borderColor.isEmpty()) return
        val sb = StringBuilder()
        borderChar.hLine = StringUtils.formatAndReset(sb, borderChar.hLine, *borderColor)
        borderChar.vLine = StringUtils.formatAndReset(sb, borderChar.vLine, *borderColor)
        borderChar.topLeft = StringUtils.formatAndReset(sb, borderChar.topLeft, *borderColor)
        borderChar.topRight = StringUtils.formatAndReset(sb, borderChar.topRight, *borderColor)
        borderChar.bottomLeft = StringUtils.formatAndReset(sb, borderChar.bottomLeft, *borderColor)
        borderChar.bottomRight = StringUtils.formatAndReset(sb, borderChar.bottomRight, *borderColor)
    }
}
