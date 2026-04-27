package io.github.bryansant.klique.components

import java.io.PrintStream

interface Component {
    fun get(): String
    fun render() = render(System.out)
    fun render(stream: PrintStream) { stream.println(get()) }
}
