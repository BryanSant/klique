package io.github.bryansant.klique

import io.github.bryansant.klique.components.Table

/** Adds a row from any mix of values; each is converted via [toString]. */
fun Table.row(vararg cells: Any?): Table = row(*cells.map { it?.toString() ?: "" }.toTypedArray())

/** Adds all collections as individual rows. */
fun Table.rows(vararg rows: Collection<String>): Table = apply { rows.forEach { row(it) } }

/** Appends a row using Kotlin's `+=` operator. */
operator fun Table.plusAssign(row: Collection<String>) { this.row(row) }
