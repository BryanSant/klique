package io.github.klique

import io.github.kusoroadeolu.clique.Clique as JClique
import io.github.kusoroadeolu.clique.components.IterableProgressBar
import io.github.kusoroadeolu.clique.components.ProgressBar
import io.github.kusoroadeolu.clique.configuration.ProgressBarConfiguration

// ── ProgressBar extensions ────────────────────────────────────────────────────

/**
 * Iterates over this collection while rendering a progress bar to stdout
 * on each element. The bar advances automatically with each call to [action].
 *
 * ```kotlin
 * files.withProgress { file -> process(file) }
 * ```
 */
fun <T> Collection<T>.withProgress(action: (T) -> Unit) {
    for (item in JClique.progressBar(this)) action(item)
}

/**
 * Same as [withProgress] but accepts a custom [ProgressBarConfiguration].
 */
fun <T> Collection<T>.withProgress(config: ProgressBarConfiguration, action: (T) -> Unit) {
    for (item in JClique.progressBar(this, config)) action(item)
}

/**
 * Iterates over this [IterableProgressBar], applying [action] to each element.
 * The progress bar advances automatically.
 */
inline fun <T> IterableProgressBar<T>.forEach(action: (T) -> Unit) {
    for (item in this) action(item)
}

/**
 * Advances this [ProgressBar] by [amount] ticks and returns `this` for chaining.
 * Use instead of [ProgressBar.tick] when you want to batch-advance.
 */
operator fun ProgressBar.plus(amount: Int): ProgressBar = tick(amount)

/** `true` once all ticks have been consumed. */
val ProgressBar.done: Boolean get() = isDone()
