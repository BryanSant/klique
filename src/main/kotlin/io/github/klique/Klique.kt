@file:JvmName("Klique")

package io.github.klique

import io.github.kusoroadeolu.clique.Clique as JClique
import io.github.kusoroadeolu.clique.components.Box
import io.github.kusoroadeolu.clique.components.Frame
import io.github.kusoroadeolu.clique.components.IterableProgressBar
import io.github.kusoroadeolu.clique.components.ItemList
import io.github.kusoroadeolu.clique.components.ProgressBar
import io.github.kusoroadeolu.clique.components.Table
import io.github.kusoroadeolu.clique.components.Tree
import io.github.kusoroadeolu.clique.configuration.BoxConfiguration
import io.github.kusoroadeolu.clique.configuration.BoxType
import io.github.kusoroadeolu.clique.configuration.FrameConfiguration
import io.github.kusoroadeolu.clique.configuration.ItemListConfiguration
import io.github.kusoroadeolu.clique.configuration.ProgressBarConfiguration
import io.github.kusoroadeolu.clique.configuration.TableConfiguration
import io.github.kusoroadeolu.clique.configuration.TableType
import io.github.kusoroadeolu.clique.configuration.TreeConfiguration
import io.github.kusoroadeolu.clique.spi.AnsiCode
import io.github.kusoroadeolu.clique.spi.CliqueTheme
import io.github.kusoroadeolu.clique.spi.RGBAnsiCode
import io.github.kusoroadeolu.clique.style.Ink
import io.github.kusoroadeolu.clique.style.StyleBuilder
import java.util.Optional

// ── Color helpers ─────────────────────────────────────────────────────────────

fun rgb(r: Int, g: Int, b: Int): RGBAnsiCode = JClique.rgb(r, g, b)
fun bgRgb(r: Int, g: Int, b: Int): RGBAnsiCode = JClique.rgb(r, g, b, true)

// ── Ink ───────────────────────────────────────────────────────────────────────

fun ink(block: Ink.() -> Ink = { this }): Ink = JClique.ink().block()

// ── Table ─────────────────────────────────────────────────────────────────────
//
// Clique requires calling .headers() on PendingTable before rows can be added.
// These overloads accept headers as a vararg leading parameter so trailing-lambda
// syntax still works:  table("Name", "Age") { row("Alice", "25") }

fun table(vararg headers: String, block: Table.() -> Unit = {}): Table =
    JClique.table().headers(*headers).apply(block)

fun table(type: TableType, vararg headers: String, block: Table.() -> Unit = {}): Table =
    JClique.table(type).headers(*headers).apply(block)

fun table(config: TableConfiguration, vararg headers: String, block: Table.() -> Unit = {}): Table =
    JClique.table(config).headers(*headers).apply(block)

fun table(type: TableType, config: TableConfiguration, vararg headers: String, block: Table.() -> Unit = {}): Table =
    JClique.table(type, config).headers(*headers).apply(block)

// ── Box ──────────────────────────────────────────────────────────────────────

fun box(block: Box.() -> Unit): String = JClique.box().apply(block).get()
fun box(type: BoxType, block: Box.() -> Unit): String = JClique.box(type).apply(block).get()
fun box(config: BoxConfiguration, block: Box.() -> Unit): String = JClique.box(config).apply(block).get()

// ── Frame ─────────────────────────────────────────────────────────────────────

fun frame(block: Frame.() -> Unit): String = JClique.frame().apply(block).get()
fun frame(type: BoxType, block: Frame.() -> Unit): String = JClique.frame(type).apply(block).get()
fun frame(config: FrameConfiguration, block: Frame.() -> Unit): String = JClique.frame(config).apply(block).get()

// ── Tree ─────────────────────────────────────────────────────────────────────

fun tree(label: String, block: Tree.() -> Unit = {}): Tree =
    JClique.tree(label).apply(block)

fun tree(label: String, config: TreeConfiguration, block: Tree.() -> Unit = {}): Tree =
    JClique.tree(label, config).apply(block)

// ── ItemList ─────────────────────────────────────────────────────────────────

fun itemList(block: ItemList.() -> Unit): ItemList =
    JClique.list().apply(block)

fun itemList(config: ItemListConfiguration, block: ItemList.() -> Unit): ItemList =
    JClique.list(config).apply(block)

// ── StyleBuilder ─────────────────────────────────────────────────────────────

fun buildStyledString(block: StyleBuilder.() -> Unit): String =
    JClique.styleBuilder().apply(block).toString()

// ── ProgressBar ───────────────────────────────────────────────────────────────

fun progressBar(total: Int): ProgressBar = JClique.progressBar(total)
fun progressBar(total: Int, config: ProgressBarConfiguration): ProgressBar = JClique.progressBar(total, config)
fun <T> progressBar(items: Collection<T>): IterableProgressBar<T> = JClique.progressBar(items)
fun <T> progressBar(items: Collection<T>, config: ProgressBarConfiguration): IterableProgressBar<T> =
    JClique.progressBar(items, config)

// ── Global configuration ──────────────────────────────────────────────────────

fun disableColors() = JClique.disableCliqueColors()
fun enableColors() = JClique.enableCliqueColors()

fun registerStyle(name: String, code: AnsiCode) = JClique.registerStyle(name, code)
fun registerStyles(codes: Map<String, AnsiCode>) = JClique.registerStyles(codes)
fun registerTheme(name: String) = JClique.registerTheme(name)
fun registerTheme(theme: CliqueTheme) = JClique.registerTheme(theme)
fun registerThemes(vararg names: String) = JClique.registerThemes(*names)
fun registerAvailableThemes() = JClique.registerAvailableThemes()
fun findAvailableThemes(): List<CliqueTheme> = JClique.findAvailableThemes()
fun findTheme(name: String): Optional<CliqueTheme> = JClique.findTheme(name)
