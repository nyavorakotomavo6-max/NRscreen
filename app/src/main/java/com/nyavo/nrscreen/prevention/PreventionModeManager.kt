package com.nyavo.nrscreen.prevention

import com.nyavo.nrscreen.test.DeadZoneMap
import com.nyavo.nrscreen.test.ZoneState

/**
 * Mode préventif : pour un écran fissuré mais pas encore mort. Évite d'appuyer
 * sur les zones fragiles (marquées SUSPECT) pour ralentir la dégradation,
 * en redirigeant proactivement via les boutons flottants même si la zone
 * répond encore techniquement.
 */
class PreventionModeManager(private val map: DeadZoneMap) {

    var isEnabled: Boolean = false

    fun zonesToAvoid(): List<Pair<Int, Int>> =
        (map.deadCells() + map.suspectCells()).map { it.row to it.col }

    // TODO: hook dans FloatingButtonService pour repositionner même les
    // interactions "normales" hors des zones fragiles quand isEnabled = true
}
