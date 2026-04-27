package io.github.bryansant.klique.internal.utils

import io.github.bryansant.klique.internal.Constants

internal object AnsiDetector {

    @Volatile
    private var ansiEnabled: Boolean = autoDetect()

    fun refresh() {
        ansiEnabled = autoDetect()
    }

    fun ansiEnabled(): Boolean = ansiEnabled

    fun enableColors() {
        System.setProperty(Constants.CLIQUE_COLOR, Constants.ALWAYS)
        ansiEnabled = true
    }

    fun disableColors() {
        System.setProperty(Constants.CLIQUE_COLOR, Constants.NEVER)
        ansiEnabled = false
    }

    private fun autoDetect(): Boolean {
        val noColor = System.getenv(Constants.NO_COLOR)
        if (!noColor.isNullOrEmpty()) return false

        val cliqueColor = System.getProperty(Constants.CLIQUE_COLOR)
        if (cliqueColor == Constants.ALWAYS) return true
        if (cliqueColor == Constants.NEVER) return false

        val cliColorForce = System.getenv(Constants.CLI_COLOR_FORCE)
        if (!cliColorForce.isNullOrEmpty() && cliColorForce != "0") return true

        val forceColor = System.getenv(Constants.FORCE_COLOR)
        if (!forceColor.isNullOrEmpty()) return true

        val colorTerm = System.getenv(Constants.COLOR_TERM)
        if (colorTerm != null) return true

        val os = System.getProperty(Constants.OS_NAME, Constants.EMPTY).lowercase()
        if (System.console() == null && !os.contains(Constants.WIN)) return false

        val term = System.getenv(Constants.TERM)
        if (term == null) {
            if (System.getenv(Constants.WT_SESSION) != null) return true
            return os.contains(Constants.WIN)
        }

        return !term.equals(Constants.DUMB, ignoreCase = true) &&
            !term.equals(Constants.PLAIN, ignoreCase = true)
    }
}
