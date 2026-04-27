package io.github.bryansant.klique.internal.utils

internal object CharWidth {

    private val BMP_WIDTHS = ByteArray(0x10000) { 1 }.also { bmp ->
        val zeroRanges = arrayOf(
            intArrayOf(0x00AD, 0x00AD),
            intArrayOf(0x0300, 0x036F),
            intArrayOf(0x0483, 0x0489),
            intArrayOf(0x0591, 0x05BD),
            intArrayOf(0x05BF, 0x05BF),
            intArrayOf(0x05C1, 0x05C2),
            intArrayOf(0x05C4, 0x05C5),
            intArrayOf(0x05C7, 0x05C7),
            intArrayOf(0x0610, 0x061A),
            intArrayOf(0x064B, 0x065F),
            intArrayOf(0x0670, 0x0670),
            intArrayOf(0x06D6, 0x06DC),
            intArrayOf(0x06DF, 0x06E4),
            intArrayOf(0x06E7, 0x06E8),
            intArrayOf(0x06EA, 0x06ED),
            intArrayOf(0x0711, 0x0711),
            intArrayOf(0x0730, 0x074A),
            intArrayOf(0x0900, 0x0902),
            intArrayOf(0x093A, 0x093A),
            intArrayOf(0x093C, 0x093C),
            intArrayOf(0x0941, 0x0948),
            intArrayOf(0x094D, 0x094D),
            intArrayOf(0x0951, 0x0957),
            intArrayOf(0x0962, 0x0963),
            intArrayOf(0x0981, 0x0981),
            intArrayOf(0x09BC, 0x09BC),
            intArrayOf(0x09C1, 0x09C4),
            intArrayOf(0x09CD, 0x09CD),
            intArrayOf(0x09E2, 0x09E3),
            intArrayOf(0x0A01, 0x0A02),
            intArrayOf(0x0A3C, 0x0A3C),
            intArrayOf(0x0A41, 0x0A42),
            intArrayOf(0x0A47, 0x0A48),
            intArrayOf(0x0A4B, 0x0A4D),
            intArrayOf(0x0A51, 0x0A51),
            intArrayOf(0x0A70, 0x0A71),
            intArrayOf(0x0A75, 0x0A75),
            intArrayOf(0x0E31, 0x0E31),
            intArrayOf(0x0E34, 0x0E3A),
            intArrayOf(0x0E47, 0x0E4E),
            intArrayOf(0x1AB0, 0x1AFF),
            intArrayOf(0x1DC0, 0x1DFF),
            intArrayOf(0x200B, 0x200F),
            intArrayOf(0x2028, 0x202F),
            intArrayOf(0x2060, 0x2064),
            intArrayOf(0x2066, 0x206F),
            intArrayOf(0x20D0, 0x20FF),
            intArrayOf(0xFE00, 0xFE0F),
            intArrayOf(0xFE20, 0xFE2F),
            intArrayOf(0xFEFF, 0xFEFF),
        )
        for (r in zeroRanges) for (cp in r[0]..r[1]) bmp[cp] = 0

        val wideRanges = arrayOf(
            intArrayOf(0x231A, 0x231B),
            intArrayOf(0x23E9, 0x23EC),
            intArrayOf(0x23F0, 0x23F0),
            intArrayOf(0x23F3, 0x23F3),
            intArrayOf(0x25FD, 0x25FE),
            intArrayOf(0x2614, 0x2615),
            intArrayOf(0x2630, 0x2637),
            intArrayOf(0x2648, 0x2653),
            intArrayOf(0x267F, 0x267F),
            intArrayOf(0x268A, 0x268F),
            intArrayOf(0x2693, 0x2693),
            intArrayOf(0x26A1, 0x26A1),
            intArrayOf(0x26AA, 0x26AB),
            intArrayOf(0x26BD, 0x26BE),
            intArrayOf(0x26C4, 0x26C5),
            intArrayOf(0x26CE, 0x26CE),
            intArrayOf(0x26D4, 0x26D4),
            intArrayOf(0x26EA, 0x26EA),
            intArrayOf(0x26F2, 0x26F3),
            intArrayOf(0x26F5, 0x26F5),
            intArrayOf(0x26FA, 0x26FA),
            intArrayOf(0x26FD, 0x26FD),
            intArrayOf(0x2705, 0x2705),
            intArrayOf(0x270A, 0x270B),
            intArrayOf(0x2728, 0x2728),
            intArrayOf(0x274C, 0x274C),
            intArrayOf(0x274E, 0x274E),
            intArrayOf(0x2753, 0x2755),
            intArrayOf(0x2757, 0x2757),
            intArrayOf(0x2795, 0x2797),
            intArrayOf(0x27B0, 0x27B0),
            intArrayOf(0x27BF, 0x27BF),
            intArrayOf(0x2B05, 0x2B07),
            intArrayOf(0x2B1B, 0x2B1C),
            intArrayOf(0x2B50, 0x2B50),
            intArrayOf(0x2B55, 0x2B55),
            intArrayOf(0x2E80, 0x2FDF),
            intArrayOf(0x2FF0, 0x303E),
            intArrayOf(0x3041, 0x33FF),
            intArrayOf(0x3400, 0x4DBF),
            intArrayOf(0x4E00, 0x9FFF),
            intArrayOf(0xA000, 0xA4CF),
            intArrayOf(0xAC00, 0xD7AF),
            intArrayOf(0xF900, 0xFAFF),
            intArrayOf(0xFF01, 0xFF60),
            intArrayOf(0xFFE0, 0xFFE6),
            intArrayOf(0x2194, 0x2199),
            intArrayOf(0x21A9, 0x21AA),
            intArrayOf(0x2328, 0x2328),
            intArrayOf(0x23CF, 0x23CF),
            intArrayOf(0x23ED, 0x23EF),
            intArrayOf(0x23F1, 0x23F2),
            intArrayOf(0x23F8, 0x23FA),
            intArrayOf(0x24C2, 0x24C2),
            intArrayOf(0x25AA, 0x25AB),
            intArrayOf(0x25B6, 0x25B6),
            intArrayOf(0x25C0, 0x25C0),
            intArrayOf(0x25FB, 0x25FE),
            intArrayOf(0x2600, 0x2604),
            intArrayOf(0x260E, 0x260E),
            intArrayOf(0x2611, 0x2611),
            intArrayOf(0x2618, 0x2618),
            intArrayOf(0x261D, 0x261D),
            intArrayOf(0x2620, 0x2620),
            intArrayOf(0x2622, 0x2623),
            intArrayOf(0x2626, 0x2626),
            intArrayOf(0x262A, 0x262A),
            intArrayOf(0x262E, 0x262F),
            intArrayOf(0x2638, 0x263A),
            intArrayOf(0x2640, 0x2640),
            intArrayOf(0x2642, 0x2642),
            intArrayOf(0x265F, 0x2660),
            intArrayOf(0x2663, 0x2663),
            intArrayOf(0x2665, 0x2666),
            intArrayOf(0x2668, 0x2668),
            intArrayOf(0x267B, 0x267B),
            intArrayOf(0x267E, 0x267E),
            intArrayOf(0x2692, 0x2692),
            intArrayOf(0x2694, 0x2697),
            intArrayOf(0x2699, 0x2699),
            intArrayOf(0x269B, 0x269C),
            intArrayOf(0x26A0, 0x26A0),
            intArrayOf(0x26B0, 0x26B1),
            intArrayOf(0x26C8, 0x26C8),
            intArrayOf(0x26CF, 0x26D1),
            intArrayOf(0x26D3, 0x26D3),
            intArrayOf(0x26E9, 0x26E9),
            intArrayOf(0x26F0, 0x26F1),
            intArrayOf(0x26F4, 0x26F4),
            intArrayOf(0x26F7, 0x26F9),
            intArrayOf(0x2702, 0x2702),
            intArrayOf(0x2708, 0x2708),
            intArrayOf(0x2709, 0x2709),
            intArrayOf(0x270C, 0x270D),
            intArrayOf(0x270F, 0x270F),
            intArrayOf(0x2712, 0x2712),
            intArrayOf(0x2714, 0x2714),
            intArrayOf(0x2716, 0x2716),
            intArrayOf(0x271D, 0x271D),
            intArrayOf(0x2721, 0x2721),
            intArrayOf(0x2733, 0x2734),
            intArrayOf(0x2744, 0x2744),
            intArrayOf(0x2747, 0x2747),
            intArrayOf(0x2763, 0x2764),
            intArrayOf(0x27A1, 0x27A1),
            intArrayOf(0x2934, 0x2935),
            intArrayOf(0x2139, 0x2139),
            intArrayOf(0x3030, 0x3030),
            intArrayOf(0x303D, 0x303D),
            intArrayOf(0x3297, 0x3297),
            intArrayOf(0x3299, 0x3299),
        )
        for (r in wideRanges) for (cp in r[0]..r[1]) bmp[cp] = 2
    }

