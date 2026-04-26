package io.github.klique

import io.github.kusoroadeolu.clique.Clique as JClique
import io.github.kusoroadeolu.clique.spi.RGBAnsiCode
import io.github.kusoroadeolu.clique.style.Ink

// ── Ink terminal: apply styles to a string ────────────────────────────────────

/** Applies ANSI styles to this string via an [Ink] chain. */
fun String.styled(block: Ink.() -> Ink): String = JClique.ink().block().on(this)

// Standard foreground colors
fun String.black(): String = styled { black() }
fun String.red(): String = styled { red() }
fun String.green(): String = styled { green() }
fun String.yellow(): String = styled { yellow() }
fun String.blue(): String = styled { blue() }
fun String.magenta(): String = styled { magenta() }
fun String.cyan(): String = styled { cyan() }
fun String.white(): String = styled { white() }

// Bright foreground colors
fun String.brightBlack(): String = styled { brightBlack() }
fun String.brightRed(): String = styled { brightRed() }
fun String.brightGreen(): String = styled { brightGreen() }
fun String.brightYellow(): String = styled { brightYellow() }
fun String.brightBlue(): String = styled { brightBlue() }
fun String.brightMagenta(): String = styled { brightMagenta() }
fun String.brightCyan(): String = styled { brightCyan() }
fun String.brightWhite(): String = styled { brightWhite() }

// Background colors
fun String.bgBlack(): String = styled { bgBlack() }
fun String.bgRed(): String = styled { bgRed() }
fun String.bgGreen(): String = styled { bgGreen() }
fun String.bgYellow(): String = styled { bgYellow() }
fun String.bgBlue(): String = styled { bgBlue() }
fun String.bgMagenta(): String = styled { bgMagenta() }
fun String.bgCyan(): String = styled { bgCyan() }
fun String.bgWhite(): String = styled { bgWhite() }

// RGB colors
fun String.rgb(r: Int, g: Int, b: Int): String = styled { rgb(r, g, b) }
fun String.bgRgb(r: Int, g: Int, b: Int): String = styled { bgRgb(r, g, b) }

// Text decorations
fun String.bold(): String = styled { bold() }
fun String.dim(): String = styled { dim() }
fun String.italic(): String = styled { italic() }
fun String.underline(): String = styled { underline() }
fun String.doubleUnderline(): String = styled { doubleUnderline() }
fun String.strikethrough(): String = styled { strikethrough() }
fun String.reverseVideo(): String = styled { reverseVideo() }

/** Wraps this string in an OSC 8 terminal hyperlink. */
fun String.hyperlink(url: String): String = styled { hyperlink(url) }

/** Applies a per-character RGB gradient from [from] to [to] across this string. */
fun String.gradient(from: RGBAnsiCode, to: RGBAnsiCode): String = styled { gradient(from, to) }

/** Looks up a registered named style and applies it. */
fun String.ofStyle(name: String): String = styled { of(name) }

// ── Ink chaining conveniences ─────────────────────────────────────────────────

/** Applies this [Ink] chain to the given string. Alias for [Ink.on]. */
operator fun Ink.invoke(text: String): String = on(text)
