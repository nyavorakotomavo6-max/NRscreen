package com.nyavo.nrscreen.test

/**
 * Modèle de données central : représente l'état de l'écran sous forme de grille pixel art.
 * Chaque cellule a un état ET un niveau de confiance (pas juste mort/vivant),
 * ce qui permet de détecter les zones "à moitié mortes" et de suivre leur évolution
 * dans le temps (dégradation progressive détectée par ContinuousMonitorService).
 */
enum class ZoneState { UNTESTED, ALIVE, SUSPECT, DEAD }

data class GridCell(
    val row: Int,
    val col: Int,
    var state: ZoneState = ZoneState.UNTESTED,
    var confidence: Float = 1.0f,       // 0.0 = totalement peu fiable, 1.0 = certain
    var missedTapCount: Int = 0,        // pour la détection continue de dégradation
    var lastUpdatedTimestamp: Long = 0L
)

class DeadZoneMap(val rows: Int, val cols: Int) {

    private val cells: Array<Array<GridCell>> =
        Array(rows) { r -> Array(cols) { c -> GridCell(r, c) } }

    fun cellAt(row: Int, col: Int): GridCell = cells[row][col]

    fun deadCells(): List<GridCell> =
        cells.flatten().filter { it.state == ZoneState.DEAD }

    fun suspectCells(): List<GridCell> =
        cells.flatten().filter { it.state == ZoneState.SUSPECT }

    /** Pourcentage d'écran mort, utilisé pour décider si on bascule en mode "100% cassé". */
    fun deadPercentage(): Float =
        deadCells().size.toFloat() / (rows * cols).toFloat() * 100f

    /**
     * Enregistre un tap raté sur une zone auparavant considérée vivante.
     * Utilisé par le monitoring continu pour repérer une dégradation avant
     * qu'elle ne soit totale (voir ContinuousMonitorService).
     */
    fun recordMissedTap(row: Int, col: Int) {
        val cell = cellAt(row, col)
        cell.missedTapCount++
        cell.lastUpdatedTimestamp = System.currentTimeMillis()
        if (cell.missedTapCount > MISSED_TAP_THRESHOLD && cell.state == ZoneState.ALIVE) {
            cell.state = ZoneState.SUSPECT
        }
    }

    // TODO: sérialisation JSON pour ProfileStorage (sauvegarde locale + export partage)

    companion object {
        const val MISSED_TAP_THRESHOLD = 5
    }
}
