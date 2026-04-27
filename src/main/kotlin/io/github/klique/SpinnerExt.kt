package io.github.klique

import java.io.PrintStream
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Defines the animation frames for a [Spinner].
 *
 * Custom styles can be created: `SpinnerStyle(listOf("|", "/", "-", "\\"))`
 */
data class SpinnerStyle(val frames: List<String>) {
    companion object {
        val BRAILLE = SpinnerStyle(listOf("⠋", "⠙", "⠹", "⠸", "⠼", "⠴", "⠦", "⠧", "⠇", "⠏"))
    }
}

/**
 * Animated terminal spinner for non-deterministic progress.
 *
 * ```kotlin
 * withSpinner("Deploying") {
 *     deploy()
 *     label = "Verifying…"
 *     verify()
 * }
 * ```
 *
 * The cursor is hidden while the spinner is running and restored on [stop].
 * A JVM shutdown hook ensures the cursor is restored even if the process exits
 * while a spinner is active.
 */
class Spinner(label: String, val style: SpinnerStyle = SpinnerStyle.BRAILLE) : AutoCloseable {

    @Volatile var label: String = label
    @Volatile private var running = false
    private var thread: Thread? = null
    private val out: PrintStream = System.out

    fun start(): Spinner {
        if (running) return this
        running = true
        ensureShutdownHook()
        hideCursor()
        thread = Thread {
            var frameIndex = 0
            while (running) {
                val frame = style.frames[frameIndex % style.frames.size]
                out.print("\r$frame ${this.label}[K")
                out.flush()
                frameIndex++
                try {
                    Thread.sleep(80)
                } catch (e: InterruptedException) {
                    break
                }
            }
            out.print("\r[K")
            out.flush()
            showCursor()
        }.also {
            it.isDaemon = true
            it.start()
        }
        return this
    }

    fun stop() {
        if (!running) return
        running = false
        thread?.join(500)
        thread = null
    }

    override fun close() = stop()

    private fun hideCursor() { out.print("[?25l"); out.flush() }
    private fun showCursor() { out.print("[?25h"); out.flush() }

    companion object {
        private val shutdownHookRegistered = AtomicBoolean(false)

        internal fun ensureShutdownHook() {
            if (shutdownHookRegistered.compareAndSet(false, true)) {
                Runtime.getRuntime().addShutdownHook(Thread {
                    System.out.print("[?25h")
                    System.out.flush()
                })
            }
        }
    }
}

/**
 * Runs [block] with an animated spinner displayed on stdout.
 *
 * The [Spinner] instance is the receiver inside [block], so the label can be
 * updated mid-execution: `label = "Almost done…"`
 *
 * Returns the value produced by [block].
 */
fun <T> withSpinner(
    label: String = "",
    style: SpinnerStyle = SpinnerStyle.BRAILLE,
    block: Spinner.() -> T,
): T {
    Spinner.ensureShutdownHook()
    val spinner = Spinner(label, style).start()
    return try {
        spinner.block()
    } finally {
        spinner.stop()
    }
}
