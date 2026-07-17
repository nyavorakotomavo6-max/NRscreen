package com.nyavo.nrscreen.test

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.nyavo.nrscreen.R
import com.nyavo.nrscreen.data.DeadZoneMapHolder

class GridTestActivity : AppCompatActivity() {

    private lateinit var gridTestView: GridTestView
    private lateinit var timerTextView: TextView
    private lateinit var finishButton: Button
    
    private var deadZoneMap: DeadZoneMap? = null
    private var countDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Configurer les couleurs de la status bar et navigation bar
        window.statusBarColor = Color.parseColor("#0D0221")
        window.navigationBarColor = Color.parseColor("#0D0221")
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        
        setContentView(R.layout.activity_grid_test)
        
        gridTestView = findViewById(R.id.gridTestView)
        timerTextView = findViewById(R.id.timerTextView)
        finishButton = findViewById(R.id.button)
        
        // Initialiser la DeadZoneMap
        deadZoneMap = DeadZoneMap(rows = 12, cols = 6)
        gridTestView.setDeadZoneMap(deadZoneMap!!)
        
        // Configurer le listener de tap
        gridTestView.setOnCellTappedListener(object : GridTestView.OnCellTappedListener {
            override fun onCellTapped(row: Int, col: Int) {
                deadZoneMap?.recordMissedTap(row, col)
            }
        })
        
        // Configurer le bouton Terminer        finishButton.setOnClickListener {
            finishTest()
        }
        
        // Démarrer le timer
        startTimer()
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(15000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = (millisUntilFinished / 1000).toInt()
                timerTextView.text = "${seconds}s"
            }

            override fun onFinish() {
                timerTextView.text = "0s"
                finishTest()
            }
        }.start()
    }

    private fun finishTest() {
        countDownTimer?.cancel()
        
        // Marquer toutes les cellules UNTESTED comme DEAD
        val map = deadZoneMap ?: return
        for (row in 0 until 12) {
            for (col in 0 until 6) {
                val cell = map.cellAt(row, col)
                if (cell.state == ZoneState.UNTESTED) {
                    cell.state = ZoneState.DEAD
                }
            }
        }
        
        // Stocker la map
        DeadZoneMapHolder.current = map
        
        // Naviguer vers DeadZoneMapActivity
        startActivity(
            android.content.Intent(this, DeadZoneMapActivity::class.java)
        )
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }}
