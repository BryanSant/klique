package io.github.bryansant.klique

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        printUsage()
        return
    }

    val defaultInk = InkFile.load()

    when (args[0].lowercase()) {
        "say" -> {
            val message = args.drop(1).joinToString(" ")
            val parsed = message.parseMarkup()
            val output = if (defaultInk != null) InkFile.applyToText(parsed, defaultInk) else parsed
            print(output.trim())
        }
        "spin" -> {
            when (args.getOrNull(1)?.lowercase()) {
                null -> emitOsc94(System.out, 3)
                "done" -> emitOsc94(System.out, 0)
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
                emitOsc94(System.out, 0)
            } else {
                val value = arg.toIntOrNull()
                if (value == null) {
                    System.err.println("Invalid prog argument: '$arg'")
                    return
                }
                emitOsc94(System.out, 1, value.coerceIn(0, 100))
            }
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
}
