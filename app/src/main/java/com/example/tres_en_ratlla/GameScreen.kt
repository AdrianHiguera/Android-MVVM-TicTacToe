package com.example.tres_en_ratlla

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

// Paleta de colors del joc
private val BgTop = Color(0xFF1B1F3B)
private val BgBottom = Color(0xFF0D1024)
private val BoardBg = Color(0xFF232850)
private val CellBg = Color(0xFF2E3563)
private val ColorX = Color(0xFF4FC3F7)
private val ColorO = Color(0xFFFF6E87)
private val Gold = Color(0xFFFFD54F)
private val TextSoft = Color(0xFFB8BEE0)

// Línies guanyadores (només per ressaltar-les a la pantalla)
private val WINNING_LINES = listOf(
    listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8),
    listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8),
    listOf(0, 4, 8), listOf(2, 4, 6)
)

private fun winningCells(cells: List<CellModel>): Set<Int> {
    for (line in WINNING_LINES) {
        val s = cells[line[0]].symbol
        if (s.isNotEmpty() && s == cells[line[1]].symbol && s == cells[line[2]].symbol) {
            return line.toSet()
        }
    }
    return emptySet()
}

private fun symbolColor(symbol: String): Color = if (symbol == "X") ColorX else ColorO

@Composable
fun GameScreen(vm: GameViewModel = viewModel()) {
    // Llegim l'estat del ViewModel
    val board by vm.board.collectAsState()
    val winCells = winningCells(board.cells)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BgTop, BgBottom)))
            .statusBarsPadding()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(12.dp))
        Text(
            text = "TRES EN RATLLA",
            fontSize = 30.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 3.sp,
            color = Color.White
        )
        Spacer(Modifier.height(20.dp))

        // Missatge: torn, victòria o empat (amb el color del jugador)
        val message = when {
            board.winner != null -> "Ha guanyat ${board.winner}!"
            board.isDraw -> "Empat!"
            else -> "Torn de ${board.currentTurn}"
        }
        val targetColor = when {
            board.winner != null -> Gold
            board.isDraw -> TextSoft
            else -> symbolColor(board.currentTurn)
        }
        val messageColor by animateColorAsState(targetColor, label = "messageColor")
        Text(
            text = message,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            color = messageColor
        )
        Spacer(Modifier.height(28.dp))

        BoardGrid(
            cells = board.cells,
            winCells = winCells,
            enabled = !board.isGameOver,
            onCellClick = { vm.onCellClick(it) }
        )

        Spacer(Modifier.height(32.dp))
        Button(
            onClick = { vm.restartGame() },
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = ColorX, contentColor = BgBottom)
        ) {
            Text(
                text = "REINICIAR",
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
fun BoardGrid(
    cells: List<CellModel>,
    winCells: Set<Int>,
    enabled: Boolean,
    onCellClick: (Int) -> Unit
) {
    val boardShape = RoundedCornerShape(24.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .shadow(12.dp, boardShape)
            .background(BoardBg, boardShape)
            .padding(12.dp)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = false,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(cells, key = { it.id }) { cell ->
                CellItemView(
                    cell = cell,
                    isWinning = cell.position in winCells,
                    enabled = enabled,
                    onClick = { onCellClick(cell.position) }
                )
            }
        }
    }
}

@Composable
fun CellItemView(
    cell: CellModel,
    isWinning: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    val color = symbolColor(cell.symbol)

    // Animació: el símbol surt amb un petit "rebot"
    val scale = remember { Animatable(0f) }
    LaunchedEffect(cell.symbol) {
        if (cell.symbol.isNotEmpty()) {
            scale.snapTo(0f)
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
            )
        } else {
            scale.snapTo(0f)
        }
    }

    // La casella guanyadora es tenyeix i té vora daurada
    val bgColor by animateColorAsState(
        targetValue = if (isWinning) color.copy(alpha = 0.25f) else CellBg,
        label = "cellBg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isWinning) Gold else Color.Transparent,
        label = "cellBorder"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .aspectRatio(1f)
            .clip(shape)
            .background(bgColor, shape)
            .border(BorderStroke(3.dp, borderColor), shape)
            .clickable(enabled = enabled && cell.symbol.isEmpty()) { onClick() }
    ) {
        Text(
            text = cell.symbol,
            color = color,
            fontSize = 56.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            }
        )
    }
}