package io.github.klique

import io.github.kusoroadeolu.clique.components.Box
import io.github.kusoroadeolu.clique.components.Component
import io.github.kusoroadeolu.clique.components.Frame
import io.github.kusoroadeolu.clique.configuration.FrameAlign
import io.github.kusoroadeolu.clique.configuration.TextAlign

// ── Box extensions ────────────────────────────────────────────────────────────

/** Sets content with left alignment (default). */
fun Box.content(text: String): Box = content(text)

/** Sets content and the text alignment within the box. */
fun Box.content(text: String, align: TextAlign): Box = content(text, align)

// ── Frame extensions ──────────────────────────────────────────────────────────

/** Nests a [Component] with the given [align] (default LEFT). */
fun Frame.nest(component: Component, align: FrameAlign = FrameAlign.LEFT): Frame = nest(component, align)

/** Nests a rendered string with the given [align] (default LEFT). */
fun Frame.nest(content: String, align: FrameAlign = FrameAlign.LEFT): Frame = nest(content, align)
