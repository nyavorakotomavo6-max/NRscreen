
package com.nyavo.nrscreen.test

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.nyavo.nrscreen.R
import com.nyavo.nrscreen.data.DeadZoneMapHolder

class GridTestActivity : AppCompatActivity() {

    private lateinit var map: DeadZoneMap
    private lateinit var gridView: GridTestView
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grid_test)

        map = DeadZoneMap(rows = GRID_ROWS, cols = GRID_COLS)

        gridView = findViewById(R.id.gridTestView)
        gridView.map = map

        handler.postDelayed({ markUntestedAsDead() }, TIMEOUT_MS)

        findViewById<Button>(R.id.finishButton).setOnClickListener {
            finishTestNow()
        }
    }

    private fun markUntestedAsDead() {
        for (r in 0 until map.rows) {
            for (c in 0 until map.cols) {
                val cell = map.cellAt(r, c)
                if (cell.state == ZoneState.UNTESTED) {
                    cell.state = ZoneState.DEAD
                }
            }
        }
        gridView.invalidate()
    }

    private fun finishTestNow() {
        handler.removeCallbacksAndMessages(null)
        markUntestedAsDead()
        DeadZoneMapHolder.current = map
        startActivity(Intent(this, DeadZoneMapActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    companion object {
        const val GRID_ROWS = 12
        const val GRID_COLS = 6
        const val TIMEOUT_MS = 15_000L
    }
}