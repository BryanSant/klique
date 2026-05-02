package io.github.bryansant.klique

import io.github.bryansant.klique.components.OSC
import io.github.bryansant.klique.components.OSC.ProgressState

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        printUsage()
        return
    }

    System.setProperty("klique.keepProgress", "true");
    val defaultInk = InkFile.load()

    when (args[0].lowercase()) {
        "say" -> {
            val message = args.drop(1).joinToString(" ")
            val parsed = message.parseMarkup()
            val output = if (defaultInk != null) InkFile.applyToText(parsed, defaultInk) else parsed
            println(output.trim())
        }
        "spin" -> {
            when (args.getOrNull(1)?.lowercase()) {
                null -> OSC.emitOsc94(System.out, ProgressState.INDETERMINATE)
                "done" -> OSC.emitOsc94(System.out, ProgressState.INACTIVE)
                else -> {
                    System.err.println("spin takes no argument or 'done'")
                    printUsage()
                    return
                }
            }
        }
        "prog" -> {
            val arg = args.getOrNull(1)?.lowercase()
            if (arg == null) {
                System.err.println("prog requires a percentage (0-100) or 'done'")
                printUsage()
                return
            }

            if (arg == "done") {
                OSC.emitOsc94(System.out, ProgressState.INACTIVE)
            } else {
                val value = arg.toIntOrNull()
                if (value == null) {
                    System.err.println("Invalid prog argument: '$arg'")
                    return
                }
                OSC.emitOsc94(System.out, ProgressState.IN_PROGRESS, value.coerceIn(0, 100))
            }
        }
        "title" -> {
            val text = args.drop(1).joinToString(" ")
            if (text.isEmpty()) {
                System.err.println("title requires text")
                printUsage()
                return
            }
            OSC.setSystemTitle(text)
        }
        "notify" -> {
            if (args.size < 3) {
                System.err.println("notify requires <subject> <message>")
                printUsage()
                return
            }
            val subject = args[1]
            val message = args.drop(2).joinToString(" ")
            OSC.sendSystemNotification(subject, message)
        }
        "copy" -> {
            val text = args.drop(1).joinToString(" ")
            if (text.isEmpty()) {
                System.err.println("copy requires text")
                printUsage()
                return
            }
            OSC.copyToClipboard(text)
        }
        "ink" -> {
            val colors = args.drop(1)
            if (colors.isEmpty() || colors.size > 2) {
                System.err.println("ink requires 1 or 2 color arguments")
                printUsage()
                return
            }
            for (spec in colors) {
                if (InkFile.resolveSpec(spec) == null) {
                    System.err.println("Unknown color: '$spec'")
                    return
                }
            }
            InkFile.save(colors)
        }
        else -> {
            System.err.println("Unknown command '${args[0]}'")
            printUsage()
        }
    }
}

private fun printUsage() {
    println("Usage:")
    println("  ink <color> [color]     Set the default color or gradient")
    println("                          Colors: named (red, cyan, …) or hex (#rgb / #rrggbb)")
    println("                          Two colors produce a gradient (hex only)")
    println("  say <message>           Print a Klique markup message")
    println("  spin [done]             Bump the terminal OSC intermediate indicator")
    println("  prog <0-100|done>       Set the terminal OSC progress bar percentage")
    println("  title <text>            Set the terminal window title")
    println("  notify <subject> <msg>  Send a desktop notification")
    println("  copy <text>             Copy text to the clipboard")
}
