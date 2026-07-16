package com.nyavo.nrscreen.accessibility

import com.nyavo.nrscreen.test.DeadZoneMap

/**
 * Traduit la DeadZoneMap en configuration exploitable par les services
 * d'accessibilité natifs Android (zones tactiles exclues, TalkBack partiel),
 * pour que d'autres apps bénéficient de la détection sans que NRscreen
 * ait à gérer chaque app individuellement.
 * TODO: étudier le format exact attendu par AccessibilityService.GLOBAL_ACTION_*
 *       et les touch exploration APIs pour une intégration réelle
 */
object AccessibilityConfigExporter {

    fun exportAsSystemTouchExclusionZones(map: DeadZoneMap): List<android.graphics.Rect> {
        // TODO: convertir chaque GridCell DEAD en Rect en coordonnées écran réelles
        return emptyList()
    }
}
