package io.github.bryansant.klique.components

import io.github.bryansant.klique.config.ProgressBarConfig
import io.github.bryansant.klique.internal.Constants
import io.github.bryansant.klique.internal.utils.StringUtils
import java.io.PrintStream
import java.util.concurrent.TimeUnit

class ProgressBar(
    val total: Int,
    val config: ProgressBarConfig = ProgressBarConfig.DEFAULT,
) : Component {
    init {
        require(total >= 0) { "Progress bar total cannot be negative" }
    }

    private val creationTime: Long = System.currentTimeMillis()
    internal var currentTick: Int = 0
    internal var isDone: Boolean = false

    fun tick(): ProgressBar = tick(1)

    fun tick(amount: Int): ProgressBar = tick(amount, render = true)

    fun tick(render: Boolean): ProgressBar = tick(1, render)

    fun tick(amount: Int, render: Boolean): ProgressBar {
        require(amount >= 1) { "Tick amount cannot be less than 1" }
        currentTick = currentTick.plus(amount).coerceIn(0, total)
        if (currentTick >= total && !isDone) isDone = true
        if (render) this.render()
        return this
    }

    fun tickAnimated(amount: Int): ProgressBar {
        return if (config.easing.shouldEase(amount)) {
            easeTick(amount)
            this
        } else {
            tick(amount)
        }
    }

    fun complete(render: Boolean = true): ProgressBar = tick(maxOf(1, total - currentTick), render)

    private fun easeTick(amount: Int) {
        val start = currentTick
        val target = (currentTick + amount).coerceIn(0, total)
        val diff = target - start
        val easing = config.easing

        for (i in 1..easing.frames) {
            val t = i / easing.frames.toDouble()
            val eased = easing.function.apply(t)
            currentTick = start + (diff * eased).toInt()
            this.render()
            try {
                TimeUnit.MILLISECONDS.sleep(easing.frameDelayMs)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                break
            }
        }
        currentTick = target
        if (currentTick >= total && !isDone) isDone = true
    }

    fun isDone(): Boolean = isDone

    override fun get(): String {
        val percent = if (total > 0) (currentTick.toDouble() / total * 100).toInt() else 0
        val format = config.getFormatForPercent(percent)
        val glyphs = config.glyphs
        val n = glyphs.length
        val stepsPerCell = n - 1
        val filledSteps = if (total > 0)
            (currentTick.toDouble() / total * config.length * stepsPerCell).toInt()
        else
            config.length * stepsPerCell
        val fullCells = (filledSteps / stepsPerCell).coerceIn(0, config.length)
        val subStep = filledSteps % stepsPerCell

        val bar = buildString {
            repeat(fullCells) { append(glyphs[0]) }
            if (fullCells < config.length) {
                if (subStep > 0) append(glyphs[n - 1 - subStep])
                repeat(config.length - fullCells - if (subStep > 0) 1 else 0) { append(glyphs[n - 1]) }
            }
        }

        val progress = String.valueOf(currentTick).padStart(String.valueOf(total).length)
        val percentStr = percent.toString().padStart(3)
        val elapsed = formatInterval(System.currentTimeMillis() - creationTime)
        val remaining = formatInterval(remainingMs())

        return StringUtils.parse(
            format
                .replace(":bar", bar)
                .replace(":progress", progress)
                .replace(":total", total.toString())
                .replace(":percent", percentStr)
                .replace(":elapsed", elapsed)
                .replace(":remaining", remaining),
            config.parser,
        )
    }

    override fun render(stream: PrintStream) {
        stream.print("\r${get()}")
        if (isDone) stream.println()
        stream.flush()
    }

    private fun formatInterval(ms: Long?): String {
        if (ms == null) return "--:--"
        val seconds = (ms / 1000) % 60
        val minutes = ms / 60000
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun remainingMs(): Long? {
        if (currentTick <= 0 || total <= 0) return null
        val elapsed = System.currentTimeMillis() - creationTime
        val totalTime = elapsed / (currentTick.toDouble() / total)
        return (totalTime - elapsed).toLong()
    }
}

private fun String.Companion.valueOf(n: Int) = n.toString()
