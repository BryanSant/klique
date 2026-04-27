package io.github.bryansant.klique.config

import io.github.bryansant.klique.EasingFunction

data class EasingConfig(
    val function: EasingFunction = EasingFunction.EASE_OUT_QUAD,
    val durationMs: Int = 500,
    val frames: Int = 20,
    val threshold: Int = 5,
) {
    companion object {
        val DEFAULT = EasingConfig()
        val DISABLED = EasingConfig(EasingFunction.LINEAR, durationMs = 0, frames = 1, threshold = -1)
    }

    val frameDelayMs: Long get() = (durationMs / frames).toLong()

    fun shouldEase(tickBy: Int): Boolean {
        if (threshold < 0) return false
        return tickBy >= threshold
    }
}

class EasingConfigBuilder {
    var function: EasingFunction = EasingFunction.EASE_OUT_QUAD
    var durationMs: Int = 500
    var frames: Int = 20
    var threshold: Int = 5

    fun build() = EasingConfig(function, durationMs, frames, threshold)
}

fun easingConfig(block: EasingConfigBuilder.() -> Unit): EasingConfig =
    EasingConfigBuilder().apply(block).build()
