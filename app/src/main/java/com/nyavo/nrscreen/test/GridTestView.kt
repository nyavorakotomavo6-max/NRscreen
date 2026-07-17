package com.nyavo.nrscreen.test

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.OvershootInterpolator
import com.nyavo.nrscreen.R
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

class GridTestView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    interface OnCellTappedListener {
        fun onCellTapped(row: Int, col: Int)
    }

    private var deadZoneMap: DeadZoneMap? = null
    private var onCellTappedListener: OnCellTappedListener? = null

    // Dimensions de la grille
    private val rows = 12
    private val cols = 6
    private var cellWidth = 0f
    private var cellHeight = 0f
    private var gridOffsetX = 0f
    private var gridOffsetY = 0f

    // Paints pour le rendu
    private val backgroundPaint = Paint().apply {
        color = Color.parseColor("#0D0221")
        style = Paint.Style.FILL
    }

    private val gridLinePaint = Paint().apply {
        color = Color.parseColor("#240046")
        style = Paint.Style.STROKE
        strokeWidth = 2f
        isAntiAlias = false
    }
    private val cellFillPaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = false
    }

    private val cellBorderPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 3f
        isAntiAlias = false
    }

    private val texturePaint = Paint().apply {
        isAntiAlias = false
    }

    private val glowPaint = Paint().apply {
        isAntiAlias = true
        maskFilter = BlurMaskFilter(20f, BlurMaskFilter.Blur.NORMAL)
    }

    private val ripplePaint = Paint().apply {
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    private val flashPaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = false
    }

    private val particlePaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = false
    }

    // Animations par cellule
    data class CellAnimation(
        var flashAlpha: Float = 0f,
        var flashStartTime: Long = 0,
        var rippleRadius: Float = 0f,
        var rippleAlpha: Float = 0f,
        var rippleStartTime: Long = 0,
        var scale: Float = 1f,
        var scaleAnimator: ValueAnimator? = null,
        var particles: MutableList<Particle> = mutableListOf()
    )

    data class Particle(
        var x: Float,
        var y: Float,        var vx: Float,
        var vy: Float,
        var alpha: Float = 1f,
        var startTime: Long
    )

    private val cellAnimations = mutableMapOf<String, CellAnimation>()

    // Pulsation pour ALIVE
    private var alivePulsePhase = 0f
    private val alivePulseAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 2000
        repeatCount = ValueAnimator.INFINITE
        repeatMode = ValueAnimator.REVERSE
        addUpdateListener {
            alivePulsePhase = it.animatedValue as Float
            invalidate()
        }
    }

    // Clignotement pour SUSPECT
    private var suspectBlinkPhase = 0f
    private val suspectBlinkAnimator = ValueAnimator.ofFloat(0.6f, 1f).apply {
        duration = 1500
        repeatCount = ValueAnimator.INFINITE
        repeatMode = ValueAnimator.REVERSE
        addUpdateListener {
            suspectBlinkPhase = it.animatedValue as Float
            invalidate()
        }
    }

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null) // Nécessaire pour BlurMaskFilter
        alivePulseAnimator.start()
        suspectBlinkAnimator.start()
    }

    fun setDeadZoneMap(map: DeadZoneMap) {
        this.deadZoneMap = map
        invalidate()
    }

    fun setOnCellTappedListener(listener: OnCellTappedListener) {
        this.onCellTappedListener = listener
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
                // Calculer les dimensions des cellules (multiple de 4 pour pixel art)
        val availableWidth = w.toFloat()
        val availableHeight = h.toFloat()
        
        cellWidth = (availableWidth / cols).toInt().toFloat()
        cellHeight = (availableHeight / rows).toInt().toFloat()
        
        // Arrondir au multiple de 4 inférieur
        cellWidth = (cellWidth / 4).toInt() * 4f
        cellHeight = (cellHeight / 4).toInt() * 4f
        
        // Centrer la grille
        val totalGridWidth = cellWidth * cols
        val totalGridHeight = cellHeight * rows
        gridOffsetX = (availableWidth - totalGridWidth) / 2f
        gridOffsetY = (availableHeight - totalGridHeight) / 2f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // Fond
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), backgroundPaint)
        
        val map = deadZoneMap ?: return
        
        // Dessiner chaque cellule
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val cell = map.cellAt(row, col)
                val left = gridOffsetX + col * cellWidth
                val top = gridOffsetY + row * cellHeight
                val right = left + cellWidth
                val bottom = top + cellHeight
                
                drawCell(canvas, cell, left, top, right, bottom)
            }
        }
        
        // Dessiner les lignes de grille
        drawGridLines(canvas)
        
        // Dessiner les animations par-dessus
        drawAnimations(canvas)
    }

    private fun drawCell(canvas: Canvas, cell: GridCell, left: Float, top: Float, right: Float, bottom: Float) {
        val cellKey = "${cell.row}_${cell.col}"
        val animation = cellAnimations[cellKey]
                // Appliquer le scale visuel
        val scale = animation?.scale ?: 1f
        if (scale != 1f) {
            val centerX = (left + right) / 2f
            val centerY = (top + bottom) / 2f
            canvas.save()
            canvas.scale(scale, scale, centerX, centerY)
        }
        
        // Fond de la cellule selon l'état
        when (cell.state) {
            ZoneState.UNTESTED -> {
                cellFillPaint.color = Color.parseColor("#1B0A2E")
                canvas.drawRect(left, top, right, bottom, cellFillPaint)
                drawTexture(canvas, left, top, right, bottom, "#240046", 0.2f)
            }
            ZoneState.ALIVE -> {
                // Glow
                val glowAlpha = (0.3f + alivePulsePhase * 0.2f)
                glowPaint.color = Color.parseColor("#E0AAFF")
                glowPaint.alpha = (glowAlpha * 255).toInt()
                canvas.drawRect(left + 10, top + 10, right - 10, bottom - 10, glowPaint)
                
                // Fond
                cellFillPaint.color = Color.parseColor("#7B2D8E")
                canvas.drawRect(left, top, right, bottom, cellFillPaint)
            }
            ZoneState.DEAD -> {
                cellFillPaint.color = Color.parseColor("#0D0221")
                canvas.drawRect(left, top, right, bottom, cellFillPaint)
                drawDeadCross(canvas, left, top, right, bottom)
            }
            ZoneState.SUSPECT -> {
                cellFillPaint.color = Color.parseColor("#5A189A")
                cellFillPaint.alpha = (suspectBlinkPhase * 255).toInt()
                canvas.drawRect(left, top, right, bottom, cellFillPaint)
                cellFillPaint.alpha = 255
            }
        }
        
        // Bordure
        cellBorderPaint.color = when (cell.state) {
            ZoneState.UNTESTED -> Color.parseColor("#3C096C")
            ZoneState.ALIVE -> Color.parseColor("#C77DFF")
            ZoneState.DEAD -> Color.parseColor("#240046")
            ZoneState.SUSPECT -> Color.parseColor("#3C096C")
        }
        cellBorderPaint.strokeWidth = if (cell.state == ZoneState.ALIVE) 4f else 3f
        canvas.drawRect(left, top, right, bottom, cellBorderPaint)
                // Flash
        if (animation != null && animation.flashAlpha > 0f) {
            flashPaint.color = Color.WHITE
            flashPaint.alpha = (animation.flashAlpha * 255).toInt()
            canvas.drawRect(left, top, right, bottom, flashPaint)
        }
        
        if (scale != 1f) {
            canvas.restore()
        }
    }

    private fun drawTexture(canvas: Canvas, left: Float, top: Float, right: Float, bottom: Float, color: String, alpha: Float) {
        texturePaint.color = Color.parseColor(color)
        texturePaint.alpha = (alpha * 255).toInt()
        
        val pixelSize = 4f
        var y = top
        var row = 0
        while (y < bottom) {
            var x = left
            var col = 0
            while (x < right) {
                if ((row + col) % 2 == 0) {
                    canvas.drawRect(x, y, x + pixelSize, y + pixelSize, texturePaint)
                }
                x += pixelSize
                col++
            }
            y += pixelSize
            row++
        }
    }

    private fun drawDeadCross(canvas: Canvas, left: Float, top: Float, right: Float, bottom: Float) {
        texturePaint.color = Color.parseColor("#1A0B2E")
        texturePaint.style = Paint.Style.STROKE
        texturePaint.strokeWidth = 2f
        
        val centerX = (left + right) / 2f
        val centerY = (top + bottom) / 2f
        val size = min(cellWidth, cellHeight) * 0.3f
        
        canvas.drawLine(centerX - size, centerY - size, centerX + size, centerY + size, texturePaint)
        canvas.drawLine(centerX + size, centerY - size, centerX - size, centerY + size, texturePaint)
        
        texturePaint.style = Paint.Style.FILL
    }

    private fun drawGridLines(canvas: Canvas) {        // Lignes verticales
        for (col in 1 until cols) {
            val x = gridOffsetX + col * cellWidth
            canvas.drawLine(x, gridOffsetY, x, gridOffsetY + rows * cellHeight, gridLinePaint)
        }
        
        // Lignes horizontales
        for (row in 1 until rows) {
            val y = gridOffsetY + row * cellHeight
            canvas.drawLine(gridOffsetX, y, gridOffsetX + cols * cellWidth, y, gridLinePaint)
        }
    }

    private fun drawAnimations(canvas: Canvas) {
        for ((_, animation) in cellAnimations) {
            // Ripple
            if (animation.rippleAlpha > 0f) {
                ripplePaint.color = Color.parseColor("#E0AAFF")
                ripplePaint.alpha = (animation.rippleAlpha * 255).toInt()
                ripplePaint.strokeWidth = 3f
                canvas.drawCircle(
                    animation.rippleRadius,
                    animation.rippleRadius,
                    animation.rippleRadius,
                    ripplePaint
                )
            }
            
            // Particules
            for (particle in animation.particles) {
                particlePaint.color = Color.parseColor("#C77DFF")
                particlePaint.alpha = (particle.alpha * 255).toInt()
                canvas.drawRect(particle.x, particle.y, particle.x + 4f, particle.y + 4f, particlePaint)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val col = ((event.x - gridOffsetX) / cellWidth).toInt()
                val row = ((event.y - gridOffsetY) / cellHeight).toInt()
                
                if (row in 0 until rows && col in 0 until cols) {
                    val cell = deadZoneMap?.cellAt(row, col)
                    if (cell != null && (cell.state == ZoneState.UNTESTED || cell.state == ZoneState.ALIVE)) {
                        triggerCellAnimation(row, col, event.x, event.y)
                        onCellTappedListener?.onCellTapped(row, col)
                        return true
                    }                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun triggerCellAnimation(row: Int, col: Int, touchX: Float, touchY: Float) {
        val cellKey = "${row}_${col}"
        val animation = cellAnimations.getOrPut(cellKey) { CellAnimation() }
        
        val left = gridOffsetX + col * cellWidth
        val top = gridOffsetY + row * cellHeight
        val right = left + cellWidth
        val bottom = top + cellHeight
        val centerX = (left + right) / 2f
        val centerY = (top + bottom) / 2f
        
        // Flash
        animation.flashAlpha = 0.4f
        animation.flashStartTime = System.currentTimeMillis()
        
        // Ripple
        animation.rippleRadius = 0f
        animation.rippleAlpha = 0.5f
        animation.rippleStartTime = System.currentTimeMillis()
        
        // Bounce
        animation.scaleAnimator?.cancel()
        animation.scaleAnimator = ValueAnimator.ofFloat(1f, 0.92f, 1.04f, 1f).apply {
            duration = 250
            interpolator = OvershootInterpolator()
            addUpdateListener {
                animation.scale = it.animatedValue as Float
                invalidate()
            }
            start()
        }
        
        // Particules
        animation.particles.clear()
        val particleCount = 6 + (Math.random() * 3).toInt()
        for (i in 0 until particleCount) {
            val angle = (Math.random() * Math.PI * 2).toFloat()
            val speed = 2f + (Math.random() * 3).toFloat()
            animation.particles.add(
                Particle(
                    x = centerX,
                    y = centerY,
                    vx = (Math.cos(angle) * speed).toFloat(),
                    vy = (Math.sin(angle) * speed).toFloat(),                    alpha = 1f,
                    startTime = System.currentTimeMillis()
                )
            )
        }
        
        // Démarrer l'animation globale
        startAnimationUpdates()
    }

    private var animationUpdater: ValueAnimator? = null

    private fun startAnimationUpdates() {
        animationUpdater?.cancel()
        animationUpdater = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 400
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener {
                updateAnimations()
                invalidate()
            }
            start()
        }
    }

    private fun updateAnimations() {
        val currentTime = System.currentTimeMillis()
        val toRemove = mutableListOf<String>()
        
        for ((cellKey, animation) in cellAnimations) {
            // Flash (80ms)
            if (animation.flashAlpha > 0f) {
                val elapsed = currentTime - animation.flashStartTime
                if (elapsed >= 80) {
                    animation.flashAlpha = 0f
                } else {
                    animation.flashAlpha = 0.4f * (1f - elapsed / 80f)
                }
            }
            
            // Ripple (300ms)
            if (animation.rippleAlpha > 0f) {
                val elapsed = currentTime - animation.rippleStartTime
                if (elapsed >= 300) {
                    animation.rippleAlpha = 0f
                } else {
                    val progress = elapsed / 300f
                    animation.rippleRadius = progress * max(cellWidth, cellHeight)
                    animation.rippleAlpha = 0.5f * (1f - progress)
                }            }
            
            // Particules (400ms)
            val activeParticles = animation.particles.filter { particle ->
                val elapsed = currentTime - particle.startTime
                if (elapsed >= 400) {
                    false
                } else {
                    particle.x += particle.vx
                    particle.y += particle.vy
                    particle.alpha = 1f - (elapsed / 400f)
                    true
                }
            }
            animation.particles.clear()
            animation.particles.addAll(activeParticles)
            
            // Supprimer si tout est terminé
            if (animation.flashAlpha == 0f && animation.rippleAlpha == 0f && animation.particles.isEmpty()) {
                toRemove.add(cellKey)
            }
        }
        
        toRemove.forEach { cellAnimations.remove(it) }
        
        if (cellAnimations.isEmpty()) {
            animationUpdater?.cancel()
            animationUpdater = null
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        alivePulseAnimator.cancel()
        suspectBlinkAnimator.cancel()
        animationUpdater?.cancel()
        cellAnimations.values.forEach { it.scaleAnimator?.cancel() }
        cellAnimations.clear()
    }
}
