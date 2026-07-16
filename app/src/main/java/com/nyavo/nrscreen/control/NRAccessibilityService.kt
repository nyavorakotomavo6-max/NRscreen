package com.nyavo.nrscreen.control

import android.accessibilityservice.AccessibilityService
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent

/**
 * Coeur du contrôle alternatif : capte les évènements système, les touches
 * physiques, et sert de pont vers les autres apps (voir AccessibilityConfigExporter)
 * pour qu'elles bénéficient aussi de la carte de zones mortes sans logique dupliquée.
 * TODO: relayer les taps ratés vers ContinuousMonitorService
 * TODO: déléguer les KeyEvent à ExternalButtonController
 * TODO: en mode "100% cassé", proposer un mode scan (surbrillance séquentielle
 *       des éléments à l'écran, validation par bouton physique)
 */
class NRAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // TODO
    }

    override fun onInterrupt() {}

    override fun onKeyEvent(event: KeyEvent?): Boolean {
        // TODO: déléguer à ExternalButtonController si mode contrôle externe actif
        return super.onKeyEvent(event)
    }
}
