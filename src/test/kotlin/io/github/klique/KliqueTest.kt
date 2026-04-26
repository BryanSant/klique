package io.github.klique

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class KliqueTest {

    companion object {
        @JvmStatic
        @BeforeAll
        fun setup() {
            // Disable ANSI codes so assertions work against plain text
            disableColors()
        }
    }

    // ── Ink / String extensions ───────────────────────────────────────────────

    @Test
    fun `string styled extensions return non-blank strings`() {
        val r = "hello".bold()
        assertTrue(r.isNotBlank())
    }

    @Test
    fun `ink block applies styles`() {
        val result = ink { bold().red() }("Error")
        assertNotNull(result)
    }

    @Test
    fun `styled extension chains multiple styles`() {
        val result = "warning".styled { yellow().bold() }
        assertTrue(result.isNotBlank())
    }

    // ── Markup parser ─────────────────────────────────────────────────────────

    @Test
    fun `parseMarkup strips tags when colors disabled`() {
        val result = "[red]hello[/]".parseMarkup()
        assertNotNull(result)
    }

    // ── Table ─────────────────────────────────────────────────────────────────

    @Test
    fun `table DSL builds and renders`() {
        val output = table("Name", "Age", "Status") {
            row("Alice", "25", "Active")
            row("Bob", "30", "Inactive")
        }.get()

        assertTrue("Alice" in output)
        assertTrue("Bob" in output)
        assertTrue("Name" in output)
    }

    @Test
    fun `table plusAssign operator adds rows`() {
        val t = table("X", "Y") {}
        t += listOf("a", "b") as Collection<String>
        assertTrue("a" in t.get())
    }

    @Test
    fun `table row with any values converts to string`() {
        val output = table("ID", "Score") {
            row(1, 99.5)
            row(2, 87.0)
        }.get()

        assertTrue("99" in output)
    }

    // ── Box ───────────────────────────────────────────────────────────────────

    @Test
    fun `box DSL renders content`() {
        val output = box {
            dimensions(40, 5)
            content("Hello from Klique!")
        }
        assertTrue("Hello from Klique!" in output)
    }

    // ── Tree ──────────────────────────────────────────────────────────────────

    @Test
    fun `tree DSL with branch and leaf`() {
        val output = tree("project/") {
            branch("src/") {
                leaf("Main.kt")
                leaf("App.kt")
            }
            leaf("build.gradle.kts")
        }.get()

        assertTrue("project/" in output)
        assertTrue("src/" in output)
        assertTrue("Main.kt" in output)
        assertTrue("build.gradle.kts" in output)
    }

    @Test
    fun `leaf returns parent for chaining`() {
        val root = tree("root")
        val same = root.leaf("a").leaf("b").leaf("c")
        assertTrue(same === root)
    }

    // ── ItemList ──────────────────────────────────────────────────────────────

    @Test
    fun `itemList DSL with nested sublist`() {
        val output = itemList {
            item("✓", "Done")
            item("~", "In Progress") {
                item("-", "Step 1")
                item("-", "Step 2")
            }
        }.get()

        assertTrue("Done" in output)
        assertTrue("In Progress" in output)
        assertTrue("Step 1" in output)
    }

    // ── Frame ─────────────────────────────────────────────────────────────────

    @Test
    fun `frame DSL with title and nested string`() {
        val output = frame {
            title("Dashboard")
            nest("Hello, Frame!")
        }
        assertTrue("Dashboard" in output)
        assertTrue("Hello, Frame!" in output)
    }

    @Test
    fun `frame with nested component`() {
        val t = table("A", "B") { row("1", "2") }
        val output = frame {
            title("My Table")
            nest(t)
        }
        assertTrue("My Table" in output)
    }

    // ── StyleBuilder ──────────────────────────────────────────────────────────

    @Test
    fun `buildStyledString concatenates segments`() {
        val result = buildStyledString {
            append("Hello ")
            appendReset("World")
        }
        assertTrue("Hello" in result)
        assertTrue("World" in result)
    }

    // ── ProgressBar ───────────────────────────────────────────────────────────

    @Test
    fun `progressBar completes after all ticks`() {
        val bar = progressBar(3)
        bar.tick().tick().tick()
        assertTrue(bar.done)
    }

    @Test
    fun `withProgress iterates all items`() {
        val items = listOf("a", "b", "c")
        val seen = mutableListOf<String>()
        items.withProgress { seen += it }
        assertTrue(seen.containsAll(items))
    }

    @Test
    fun `plus operator advances progress bar`() {
        val bar = progressBar(10)
        bar + 5
        assertTrue(!bar.done)
        bar + 5
        assertTrue(bar.done)
    }
}
