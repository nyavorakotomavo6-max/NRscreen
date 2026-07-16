package com.nyavo.nrscreen.test

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.nyavo.nrscreen.R
import com.nyavo.nrscreen.data.DeadZoneMapHolder

class DeadZoneMapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deadzone_map)

        val map = DeadZoneMapHolder.current ?: DeadZoneMap(rows = 12, cols = 6)

        val resultGridView = findViewById<GridTestView>(R.id.resultGridView)
        resultGridView.map = map
        resultGridView.onCellTouched = null

        val dead = map.deadCells().size
        val total = map.rows * map.cols
        val pct = map.deadPercentage()

        findViewById<TextView>(R.id.summaryText).text =
            "Zones mortes : $dead/$total (${"%.1f".format(pct)}%%)"
    }

    companion object {
        const val SEUIL_ECRAN_QUASI_MORT = 85f
    }
}
