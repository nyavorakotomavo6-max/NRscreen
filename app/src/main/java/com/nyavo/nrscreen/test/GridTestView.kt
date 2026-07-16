package com.nyavo.nrscreen.test

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class GridTestView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    var map: DeadZoneMap = DeadZoneMap(rows = 12, cols = 6)
        set(value) {
            field = value
            invalidate()
        }

    var onCellTouched: ((row: Int, col: Int) -> Unit)? = null

    private val cellPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val gapPx = 4f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cellW = width / map.cols.toFloat()
        val cellH = height / map.rows.toFloat()

        for (r in 0 until map.rows) {
            for (c in 0 until map.cols) {
                val cell = map.cellAt(r, c)
                cellPaint.color = colorForState(cell.state)

                val left = c * cellW + gapPx / 2
                val top = r * cellH + gapPx / 2
                val right = (c + 1) * cellW - gapPx / 2
                val bottom = (r + 1) * cellH - gapPx / 2

                canvas.drawRect(left, top, right, bottom, cellPaint)
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
            if (cell.state == ZoneState.UNTESTED) {
                cell.state = ZoneState.ALIVE
                cell.lastUpdatedTimestamp = System.currentTimeMillis()
                invalidate()
                onCellTouched?.invoke(row, col)
            }
            return true
        }
        return super.onTouchEvent(event)
    }

    private fun colorForState(state: ZoneState): Int = when (state) {
        ZoneState.UNTESTED -> Color.parseColor("#3A3A55")
        ZoneState.ALIVE -> Color.parseColor("#3ECF8E")
        ZoneState.SUSPECT -> Color.parseColor("#F4A261")
        ZoneState.DEAD -> Color.parseColor("#E63946")
    }
}
