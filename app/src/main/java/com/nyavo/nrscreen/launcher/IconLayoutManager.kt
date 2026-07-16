package com.nyavo.nrscreen.launcher

import com.nyavo.nrscreen.test.DeadZoneMap
import com.nyavo.nrscreen.test.ZoneState

/**
 * Calcule le placement des icônes d'apps en évitant les cases mortes/suspectes.
 * Priorise aussi les apps les plus utilisées dans les zones les plus faciles
 * d'accès (voir AppTouchHistoryTracker pour la fréquence d'usage).
 */
object IconLayoutManager {

    fun computeSafePositions(map: DeadZoneMap): List<Pair<Int, Int>> {
        val positions = mutableListOf<Pair<Int, Int>>()
        for (r in 0 until map.rows) {
            for (c in 0 until map.cols) {
                val cell = map.cellAt(r, c)
                if (cell.state == ZoneState.ALIVE) {
                    positions.add(r to c)
                }
            }
        }
        // TODO: trier par accessibilité (centre de l'écran d'abord, bords en dernier)
        return positions
    }
}
