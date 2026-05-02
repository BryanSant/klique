package io.github.bryansant.klique.spi

interface RGBAnsiCode : AnsiCode {
    fun red(): Int
    fun green(): Int
    fun blue(): Int
    fun isBackground(): Boolean
}
