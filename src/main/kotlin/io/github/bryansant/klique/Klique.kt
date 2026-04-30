@file:JvmName("Klique")

package io.github.bryansant.klique

import io.github.bryansant.klique.components.Box
import io.github.bryansant.klique.components.Divider
import io.github.bryansant.klique.components.Frame
import io.github.bryansant.klique.components.IterableProgressBar
import io.github.bryansant.klique.components.ItemList
import io.github.bryansant.klique.components.ProgressBar
import io.github.bryansant.klique.components.Table
import io.github.bryansant.klique.components.Tree
import io.github.bryansant.klique.components.createTable
import io.github.bryansant.klique.config.BoxConfig
import io.github.bryansant.klique.config.DividerConfig
import io.github.bryansant.klique.config.FrameConfig
import io.github.bryansant.klique.config.ItemListConfig
import io.github.bryansant.klique.config.ProgressBarConfig
import io.github.bryansant.klique.config.TableConfig
import io.github.bryansant.klique.config.TreeConfig
import io.github.bryansant.klique.internal.RGBColor
import io.github.bryansant.klique.internal.utils.AnsiDetector
import io.github.bryansant.klique.parser.GlobalStyleRegistry
import io.github.bryansant.klique.parser.MarkupParser
import io.github.bryansant.klique.parser.ParserConfig
import io.github.bryansant.klique.parser.StyleContext
import io.github.bryansant.klique.spi.AnsiCode
import io.github.bryansant.klique.spi.Theme
import io.github.bryansant.klique.spi.RGBAnsiCode
import io.github.bryansant.klique.style.Ink
import io.github.bryansant.klique.style.StyleBuilder

// ── Color helpers ─────────────────────────────────────────────────────────────

fun rgb(r: Int, g: Int, b: Int): RGBAnsiCode = RGBColor(r, g, b)
fun bgRgb(r: Int, g: Int, b: Int): RGBAnsiCode = RGBColor(r, g, b, background = true)

// ── Ink ───────────────────────────────────────────────────────────────────────

fun ink(context: StyleContext = StyleContext.NONE, block: Ink.() -> Ink = { this }): Ink =
    Ink(context).block()

// ── Table ─────────────────────────────────────────────────────────────────────

fun table(
    vararg headers: String,
    config: TableConfig = TableConfig.DEFAULT,
    block: Table.() -> Unit = {},
): Table = createTable(config.tableType, config).headers(*headers).apply(block)

fun table(
    type: TableType,
    vararg headers: String,
    config: TableConfig = TableConfig.DEFAULT,
    block: Table.() -> Unit = {},
): Table = createTable(type, config).headers(*headers).apply(block)

// ── Box ──────────────────────────────────────────────────────────────────────

fun box(config: BoxConfig = BoxConfig.DEFAULT, block: Box.() -> Unit): String =
    Box(config).apply(block).get()

// ── Frame ─────────────────────────────────────────────────────────────────────

fun frame(config: FrameConfig = FrameConfig.DEFAULT, block: Frame.() -> Unit): String =
    Frame(config).apply(block).get()

// ── Tree ─────────────────────────────────────────────────────────────────────

fun tree(label: String, config: TreeConfig = TreeConfig.DEFAULT, block: Tree.() -> Unit = {}): Tree =
    Tree(label, config).apply(block)

// ── ItemList ─────────────────────────────────────────────────────────────────

fun itemList(config: ItemListConfig = ItemListConfig.DEFAULT, block: ItemList.() -> Unit): ItemList =
    ItemList(config).apply(block)

// ── Divider ───────────────────────────────────────────────────────────────────

fun divider(width: Int, config: DividerConfig = DividerConfig.DEFAULT, block: Divider.() -> Unit = {}): String =
    Divider(width, config).apply(block).get()

// ── StyleBuilder ─────────────────────────────────────────────────────────────

fun buildStyledString(block: StyleBuilder.() -> Unit): String =
    StyleBuilder().apply(block).toString()

// ── ProgressBar ───────────────────────────────────────────────────────────────

fun progressBar(total: Int, config: ProgressBarConfig = ProgressBarConfig.DEFAULT): ProgressBar =
    ProgressBar(total, config)

fun <T> progressBar(items: Collection<T>, config: ProgressBarConfig = ProgressBarConfig.DEFAULT): IterableProgressBar<T> =
    IterableProgressBar(items, config)

// ── Parser ────────────────────────────────────────────────────────────────────

fun parser(config: ParserConfig = ParserConfig.DEFAULT): MarkupParser = MarkupParser(config)

// ── Global configuration ──────────────────────────────────────────────────────

fun disableColors() = AnsiDetector.disableColors()
fun enableColors() = AnsiDetector.enableColors()

fun registerStyle(name: String, code: AnsiCode) = GlobalStyleRegistry.registerStyle(name, code)
fun registerStyles(codes: Map<String, AnsiCode>) = GlobalStyleRegistry.registerStyles(codes)

private val registeredThemes = mutableListOf<Theme>()

fun registerTheme(theme: Theme) {
    registeredThemes.add(theme)
    GlobalStyleRegistry.registerStyles(theme.styles())
}

fun findAvailableThemes(): List<Theme> = registeredThemes.toList()

fun findTheme(name: String): Theme? = registeredThemes.find { it.themeName() == name }
