package io.github.bryansant.klique

import io.github.bryansant.klique.components.IterableProgressBar
import io.github.bryansant.klique.components.ProgressBar
import io.github.bryansant.klique.config.ProgressBarConfig

enum class ProgressBarPreset(
    val length: Int,
    val glyphs: String,
    val format: String,
) {
    BLOCKS(40, "█░", ":bar :percent% [:elapsed/:remaining]"),
    LINES(50, "▂▁", ":bar :percent%"),
    BOLD(40, "▰▱", ":bar :percent% | :progress/:total"),
    CLASSIC(50, "#=", "[:bar] :percent% [:elapsed]"),
    DOTS(50, "●○", ":bar :percent%"),
    SMOOTH(40, "█▉▊▋▌▍▎▏ ", ":bar :percent%"),
}

/** Iterates over this collection while rendering a progress bar to stdout on each element. */
fun <T> Collection<T>.withProgress(action: (T) -> Unit) {
    for (item in IterableProgressBar(this)) action(item)
}

/** Same as [withProgress] but accepts a custom [ProgressBarConfig]. */
fun <T> Collection<T>.withProgress(config: ProgressBarConfig, action: (T) -> Unit) {
    for (item in IterableProgressBar(this, config)) action(item)
}

/** Iterates over this [IterableProgressBar], applying [action] to each element. */
inline fun <T> IterableProgressBar<T>.forEach(action: (T) -> Unit) {
    for (item in this) action(item)
}

/** Advances this [ProgressBar] by [amount] ticks and returns `this` for chaining. */
operator fun ProgressBar.plus(amount: Int): ProgressBar = tick(amount)

/** `true` once all ticks have been consumed. */
val ProgressBar.done: Boolean get() = isDone()

fun smoothProgressBar(total: Int, width: Int = 40): ProgressBar =
    ProgressBar(total, ProgressBarConfig.fromPreset(ProgressBarPreset.SMOOTH).apply { length = width }.build())

fun <T> Collection<T>.withSmoothProgress(action: (T) -> Unit) {
    val bar = smoothProgressBar(size)
    for (item in this) { action(item); bar.tick() }
}

fun <T> Collection<T>.withSmoothProgress(width: Int, action: (T) -> Unit) {
    val bar = smoothProgressBar(size, width)
    for (item in this) { action(item); bar.tick() }
}
