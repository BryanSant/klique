package io.github.bryansant.klique.components

import io.github.bryansant.klique.config.SpinnerConfig
import io.github.bryansant.klique.spi.ESC
import io.github.bryansant.klique.emitOsc94
import io.github.bryansant.klique.ProgressState
import io.github.bryansant.klique.internal.RGBColor
import io.github.bryansant.klique.internal.utils.AnsiDetector
import io.github.bryansant.klique.internal.utils.StringUtils
import io.github.bryansant.klique.spi.RGBAnsiCode
import io.github.bryansant.klique.style.StyleBuilder
import java.io.PrintStream
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

private const val HIDE_CURSOR_SUFFIX = "[?25l"
private const val SHOW_CURSOR_SUFFIX = "[?25h"
private const val ERASE_TO_EOL_SUFFIX = "[K"

private fun hideCursorSeq() = "$ESC$HIDE_CURSOR_SUFFIX"
private fun showCursorSeq() = "$ESC$SHOW_CURSOR_SUFFIX"
private fun eraseToEolSeq() = "$ESC$ERASE_TO_EOL_SUFFIX"

private fun terminalWidth(): Int = System.getenv("COLUMNS")?.toIntOrNull()?.takeIf { it > 0 } ?: 80

private fun interpolateGradient(from: RGBAnsiCode, to: RGBAnsiCode, frameIdx: Int, frameCount: Int): RGBColor {
    val t = if (frameCount <= 1) 0.0 else frameIdx.toDouble() / (frameCount - 1)
    val r = (from.red() + t * (to.red() - from.red())).toInt()
    val g = (from.green() + t * (to.green() - from.green())).toInt()
    val b = (from.blue() + t * (to.blue() - from.blue())).toInt()
    return RGBColor(r, g, b)
}

