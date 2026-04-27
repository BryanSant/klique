package io.github.bryansant.klique.parser

import io.github.bryansant.klique.internal.CompositeColor
import io.github.bryansant.klique.spi.AnsiCode

class StyleContext private constructor(private val styles: Map<String, AnsiCode>) {

    fun get(name: String): AnsiCode? = styles[name]

    companion object {
        val NONE = StyleContext(emptyMap())

        fun from(codes: Map<String, AnsiCode>): StyleContext = builder().add(codes).build()

        fun from(context: StyleContext): StyleContext = builder().add(context).build()

        fun builder(): Builder = Builder()
    }

    class Builder {
        private val styles = mutableMapOf<String, AnsiCode>()

        fun add(name: String, code: AnsiCode): Builder { styles[name] = code; return this }

        fun add(name: String, vararg codes: AnsiCode): Builder {
            styles[name] = CompositeColor(*codes)
            return this
        }

        fun add(name: String, codes: Collection<AnsiCode>): Builder {
            styles[name] = CompositeColor(codes)
            return this
        }

        fun add(map: Map<String, AnsiCode>): Builder { styles.putAll(map); return this }

        fun add(context: StyleContext): Builder { styles.putAll(context.styles); return this }

        fun build(): StyleContext = StyleContext(styles.toMap())
    }

    override fun equals(other: Any?): Boolean =
        other is StyleContext && styles == other.styles

    override fun hashCode(): Int = styles.hashCode()
    override fun toString(): String = "StyleContext[$styles]"
}
