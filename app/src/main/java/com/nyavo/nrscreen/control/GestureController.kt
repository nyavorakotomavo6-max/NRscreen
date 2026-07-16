package com.nyavo.nrscreen.control

/**
 * Interprète des gestes spécifiques (swipe, double-tap sur zone saine, etc.)
 * comme des commandes de navigation, indépendamment des taps classiques.
 * Utilisé partout dans l'app pour "continuer / valider / retour" sans dépendre
 * d'un bouton précis qui pourrait tomber dans une zone morte.
 * TODO: définir le vocabulaire de gestes (swipe haut = valider, swipe bas = retour...)
 * TODO: rendre les gestes configurables (accessibilité = pas de norme universelle)
 */
class GestureController(private val onCommand: (GestureCommand) -> Unit) {

    enum class GestureCommand { CONFIRM, BACK, NEXT, HOME, EMERGENCY }

    // TODO: brancher sur un GestureDetector Android standard,
    // ou sur les évènements remontés par NRAccessibilityService en mode 100% cassé
}
