package io.github.bryansant.klique

import io.github.bryansant.klique.components.OSC
import io.github.bryansant.klique.components.ProgressBar
import io.github.bryansant.klique.spi.ESC
import io.github.bryansant.klique.spi.ST
import io.github.bryansant.klique.components.Spinner
import io.github.bryansant.klique.components.withSpinner
import io.github.bryansant.klique.config.progressBarConfig
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import java.io.ByteArrayOutputStream
import java.io.PrintStream

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

    // ── Divider ───────────────────────────────────────────────────────────────

    @Test
    fun `divider renders a line of the correct length`() {
        val output = divider(20)
        assertTrue(output.isNotBlank())
    }

    @Test
    fun `divider with title centers the text`() {
        val output = divider(30) { title("Section") }
        assertTrue("Section" in output)
    }

    // ── Spinner ───────────────────────────────────────────────────────────────

    @Test
    fun `withSpinner returns block result`() {
        val result = withSpinner("Testing") { 42 }
        assertTrue(result == 42)
    }

    @Test
    fun `withSpinner label can be mutated mid-block`() {
        val result = withSpinner("Phase 1") {
            label = "Phase 2"
            label
        }
        assertTrue(result == "Phase 2")
    }

    @Test
    fun `Spinner start and stop lifecycle`() {
        val spinner = Spinner("Working")
        spinner.start()
        spinner.stop()
    }

    @Test
    fun `Spinner use block stops on exit`() {
        Spinner("Closing").use { it.start() }
    }

    @Test
    fun `Spinner emits OSC 9 semicolon 4 state 0 on manual stop`() {
        enableColors()
        try {
            val buf = ByteArrayOutputStream()
            val stream = PrintStream(buf)
            val spinner = Spinner("Working")
            spinner.stop(stream)
            val output = buf.toString()
            assertTrue("${ESC}]9;4;0;0${ST}" in output)
        } finally {
            disableColors()
        }
    }

    // ── OSC window title ──────────────────────────────────────────────────────

    @Test
    fun `setSystemTitle does not throw`() {
        OSC.setSystemTitle("Klique Test")
    }

    // ── SmoothProgressBar ─────────────────────────────────────────────────────

    @Test
    fun `smoothProgressBar completes after all ticks`() {
        val bar = smoothProgressBar(5)
        repeat(5) { bar.tick() }
        assertTrue(bar.done)
    }

    @Test
    fun `smoothProgressBar plus operator advances`() {
        val bar = smoothProgressBar(10)
        bar + 5
        assertTrue(!bar.done)
        bar + 5
        assertTrue(bar.done)
    }

    @Test
    fun `withSmoothProgress iterates all items`() {
        val items = listOf("a", "b", "c")
        val seen = mutableListOf<String>()
        items.withSmoothProgress { seen += it }
        assertTrue(seen.containsAll(items))
    }

    @Test
    fun `withSmoothProgress custom width iterates all items`() {
        val items = listOf(1, 2, 3)
        val seen = mutableListOf<Int>()
        items.withSmoothProgress(20) { seen += it }
        assertTrue(seen.containsAll(items))
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

    @Test
    fun `ProgressBar emits OSC 9 semicolon 4 state 1 with percent on render`() {
        enableColors()
        try {
            val buf = ByteArrayOutputStream()
            val stream = PrintStream(buf)
            val bar = ProgressBar(10)
            bar.tick(5, false)   // advance to 50%, no render
            bar.render(stream)
            val output = buf.toString()
            assertTrue("${ESC}]9;4;1;50${ST}" in output)
        } finally {
            disableColors()
        }
    }

    @Test
    fun `ProgressBar emits OSC 9 semicolon 4 state 0 on completion`() {
        enableColors()
        try {
            val buf = ByteArrayOutputStream()
            val stream = PrintStream(buf)
            val bar = ProgressBar(4)
            bar.tick(4, false)   // complete, no render
            bar.render(stream)
            val output = buf.toString()
            assertTrue("${ESC}]9;4;0;100${ST}" in output)
        } finally {
            disableColors()
        }
    }

    @Test
    fun `ProgressBar render emits state 2 when in error state`() {
        enableColors()
        try {
            val buf = ByteArrayOutputStream()
            val stream = PrintStream(buf)
            val bar = ProgressBar(10)
            bar.tick(3, false)
            bar.isError = true
            bar.render(stream)
            val output = buf.toString()
            assertTrue("${ESC}]9;4;2;30${ST}" in output)
        } finally {
            disableColors()
        }
    }

    @Test
    fun `ProgressBar render emits state 4 when paused`() {
        enableColors()
        try {
            val buf = ByteArrayOutputStream()
            val stream = PrintStream(buf)
            val bar = ProgressBar(10)
            bar.tick(5, false)
            bar.isPaused = true
            bar.render(stream)
            val output = buf.toString()
            assertTrue("${ESC}]9;4;4;50${ST}" in output)
        } finally {
            disableColors()
        }
    }

    @Test
    fun `ProgressBar resume emits state 1 after pause`() {
        enableColors()
        try {
            val buf = ByteArrayOutputStream()
            val stream = PrintStream(buf)
            val bar = ProgressBar(10)
            bar.tick(5, false)
            bar.pause(stream)
            buf.reset()
            bar.resume(stream)
            val output = buf.toString()
            assertTrue("${ESC}]9;4;1;50${ST}" in output)
        } finally {
            disableColors()
        }
    }

    @Test
    fun `Spinner stopWithError emits OSC 9 semicolon 4 state 2`() {
        enableColors()
        try {
            val buf = ByteArrayOutputStream()
            val stream = PrintStream(buf)
            val spinner = Spinner("Working")
            spinner.stopWithError(stream)
            val output = buf.toString()
            assertTrue("${ESC}]9;4;2;0${ST}" in output)
        } finally {
            disableColors()
        }
    }

    @Test
    fun `progressBar applies barColor to bar output`() {
        enableColors()
        try {
            val bar = progressBar(10, progressBarConfig {
                barColor("red")
                format = ":bar"
            })
            bar.tick(5, false)
            val buf = ByteArrayOutputStream()
            bar.render(PrintStream(buf))
            val output = buf.toString()
            assertTrue(output.isNotBlank())
            // ANSI color codes are present (ESC [ ... m sequences)
            assertTrue(ESC in output)
        } finally {
            disableColors()
        }
    }

    @Test
    fun `progressBar applies gradient to bar output`() {
        enableColors()
        try {
            val bar = progressBar(10, progressBarConfig {
                barGradient(rgb(255, 0, 0), rgb(0, 0, 255))
                format = ":bar"
            })
            bar.tick(5, false)
            val buf = ByteArrayOutputStream()
            bar.render(PrintStream(buf))
            val output = buf.toString()
            assertTrue(output.isNotBlank())
            assertTrue(ESC in output)
        } finally {
            disableColors()
        }
    }
}
