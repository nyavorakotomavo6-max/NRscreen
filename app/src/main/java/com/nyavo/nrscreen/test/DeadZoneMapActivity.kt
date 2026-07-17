package com.nyavo.nrscreen.test

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.nyavo.nrscreen.R
import com.nyavo.nrscreen.data.DeadZoneMapHolder

class DeadZoneMapActivity : AppCompatActivity() {

    private lateinit var gridTestView: GridTestView
    private lateinit var statsTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Configurer les couleurs
        window.statusBarColor = Color.parseColor("#0D0221")
        window.navigationBarColor = Color.parseColor("#0D0221")
        
        setContentView(R.layout.activity_dead_zone_map)
        
        gridTestView = findViewById(R.id.gridTestView)
        statsTextView = findViewById(R.id.statsTextView)
        
        // Récupérer la map
        val map = DeadZoneMapHolder.current
        if (map != null) {
            gridTestView.setDeadZoneMap(map)
            
            // Afficher les statistiques
            val deadPercentage = map.deadPercentage()
            val deadCount = map.deadCells().size
            val suspectCount = map.suspectCells().size
            
            statsTextView.text = """
                Zones mortes: $deadCount (${String.format("%.1f", deadPercentage)}%)
                Zones suspectes: $suspectCount
            """.trimIndent()
        }
    }
}
