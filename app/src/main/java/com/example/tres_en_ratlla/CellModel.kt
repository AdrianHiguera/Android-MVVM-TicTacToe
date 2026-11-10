package com.example.tres_en_ratlla

// Una casella del tauler
data class CellModel(
    val id: Int,                 // identificador de la casella
    val position: Int,           // posició al tauler (0..8)
    val symbol: String = ""      // "X", "O" o buit
)