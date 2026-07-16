package com.nyavo.nrscreen.test

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

enum class TestPhase { PHANTOM_CHECK, ACTIVE }

class GridTestView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    var map: DeadZoneMap = DeadZoneMap(rows = 48, cols = 24)
        set(value) {
            field = value
            invalidate()
        }

    var phase: TestPhase = TestPhase.ACTIVE

    var onCellTouched: ((row: Int, col: Int) -> Unit)? = null

    private val cellPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val gapPx = 1.5f

    private val animatingCells = HashMap<Pair<Int, Int>, Long>()
    private val animHandler = Handler(Looper.getMainLooper())
    private var animLoopRunning = false

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cellW = width / map.cols.toFloat()
        val cellH = height / map.rows.toFloat()
        val now = System.currentTimeMillis()

        for (r in 0 until map.rows) {
            for (c in 0 until map.cols) {
                val cell = map.cellAt(r, c)
                cellPaint.color = colorForCell(cell)

                val left = c * cellW + gapPx / 2
                val top = r * cellH + gapPx / 2
                val right = (c + 1) * cellW - gapPx / 2
                val bottom = (r + 1) * cellH - gapPx / 2

                val animStart = animatingCells[r to c]
                if (animStart != null) {
                    val elapsed = now - animStart
                    val progress = (elapsed.toFloat() / ANIM_DURATION_MS).coerceIn(0f, 1f)
                    val envelope = sin(progress * PI).toFloat().coerceAtLeast(0f)
                    val scale = 1f + 0.6f * envelope
                    val shakeMag = 5f * envelope
                    val offsetX = shakeMag * sin(progress * 40)
                    val offsetY = shakeMag * cos(progress * 37)

                    val cx = (left + right) / 2f + offsetX
                    val cy = (top + bottom) / 2f + offsetY
                    val halfW = (right - left) / 2f * scale
                    val halfH = (bottom - top) / 2f * scale

                    canvas.drawRect(cx - halfW, cy - halfH, cx + halfW, cy + halfH, cellPaint)

                    if (progress >= 1f) animatingCells.remove(r to c)
                } else {
                    canvas.drawRect(left, top, right, bottom, cellPaint)
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
            val cellW = width / map.cols.toFloat()
            val cellH = height / map.rows.toFloat()
            val col = (event.x / cellW).toInt().coerceIn(0, map.cols - 1)
            val row = (event.y / cellH).toInt().coerceIn(0, map.rows - 1)
            val cell = map.cellAt(row, col)

            when (phase) {
                TestPhase.PHANTOM_CHECK -> {
                    if (!cell.isPhantom) {
                        cell.isPhantom = true
                        cell.lastUpdatedTimestamp = System.currentTimeMillis()
                        triggerAnim(row, col)
                        onCellTouched?.invoke(row, col)
                    }
                }
                TestPhase.ACTIVE -> {
                    if (cell.state == ZoneState.UNTESTED) {
                        cell.state = ZoneState.ALIVE
                        cell.lastUpdatedTimestamp = System.currentTimeMillis()
                        triggerAnim(row, col)
                        onCellTouched?.invoke(row, col)
                    }
                }
            }
            return true
        }
        return super.onTouchEvent(event)
    }

    private fun triggerAnim(row: Int, col: Int) {
        animatingCells[row to col] = System.currentTimeMillis()
        startAnimLoopIfNeeded()
    }

    private fun startAnimLoopIfNeeded() {
        if (animLoopRunning) return
        animLoopRunning = true
        val tick = object : Runnable {
            override fun run() {
                invalidate()
                if (animatingCells.isNotEmpty()) {
                    animHandler.postDelayed(this, 16L)
                } else {
                    animLoopRunning = false
                }
            }
        }
        animHandler.post(tick)
    }

    private fun colorForCell(cell: GridCell): Int {
        if (cell.isPhantom) return Color.parseColor("#B983FF")
        return when (cell.state) {
            ZoneState.UNTESTED -> Color.parseColor("#3A3A55")
            ZoneState.ALIVE -> Color.parseColor("#3ECF8E")
            ZoneState.SUSPECT -> Color.parseColor("#F4A261")
            ZoneState.DEAD -> Color.parseColor("#E63946")
        }
    }

    companion object {
        const val ANIM_DURATION_MS = 300L
    }
}
