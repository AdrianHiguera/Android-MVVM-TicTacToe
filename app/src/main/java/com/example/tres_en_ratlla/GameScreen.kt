package com.example.tres_en_ratlla

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun GameScreen(vm: GameViewModel = viewModel()) {
    // Llegim l'estat del ViewModel
    val board by vm.board.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Tres en ratlla", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))

        // Missatge: torn, victòria o empat
        val message = when {
            board.winner != null -> "Ha guanyat ${board.winner}!"
            board.isDraw -> "Empat!"
            else -> "Torn de ${board.currentTurn}"
        }
        Text(text = message, fontSize = 22.sp)
        Spacer(Modifier.height(24.dp))

        BoardGrid(
            cells = board.cells,
            enabled = !board.isGameOver,
            onCellClick = { vm.onCellClick(it) }
        )

        Spacer(Modifier.height(24.dp))
        Button(onClick = { vm.restartGame() }) { Text("REINICIAR") }
    }
}

@Composable
fun BoardGrid(
    cells: List<CellModel>,
    enabled: Boolean,
    onCellClick: (Int) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        userScrollEnabled = false,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(cells, key = { it.id }) { cell ->
            CellItemView(
                cell = cell,
                enabled = enabled,
                onClick = { onCellClick(cell.position) }
            )
        }
    }
}

@Composable
fun CellItemView(
    cell: CellModel,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(8.dp)
    val color = if (cell.symbol == "X") Color(0xFF1976D2) else Color(0xFFD32F2F)

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .aspectRatio(1f)
            .clip(shape)
            .border(BorderStroke(3.dp, Color.Gray), shape)
            .clickable(enabled = enabled && cell.symbol.isEmpty()) { onClick() }
    ) {
        Text(
            text = cell.symbol,
            color = color,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold
        )
    }
}