    private val suppWideStarts = intArrayOf(
        0x1F000, 0x1F0A0, 0x1F100, 0x1F1E0, 0x1F200,
        0x1F300, 0x1F600, 0x1F680, 0x1F7E0, 0x1F900,
        0x1FA00, 0x1FA70, 0x20000,
    )
    private val suppWideEnds = intArrayOf(
        0x1F02F, 0x1F0FF, 0x1F1DF, 0x1F1FF, 0x1F2FF,
        0x1F5FF, 0x1F64F, 0x1F6FF, 0x1F7F0, 0x1F9FF,
        0x1FA6F, 0x1FAFF, 0x2FA1F,
    )
    private val suppZeroStarts = intArrayOf(0x1F3FB, 0xE0001, 0xE0100)
    private val suppZeroEnds = intArrayOf(0x1F3FF, 0xE007F, 0xE01EF)

    fun ofCodePoint(codePoint: Int): Int {
        if (codePoint < 0x10000) return BMP_WIDTHS[codePoint].toInt()
        if (inRanges(codePoint, suppZeroStarts, suppZeroEnds)) return 0
        if (inRanges(codePoint, suppWideStarts, suppWideEnds)) return 2
        return 1
    }

    fun of(s: String?): Int {
        if (s.isNullOrEmpty()) return 0
        var width = 0
        var i = 0
        while (i < s.length) {
            val cp = s.codePointAt(i)
            val charCount = Character.charCount(cp)
            val cpWidth = ofCodePoint(cp)

            if (cpWidth == 0 && cp == 0x200D) {
                i += charCount
                if (i < s.length) i += Character.charCount(s.codePointAt(i))
                continue
            }

            if (isRegionalIndicator(cp)) {
                val nextIdx = i + charCount
                if (nextIdx < s.length) {
                    val next = s.codePointAt(nextIdx)
                    if (isRegionalIndicator(next)) {
                        width += 2
                        i = nextIdx + Character.charCount(next)
                        continue
                    }
                }
            }

            width += cpWidth
            i += charCount
        }
        return width
    }

    private fun isRegionalIndicator(cp: Int) = cp in 0x1F1E6..0x1F1FF

    private fun inRanges(cp: Int, starts: IntArray, ends: IntArray): Boolean {
        val idx = starts.binarySearch(cp)
        if (idx >= 0) return true
        val insertPoint = -(idx + 1)
        return insertPoint > 0 && cp <= ends[insertPoint - 1]
    }

    enum class TruncatePosition { END, START, MIDDLE }
}
