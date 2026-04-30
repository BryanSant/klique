package io.github.bryansant.klique.spi

import io.github.bryansant.klique.internal.RGBColor

interface RGBAnsiCode : AnsiCode {
    fun red(): Int
    fun green(): Int
    fun blue(): Int
    fun isBackground(): Boolean

    companion object {
        fun parseHex(spec: String): RGBAnsiCode? {
            if (!spec.startsWith("#")) return null
            val hex = spec.removePrefix("#").lowercase()
            return when (hex.length) {
                3 -> {
                    val r = "${hex[0]}${hex[0]}".toIntOrNull(16) ?: return null
                    val g = "${hex[1]}${hex[1]}".toIntOrNull(16) ?: return null
                    val b = "${hex[2]}${hex[2]}".toIntOrNull(16) ?: return null
                    RGBColor(r, g, b)
                }
                6 -> {
                    val r = hex.substring(0, 2).toIntOrNull(16) ?: return null
                    val g = hex.substring(2, 4).toIntOrNull(16) ?: return null
                    val b = hex.substring(4, 6).toIntOrNull(16) ?: return null
                    RGBColor(r, g, b)
                }
                else -> null
            }
        }
    }
}
