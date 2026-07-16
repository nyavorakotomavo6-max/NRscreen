package com.nyavo.nrscreen.ui.theme

import android.graphics.Color
import android.graphics.Paint

/**
 * Constantes et helpers pour le rendu pixel art cohérent dans toute l'app
 * (grille de test, launcher, boutons flottants, rapport visuel).
 */
object PixelArtTheme {

    const val PIXEL_SIZE_DP = 24
    const val GRID_GAP_DP = 2

    fun paintForState(colorHex: String): Paint = Paint().apply {
        color = Color.parseColor(colorHex)
        isAntiAlias = false // volontaire : look pixel net, pas de lissage
        style = Paint.Style.FILL
    }
}
