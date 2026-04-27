package io.github.klique

import java.io.PrintStream

/**
 * A progress bar that uses sub-character block glyphs (▏▎▍▌▋▊▉█) for smooth
 * visual progression at 8× finer resolution than a single-block bar.
 *
 * The bar renders live to stdout on each [tick], overwriting the current line.
 * A newline is printed when [done] becomes true.
 *
 * ```kotlin
 * val bar = smoothProgressBar(files.size)
 * for (file in files) {
 *     process(file)
 *     bar.tick()
 * }
 *
 * // or with a collection:
 * files.withSmoothProgress { process(it) }
 * ```
 */
class SmoothProgressBar(val total: Int, val width: Int = 40) {

    private var ticks = 0
    private val out: PrintStream = System.out

    init { display() }

    fun tick(): SmoothProgressBar = advance(1)

    fun tick(amount: Int): SmoothProgressBar = advance(amount)

    operator fun plus(amount: Int): SmoothProgressBar = advance(amount)

    val done: Boolean get() = ticks >= total

    private fun advance(amount: Int): SmoothProgressBar {
        ticks = (ticks + amount).coerceAtMost(total)
        display()
        return this
    }

    private fun display() {
        val pct = if (total > 0) ticks * 100 / total else 100
        out.print("\r${buildBar()} %3d%%".format(pct))
        if (done) out.println()
        out.flush()
    }

    private fun buildBar(): String {
        val filled = if (total > 0) ticks.toDouble() / total * width else width.toDouble()
        val full = filled.toInt().coerceIn(0, width)
        val subIdx = ((filled - full) * 8).toInt()
        return buildString {
            repeat(full) { append(FULL_BLOCK) }
            if (full < width && subIdx > 0) append(GLYPHS[subIdx - 1])
        }
    }

    companion object {
        private const val GLYPHS = "▏▎▍▌▋▊▉"
        private const val FULL_BLOCK = '█'
    }
}

fun smoothProgressBar(total: Int, width: Int = 40): SmoothProgressBar =
    SmoothProgressBar(total, width)

/**
 * Iterates this collection while rendering a smooth block progress bar to stdout.
 * The bar advances after each [action] completes.
 */
fun <T> Collection<T>.withSmoothProgress(action: (T) -> Unit) {
    val bar = SmoothProgressBar(size)
    for (item in this) { action(item); bar.tick() }
}

/**
 * Same as [withSmoothProgress] but with a custom bar [width] in characters.
 */
fun <T> Collection<T>.withSmoothProgress(width: Int, action: (T) -> Unit) {
    val bar = SmoothProgressBar(size, width)
    for (item in this) { action(item); bar.tick() }
}
