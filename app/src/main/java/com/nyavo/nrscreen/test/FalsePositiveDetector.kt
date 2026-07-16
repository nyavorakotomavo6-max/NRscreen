package com.nyavo.nrscreen.test

/**
 * Distingue une vraie zone morte physique d'un faux positif :
 * saleté sur l'écran, protection mal posée, calibration tactile temporaire.
 * Heuristique simple pour le MVP : si la zone redevient réactive après nettoyage,
 * ou si le pattern de non-réponse est inconsistant (parfois ça marche, parfois non
 * sans dégradation progressive claire), suggérer un nettoyage plutôt que conclure à la casse.
 */
object FalsePositiveDetector {

    fun isProbablyDirtNotCrack(cell: GridCell): Boolean {
        // TODO: heuristique basée sur confidence fluctuante + absence de pattern
        // géométrique cohérent avec une fissure physique (les vraies casses
        // suivent souvent des lignes/clusters, pas un point isolé aléatoire)
        return false
    }

    fun suggestCleaningIfNeeded(map: DeadZoneMap): List<GridCell> =
        map.suspectCells().filter { isProbablyDirtNotCrack(it) }
}
