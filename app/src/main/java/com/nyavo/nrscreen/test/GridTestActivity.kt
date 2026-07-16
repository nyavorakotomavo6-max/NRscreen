package com.nyavo.nrscreen.test

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Ecran de test : affiche une grille pixel art plein écran.
 * L'utilisateur touche chaque case ; celles jamais touchées après un délai
 * ou un nombre de tentatives sont marquées DEAD.
 * TODO: vue custom (Canvas) qui dessine la grille en pixel art (voir ui/theme)
 * TODO: minuteur par case + détection d'absence de tap
 * TODO: bouton "terminer le test" -> passe à DeadZoneMapActivity avec le résultat
 */
class GridTestActivity : AppCompatActivity() {

    private lateinit var map: DeadZoneMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        map = DeadZoneMap(rows = GRID_ROWS, cols = GRID_COLS)
        // TODO: setContentView avec la vue pixel art custom
    }

    companion object {
        const val GRID_ROWS = 12
        const val GRID_COLS = 6
    }
}
