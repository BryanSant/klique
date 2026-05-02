package io.github.bryansant.klique

import io.github.bryansant.klique.parser.PredefinedStyleContext
import io.github.bryansant.klique.parser.StyleContext
import io.github.bryansant.klique.spi.RGBAnsiCode
import io.github.bryansant.klique.style.Ink
import java.io.File

internal object InkFile {

    private fun sharedMemDir(): String =
        if (System.getProperty("os.name").contains("Mac", ignoreCase = true)) "/Volumes/RAMDisk"
        else "/dev/shm"

    fun inkFilePath(): String? {
        val ppid = ProcessHandle.current().parent().map { it.pid() }.orElse(null) ?: return null
        return "${sharedMemDir()}/$ppid.klique.ink"
    }

    fun save(colors: List<String>) {
        val path = inkFilePath() ?: return
        File(path).writeBytes(colors.joinToString("\n").toByteArray())
    }

    fun load(): List<String>? {
        val path = inkFilePath() ?: return null
        val file = File(path)
        if (!file.exists()) return null
        return file.readText().lines().filter { it.isNotBlank() }.takeIf { it.isNotEmpty() }
    }

    fun resolveSpec(spec: String) = PredefinedStyleContext.get(spec, StyleContext.NONE)

    fun applyToText(text: String, colors: List<String>): String {
        if (colors.isEmpty()) return text
        if (colors.size >= 2) {
            val from = resolveSpec(colors[0]) as? RGBAnsiCode
            val to = resolveSpec(colors[1]) as? RGBAnsiCode
            if (from != null && to != null) return Ink().gradient(from, to).on(text)
        }
        val code = resolveSpec(colors[0]) ?: return text
        return Ink().of(code).on(text)
    }
}
