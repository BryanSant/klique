package io.github.bryansant.klique

import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

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
