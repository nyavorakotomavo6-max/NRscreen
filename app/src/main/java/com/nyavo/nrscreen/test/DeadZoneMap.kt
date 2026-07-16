package com.nyavo.nrscreen.test

enum class ZoneState { UNTESTED, ALIVE, SUSPECT, DEAD }

data class GridCell(
    val row: Int,
    val col: Int,
    var state: ZoneState = ZoneState.UNTESTED,
    var confidence: Float = 1.0f,
    var missedTapCount: Int = 0,
    var lastUpdatedTimestamp: Long = 0L,
    var isPhantom: Boolean = false
)

class DeadZoneMap(val rows: Int, val cols: Int) {

    private val cells: Array<Array<GridCell>> =
        Array(rows) { r -> Array(cols) { c -> GridCell(r, c) } }

    fun cellAt(row: Int, col: Int): GridCell = cells[row][col]

    fun deadCells(): List<GridCell> =
        cells.flatten().filter { it.state == ZoneState.DEAD }

    fun suspectCells(): List<GridCell> =
        cells.flatten().filter { it.state == ZoneState.SUSPECT }

    fun phantomCells(): List<GridCell> =
        cells.flatten().filter { it.isPhantom }

    fun deadPercentage(): Float =
        deadCells().size.toFloat() / (rows * cols).toFloat() * 100f

    fun phantomPercentage(): Float =
        phantomCells().size.toFloat() / (rows * cols).toFloat() * 100f

    fun recordMissedTap(row: Int, col: Int) {
        val cell = cellAt(row, col)
        cell.missedTapCount++
        cell.lastUpdatedTimestamp = System.currentTimeMillis()
        if (cell.missedTapCount > MISSED_TAP_THRESHOLD && cell.state == ZoneState.ALIVE) {
            cell.state = ZoneState.SUSPECT
        }
    }

    companion object {
        const val MISSED_TAP_THRESHOLD = 5
    }
}
