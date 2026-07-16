package com.nyavo.nrscreen.test

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Affiche la carte finale des zones (vivant / suspect / mort) en pixel art,
 * avec le pourcentage global. Point d'entrée vers :
 * - le launcher temporaire
 * - le mode "100% cassé" (contrôle par gestes/boutons externes) si deadPercentage() très élevé
 * - le rapport visuel exportable
 */
class DeadZoneMapActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO: charger le DeadZoneMap du test précédent (ou depuis ProfileStorage)
        // TODO: si map.deadPercentage() > SEUIL_CRITIQUE -> proposer bascule gestes/boutons externes
    }

    companion object {
        const val SEUIL_ECRAN_QUASI_MORT = 85f
    }
}
