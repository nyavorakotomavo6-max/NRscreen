package com.nyavo.nrscreen.control

import android.view.KeyEvent

/**
 * Permet de naviguer avec les boutons physiques (volume +/-, power) quand
 * l'écran est trop endommagé pour un contrôle tactile ou gestuel fiable.
 * Ex: volume+ = suivant, volume- = précédent, double power = valider.
 * Capté via NRAccessibilityService (canRequestFilterKeyEvents).
 */
class ExternalButtonController(private val onCommand: (GestureController.GestureCommand) -> Unit) {

    fun handleKeyEvent(event: KeyEvent): Boolean {
        return when (event.keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP -> {
                onCommand(GestureController.GestureCommand.NEXT); true
            }
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                onCommand(GestureController.GestureCommand.BACK); true
            }
            // TODO: double-appui rapide sur POWER pour CONFIRM (nécessite un détecteur de timing)
            else -> false
        }
    }
}
