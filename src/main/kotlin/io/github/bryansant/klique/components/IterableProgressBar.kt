package io.github.bryansant.klique.components

import io.github.bryansant.klique.config.ProgressBarConfig
import java.io.PrintStream

class IterableProgressBar<T>(
    collection: Collection<T>,
    config: ProgressBarConfig = ProgressBarConfig.DEFAULT,
) : Iterable<T> {

    private val iterator: Iterator<T> = collection.iterator()
    private var consumed = false
    internal val progressBar: ProgressBar = ProgressBar(collection.size, config)
    private var stream: PrintStream = System.out

    override fun iterator(): Iterator<T> {
        if (consumed) throw IllegalStateException("IterableProgressBar can only be iterated once")
        consumed = true
        return ProgressBarIterator(this)
    }

    fun printStream(stream: PrintStream): IterableProgressBar<T> {
        this.stream = stream
        return this
    }

    fun isDone(): Boolean = progressBar.isDone()

    private class ProgressBarIterator<T>(
        private val ipb: IterableProgressBar<T>,
    ) : Iterator<T> {
        override fun hasNext(): Boolean = ipb.iterator.hasNext()
        override fun next(): T {
            val item = ipb.iterator.next()
            ipb.progressBar.tick(render = false)
            ipb.progressBar.render(ipb.stream)
            return item
        }
    }
}
