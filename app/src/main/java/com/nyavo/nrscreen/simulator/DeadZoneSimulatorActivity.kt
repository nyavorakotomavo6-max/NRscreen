package com.nyavo.nrscreen.simulator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nyavo.nrscreen.test.DeadZoneMap
import com.nyavo.nrscreen.test.ZoneState

/**
 * Permet de simuler artificiellement des zones mortes sur un écran fonctionnel,
 * pour développer/tester l'app sans avoir un vrai téléphone cassé sous la main.
 * Utile pour toi en dev solo : teste le launcher, les boutons flottants, le mode
 * gestes, etc. sans dépendre d'un device physiquement cassé.
 * TODO: UI pour "peindre" des zones mortes sur une grille de test
 * TODO: injecter cette fausse DeadZoneMap dans tout le pipeline normal de l'app
 */
class DeadZoneSimulatorActivity : AppCompatActivity() {

    private lateinit var simulatedMap: DeadZoneMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        simulatedMap = DeadZoneMap(rows = 12, cols = 6)
        // TODO: interface tactile pour marquer des cellules DEAD/SUSPECT à la main
    }

    fun markCellDead(row: Int, col: Int) {
        simulatedMap.cellAt(row, col).state = ZoneState.DEAD
    }
}
