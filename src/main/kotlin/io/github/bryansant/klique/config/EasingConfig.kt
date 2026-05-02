package io.github.bryansant.klique.config

import io.github.bryansant.klique.config.EasingConfig.EasingFunction
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

data class EasingConfig(
    val function: EasingFunction = EasingFunction.EASE_OUT_QUAD,
    val durationMs: Int = 500,
    val frames: Int = 20,
    val threshold: Int = 5,
) {
    enum class EasingFunction {
        LINEAR {
            override fun apply(t: Double) = t
        },
        EASE_IN_SINE {
            override fun apply(t: Double) = 1 - cos((t * Math.PI) / 2)
        },
        EASE_OUT_SINE {
            override fun apply(t: Double) = sin((t * Math.PI) / 2)
        },
        EASE_IN_OUT_SINE {
            override fun apply(t: Double) = -(cos(Math.PI * t) - 1) / 2
        },
        EASE_IN_QUAD {
            override fun apply(t: Double) = t * t
        },
        EASE_OUT_QUAD {
            override fun apply(t: Double) = 1 - (1 - t) * (1 - t)
        },
        EASE_IN_OUT_QUAD {
            override fun apply(t: Double) = if (t < 0.5) 2 * t * t else 1 - (-2 * t + 2).pow(2) / 2
        },
        EASE_IN_CUBIC {
            override fun apply(t: Double) = t * t * t
        },
        EASE_OUT_CUBIC {
            override fun apply(t: Double) = 1 - (1 - t).pow(3)
        },
        EASE_IN_OUT_CUBIC {
            override fun apply(t: Double) =
                if (t < 0.5) 4 * t * t * t else 1 - (-2 * t + 2).pow(3) / 2
        };

        abstract fun apply(t: Double): Double
    }

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
