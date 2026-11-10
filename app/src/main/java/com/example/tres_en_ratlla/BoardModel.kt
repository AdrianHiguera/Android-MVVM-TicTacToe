package com.example.tres_en_ratlla

// El tauler: guarda les dades i les regles del joc
data class BoardModel(
    val cells: List<CellModel>,
    val currentTurn: String = "X",
    val winner: String? = null,
    val isDraw: Boolean = false,
    val isGameOver: Boolean = false
) {

    companion object {
        // Totes les línies que fan guanyar
        private val WINNING_LINES = listOf(
            listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8),   // files
            listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8),   // columnes
            listOf(0, 4, 8), listOf(2, 4, 6)                     // diagonals
        )

        // Tauler nou: 9 caselles buides
        fun createInitialBoard(): BoardModel =
            BoardModel(cells = List(9) { CellModel(id = it, position = it) })
    }

    // Posa el símbol del torn a la casella. Retorna un tauler nou.
    fun makeMove(position: Int): BoardModel {
        // Si el joc ha acabat o la casella no és buida, no fem res
        if (isGameOver) return this
        if (position !in 0..8) return this
        if (cells[position].symbol.isNotEmpty()) return this

        val newCells = cells.map {
            if (it.position == position) it.copy(symbol = currentTurn) else it
        }
        val moved = copy(cells = newCells).checkWinner()

        // Si el joc continua, canviem el torn
        return if (moved.isGameOver) moved
        else moved.copy(currentTurn = if (currentTurn == "X") "O" else "X")
    }

    // Mira si hi ha guanyador o empat. Retorna un tauler nou.
    fun checkWinner(): BoardModel {
        for (line in WINNING_LINES) {
            val symbol = cells[line[0]].symbol
            if (symbol.isNotEmpty() &&
                symbol == cells[line[1]].symbol &&
                symbol == cells[line[2]].symbol
            ) {
                return copy(winner = symbol, isGameOver = true)
            }
        }
        if (cells.all { it.symbol.isNotEmpty() }) {
            return copy(isDraw = true, isGameOver = true)
        }
        return this
    }
}