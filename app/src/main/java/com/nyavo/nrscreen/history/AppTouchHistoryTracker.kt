package com.nyavo.nrscreen.history

/**
 * Enregistre, par app, le nombre de taps ratés dans les zones mortes.
 * Permet de suggérer à l'utilisateur d'éviter/reconfigurer les apps les plus
 * touchées par la casse (ex: un jeu avec des boutons dans un coin mort).
 * Stockage 100% local (voir data/ProfileStorage).
 */
data class AppTouchStat(
    val packageName: String,
    var missedTaps: Int = 0,
    var totalTaps: Int = 0
) {
    val missRate: Float get() = if (totalTaps == 0) 0f else missedTaps.toFloat() / totalTaps
}

object AppTouchHistoryTracker {
    private val stats = mutableMapOf<String, AppTouchStat>()

    fun recordTap(packageName: String, wasMissed: Boolean) {
        val stat = stats.getOrPut(packageName) { AppTouchStat(packageName) }
        stat.totalTaps++
        if (wasMissed) stat.missedTaps++
    }

    fun mostAffectedApps(limit: Int = 5): List<AppTouchStat> =
        stats.values.sortedByDescending { it.missRate }.take(limit)

    // TODO: persister via ProfileStorage entre les sessions
}
