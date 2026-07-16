package com.nyavo.nrscreen.report

import com.nyavo.nrscreen.test.DeadZoneMap

/**
 * Génère un rapport visuel (image ou texte structuré pour le MVP) de l'état
 * de l'écran : utile avant une revente, ou pour suivre l'évolution dans le temps.
 * TODO: rendu Canvas -> Bitmap de la grille avec légende
 * TODO: export PNG local (aucun upload, tout reste sur l'appareil)
 * TODO: horodatage + comparaison avec un rapport précédent (évolution de la casse)
 */
class VisualReportGenerator(private val map: DeadZoneMap) {

    fun generateSummaryText(): String {
        val dead = map.deadCells().size
        val suspect = map.suspectCells().size
        val total = map.rows * map.cols
        return "Zones mortes : $dead/$total (${"%.1f".format(map.deadPercentage())}%)\n" +
                "Zones suspectes : $suspect/$total"
    }

    // TODO: fun generateBitmap(): Bitmap
}
