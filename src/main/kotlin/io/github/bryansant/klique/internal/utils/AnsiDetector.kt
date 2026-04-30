package io.github.bryansant.klique.internal.utils

internal object AnsiDetector {

    private const val TERM = "TERM"
    private const val PLAIN = "plain"
    private const val DUMB = "dumb"
    private const val NO_COLOR = "NO_COLOR"
    private const val COLOR_PROP = "klique.color"
    private const val ALWAYS = "always"
    private const val NEVER = "never"
    private const val OS_NAME = "os.name"
    private const val WIN = "win"
    private const val CLI_COLOR_FORCE = "CLICOLOR_FORCE"
    private const val COLOR_TERM = "COLORTERM"
    private const val WT_SESSION = "WT_SESSION"
    private const val FORCE_COLOR = "FORCE_COLOR"

    @Volatile
    private var ansiEnabled: Boolean = autoDetect()

    fun refresh() {
        ansiEnabled = autoDetect()
    }

    fun ansiEnabled(): Boolean = ansiEnabled

    fun enableColors() {
        System.setProperty(COLOR_PROP, ALWAYS)
        ansiEnabled = true
    }

    fun disableColors() {
        System.setProperty(COLOR_PROP, NEVER)
        ansiEnabled = false
    }

    private fun autoDetect(): Boolean {
        val noColor = System.getenv(NO_COLOR)
        if (!noColor.isNullOrEmpty()) return false

        val cliqueColor = System.getProperty(COLOR_PROP)
        if (cliqueColor == ALWAYS) return true
        if (cliqueColor == NEVER) return false

        val cliColorForce = System.getenv(CLI_COLOR_FORCE)
        if (!cliColorForce.isNullOrEmpty() && cliColorForce != "0") return true

        val forceColor = System.getenv(FORCE_COLOR)
        if (!forceColor.isNullOrEmpty()) return true

        val colorTerm = System.getenv(COLOR_TERM)
        if (colorTerm != null) return true

        val os = System.getProperty(OS_NAME, "").lowercase()
        if (System.console() == null && !os.contains(WIN)) return false

        val term = System.getenv(TERM)
        if (term == null) {
            if (System.getenv(WT_SESSION) != null) return true
            return os.contains(WIN)
        }

        return !term.equals(DUMB, ignoreCase = true) &&
            !term.equals(PLAIN, ignoreCase = true)
    }
}
