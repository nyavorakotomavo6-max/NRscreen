package com.nyavo.nrscreen.test

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.nyavo.nrscreen.R
import com.nyavo.nrscreen.data.DeadZoneMapHolder

class GridTestActivity : AppCompatActivity() {

    private lateinit var map: DeadZoneMap
    private lateinit var gridView: GridTestView
    private lateinit var instructionText: TextView
    private lateinit var finishButton: Button
    private val handler = Handler(Looper.getMainLooper())
    private var phantomCountdown: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grid_test)

        map = DeadZoneMap(rows = GRID_ROWS, cols = GRID_COLS)

        gridView = findViewById(R.id.gridTestView)
        instructionText = findViewById(R.id.instructionText)
        finishButton = findViewById(R.id.finishButton)

        gridView.map = map
        gridView.phase = TestPhase.PHANTOM_CHECK

        finishButton.isEnabled = false
        finishButton.setOnClickListener { finishTestNow() }

        startPhantomCheckPhase()
    }

    private fun startPhantomCheckPhase() {
        phantomCountdown = object : CountDownTimer(PHANTOM_CHECK_MS, 1000) {
            override fun onTick(msLeft: Long) {
                val secLeft = (msLeft / 1000) + 1
                instructionText.text =
                    "NE TOUCHE PAS L'ECRAN pendant $secLeft s (detection des faux touchers)"
            }

            override fun onFinish() {
                startActivePhase()
            }
        }.start()
    }

    private fun startActivePhase() {
        gridView.phase = TestPhase.ACTIVE
        instructionText.text = getString(R.string.grid_test_instruction)
        finishButton.isEnabled = true
        handler.postDelayed({ markUntestedAsDead() }, TIMEOUT_MS)
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
        phantomCountdown?.cancel()
        super.onDestroy()
    }

    companion object {
        const val GRID_ROWS = 48
        const val GRID_COLS = 24
        const val PHANTOM_CHECK_MS = 5_000L
        const val TIMEOUT_MS = 300_000L
    }
}
