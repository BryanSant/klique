![Supported JVM Versions](https://img.shields.io/badge/JVM-25+-brightgreen.svg?&logo=openjdk)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![No Color](https://img.shields.io/badge/no--color.org-compliant-blue)](https://no-color.org)

# Klique

A pure Kotlin terminal UI library inspired by [Clique](https://github.com/kusoroadeolu/Clique).

Klique provides idiomatic Kotlin ergonomics for building beautiful CLIs: trailing-lambda builders, extension functions on `String`, operator overloads, and `Collection` extensions.

---

## Highlights

- **Trailing-lambda builders** — `table("Name", "Age") { row("Alice", "25") }` instead of chained Java calls
- **String extension styling** — `"Error".red().bold()` directly on any string
- **RGB & Gradients** — Full 24-bit color and per-character gradient rendering via Clique
- **no-color.org compliant** — Respects the `NO_COLOR` environment variable
- **GraalVM compatible** — No reflection; works with native image compilation
- **Better Unicode emoji support** — Compliant handling of most Unicode emojis

---

## Installation

### Gradle (Kotlin DSL)

```kotlin
dependencies {
    implementation("io.github.bryansant:klique:1.0.5")
}
```

---

## Features

### Markup Parser

Parse Clique markup tags directly on any string:

```kotlin
"[red, bold]Error:[/] Something went wrong".parseMarkup()

// Print directly to stdout
"[green]Success![/]".printMarkup()
```

### Ink — String Extensions

Apply ANSI styles directly to strings with chainable extension functions:

```kotlin
"Error".red().bold()
"Warning".yellow()
"OK".green().underline()

// Custom RGB color
"Pretty".rgb(255, 105, 180)

// Gradient across characters
val pink = rgb(255, 105, 180)
val blue = rgb(100, 149, 237)
"Gradient text".gradient(pink, blue)
```

For reusable style combinations, use the `ink` builder and invoke it as a function:

```kotlin
val boldRed = ink { bold().red() }
val boldYellow = ink { bold().yellow() }

boldRed("Error")    // bold + red
boldYellow("Warn")  // bold + yellow
```

Or use `styled` for an inline block:

```kotlin
"warning".styled { yellow().bold() }
```

### StyleBuilder

Compose multi-segment styled strings:

```kotlin
val output = buildStyledString {
    append("Status: ")
    appendReset("OK", green)
}
```

### Themes

Register a built-in color theme and use its named colors in markup:

```kotlin
registerTheme("catppuccin-mocha")
"[ctp_mauve]Styled with Catppuccin![/]".printMarkup()
```

```kotlin
registerAvailableThemes()               // register all built-in themes
val themes = findAvailableThemes()      // list them
```

**Built-in themes:** Catppuccin, Dracula, Gruvbox, Nord, Tokyo Night.

### Tables

```kotlin
table("Name", "Age", "Status") {
    row("Alice", "25", "Active")
    row("Bob", "30", "Inactive")
    row(3, 99.5, true)              // any type — converted via toString()
}.render()
```

Operator syntax for adding rows:

```kotlin
val t = table("X", "Y") {}
t += listOf("a", "b")
```

Table types and configuration are passed as leading parameters:

```kotlin
table(TableType.ROUNDED, "Name", "Score") {
    row("Alice", "100")
}
```

### Boxes

Single-cell boxes with text wrapping:

```kotlin
box {
    dimensions(40, 10)
    content("Your message here")
}
```

```kotlin
box(BoxType.DOUBLE) {
    dimensions(50, 5)
    content("Double-bordered box", TextAlign.CENTER)
}
```

### Tree

Display hierarchical data using `branch` and `leaf`:

```kotlin
tree("project/") {
    branch("src/") {
        leaf("Main.kt")
        leaf("App.kt")
    }
    branch("test/") {
        leaf("MainTest.kt")
    }
    leaf("build.gradle.kts")
}.render()
```

> `branch` applies a nested block and returns the parent node so you can keep building. `leaf` adds a terminal child and also returns the parent.

### ItemList

Symbol-driven lists with recursive nesting:

```kotlin
itemList {
    item("[green]✓[/]", "Auth service")
    item("[yellow]~[/]", "Notifications — in review") {
        item("!", "Waiting on design sign-off")
        item("!", "API spec pending")
    }
}.render()
```

### Frames

Layout container that stacks Clique components vertically inside a border:

```kotlin
val t = table("Name", "Score") { row("Alice", "100") }

frame {
    title("[bold]Dashboard[/]")
    nest(t)
    nest("Footer text")
}.also { print(it) }
```

### Progress Bars

Wrap any collection with `withProgress` — the bar advances automatically:

```kotlin
files.withProgress { file ->
    process(file)
}
```

Manual tick control with the `+` operator:

```kotlin
val bar = progressBar(100)
bar + 10    // advance by 10
if (bar.done) println("Complete!")
```

Iterate an `IterableProgressBar` directly:

```kotlin
progressBar(files).forEach { file ->
    process(file)
}
```

### Smooth Progress Bars

A smoother alternative to the standard progress bar, rendering fractional blocks for higher-fidelity progress tracking:

```kotlin
files.withSmoothProgress { file ->
    process(file)
}
```

Manual tick control:

```kotlin
val bar = smoothProgressBar(100)
bar + 10
if (bar.done) println("Complete!")
```

### Spinners

Show indeterminate progress with a terminal spinner. The spinner runs automatically on a background thread while your block executes:

```kotlin
withSpinner("Building project...") {
    // You can update the label dynamically
    label = "Resolving dependencies..."
    performTask()
}
```

Manual lifecycle control:

```kotlin
val spinner = Spinner("Working")
spinner.start()
// do work...
spinner.stop()
```

### Window Title

Set the terminal window or tab title via OSC 2. Supported by most modern terminal emulators.

```kotlin
setWindowTitle("Build — my-project")
```

---

> **Thread safety:** Style registration/lookup and config objects (once built) are thread-safe. All other components are not — avoid sharing instances across threads.

---

## License

MIT License