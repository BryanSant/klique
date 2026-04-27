package io.github.bryansant.klique.spi

interface CliqueTheme {
    fun themeName(): String
    fun styles(): Map<String, AnsiCode>
    fun author(): String = ""
    fun url(): String = ""
}