class Spinner(
    var label: String = "",
    val config: SpinnerConfig = SpinnerConfig.DEFAULT,
) : Component, AutoCloseable {

    private var currentFrame: Int = 0
    private var started: Boolean = false
    private var stopped: Boolean = false

    // Daemon thread for background spinning (Klique-style)
    @Volatile private var running: Boolean = false
    @Volatile private var paused: Boolean = false
    private var thread: Thread? = null

    companion object {
        private val shutdownHookRegistered = AtomicBoolean(false)

        fun ensureShutdownHook() {
            if (shutdownHookRegistered.compareAndSet(false, true)) {
                Runtime.getRuntime().addShutdownHook(Thread {
                    if (AnsiDetector.ansiEnabled()) {
                        emitOsc94(System.out, ProgressState.INACTIVE)
                        System.out.print(showCursorSeq())
                        System.out.flush()
                    }
                })
            }
        }
    }

    // ── Background-thread mode (for withSpinner { }) ──

    fun start(): Spinner {
        if (running) return this
        running = true
        ensureShutdownHook()
        hideCursor(System.out)
        emitOsc94(System.out, ProgressState.INDETERMINATE)
        thread = Thread {
            var idx = 0
            while (running) {
                if (!paused) {
                    val frameIdx = idx % config.frames.size
                    val frame = config.frames[frameIdx]
                    val from = config.gradientFrom
                    val to = config.gradientTo
                    val styledFrame = when {
                        from != null && to != null -> {
                            val color = interpolateGradient(from, to, frameIdx, config.frames.size)
                            StyleBuilder().appendAndReset(frame, color).toString()
                        }
                        config.color.isNotEmpty() -> StyleBuilder().appendAndReset(frame, *config.color).toString()
                        else -> frame
                    }
                    val cols = terminalWidth()
                    val rawLabel = this.label
                    // +2 = frame glyph + space; truncate so the full line stays within one terminal row
                    val displayLabel = if (rawLabel.length > cols - 2) rawLabel.take(cols - 3) + "…" else rawLabel
                    System.out.print("\r$styledFrame $displayLabel${eraseToEolSeq()}")
                    System.out.flush()
                }
                idx++
                try {
                    Thread.sleep(config.frameDelayMs)
                } catch (e: InterruptedException) {
                    break
                }
            }
                        System.out.print("\r${eraseToEolSeq()}")
            System.out.flush()
            showCursor(System.out)
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
        emitOsc94(System.out, ProgressState.INACTIVE)
    }

    fun stopWithError(): Spinner {
        if (!running) return this
        running = false
        thread?.join(500)
        thread = null
        emitOsc94(System.out, ProgressState.ERROR)
        return this
    }

    fun pause(): Spinner {
        paused = true
        emitOsc94(System.out, ProgressState.PAUSED)
        return this
    }

    fun resume(): Spinner {
        paused = false
        emitOsc94(System.out, ProgressState.INDETERMINATE)
        return this
    }

    override fun close() = stop()

    // ── Manual-tick mode (Clique-style) ──

    fun tick(): Spinner {
        if (!started) {
            started = true
            hideCursor(System.out)
        }
        currentFrame = (currentFrame + 1) % config.frames.size
        this.render()
        return this
    }

    fun stop(stream: PrintStream): Spinner {
        if (stopped) return this
        stopped = true
        emitOsc94(stream, ProgressState.INACTIVE)
        showCursor(stream)
        stream.println()
        stream.flush()
        return this
    }

    fun stopWithError(stream: PrintStream): Spinner {
        if (stopped) return this
        stopped = true
        emitOsc94(stream, ProgressState.ERROR)
        showCursor(stream)
        stream.println()
        stream.flush()
        return this
    }

    fun isStopped(): Boolean = stopped

    // ── Blocking timed spin ──

    fun spin(durationMs: Long): Spinner {
        require(durationMs >= 0) { "Duration cannot be negative" }
        val hook = Thread { emitOsc94(System.out, ProgressState.INACTIVE); showCursor(System.out) }
        Runtime.getRuntime().addShutdownHook(hook)
        hideCursor(System.out)
        emitOsc94(System.out, ProgressState.INDETERMINATE)
        try {
            val end = System.currentTimeMillis() + durationMs
            this.render()
            while (System.currentTimeMillis() < end) {
                currentFrame = (currentFrame + 1) % config.frames.size
                this.render()
                try {
                    TimeUnit.MILLISECONDS.sleep(config.frameDelayMs)
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                    break
                }
            }
        } finally {
            try { Runtime.getRuntime().removeShutdownHook(hook) } catch (_: IllegalStateException) {}
            stop(System.out)
        }
        return this
    }

    override fun get(): String {
        val frame = config.frames[currentFrame]
        val from = config.gradientFrom
        val to = config.gradientTo
        val styledFrame = when {
            from != null && to != null -> {
                val color = interpolateGradient(from, to, currentFrame, config.frames.size)
                StyleBuilder().appendAndReset(frame, color).toString()
            }
            config.color.isNotEmpty() -> StyleBuilder().appendAndReset(frame, *config.color).toString()
            else -> frame
        }
        val builder = StringBuilder(styledFrame)
        if (label.isNotEmpty()) {
            builder.append(StyleBuilder().appendAndReset(" " + StringUtils.parse(label, config.parser)))
        }
        return builder.toString()
    }

    override fun render(stream: PrintStream) {
        stream.print("\r${get()}${eraseToEolSeq()}")
        stream.flush()
    }

    private fun hideCursor(stream: PrintStream) {
        if (AnsiDetector.ansiEnabled()) { stream.print(hideCursorSeq()); stream.flush() }
    }

    private fun showCursor(stream: PrintStream) {
        if (AnsiDetector.ansiEnabled()) { stream.print(showCursorSeq()); stream.flush() }
    }
}

fun <T> withSpinner(
    label: String = "",
    config: SpinnerConfig = SpinnerConfig.DEFAULT,
    block: Spinner.() -> T,
): T {
    Spinner.ensureShutdownHook()
    val spinner = Spinner(label, config).start()
    return try {
        spinner.block()
    } finally {
        spinner.stop()
    }
}
