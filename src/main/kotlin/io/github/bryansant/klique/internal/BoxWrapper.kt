package io.github.bryansant.klique.internal

import io.github.bryansant.klique.config.BoxConfig

internal data class BoxWrapper(
    val width: Int,
    val height: Int,
    val configuration: BoxConfig,
    val wordWrap: List<Cell>,
    val hLine: String,
    val vLine: String,
    val tLeft: String,
    val tRight: String,
    val bRight: String,
    val bLeft: String,
)
