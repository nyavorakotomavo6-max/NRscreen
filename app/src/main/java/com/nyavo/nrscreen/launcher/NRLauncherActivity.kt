package com.nyavo.nrscreen.launcher

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Launcher temporaire (déclaré CATEGORY_HOME) : réorganise les icônes
 * pour qu'elles tombent uniquement dans les zones vivantes de l'écran.
 * L'utilisateur peut revenir au launcher d'origine à tout moment.
 * TODO: RecyclerView avec grille dynamique respectant DeadZoneMap
 * TODO: IconLayoutManager pour le calcul de placement
 */
class NRLauncherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO: charger la liste des apps installées + le DeadZoneMap courant
    }
}
