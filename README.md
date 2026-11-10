# Tres en Raya - Android MVVM

Juego del tres en raya hecho con Kotlin y Jetpack Compose, con arquitectura MVVM.

## 1. Arquitectura MVVM

MVVM significa Model - View - ViewModel. Sirve para no mezclarlo todo en un solo archivo:

- **Model**: los datos y las reglas del juego.
- **View**: la pantalla. Dibuja el estado y avisa cuando el usuario toca algo.
- **ViewModel**: hace de puente entre las dos. Guarda el estado y recibe las acciones de la View.

La View no sabe las reglas del juego y el Model no sabe nada de la pantalla. Los datos van en una sola dirección: las acciones suben al ViewModel y el estado baja a la View.

### MVVMSimple

(REVISAR: escribir aquí qué tiene el proyecto MVVMSimple)

Por lo que he entendido, el ViewModel guarda el estado y también hace la lógica. Va bien para apps pequeñas.

### Comparación con MVVM-separatModel

(REVISAR: comprobarlo con el proyecto de ejemplo)

En MVVM-separatModel el Model es una parte propia y lleva las reglas. El ViewModel solo guarda el estado y llama al Model. Así el ViewModel queda más pequeño y la lógica se puede probar sola.

## 2. Mi Tres en Raya

He usado MVVM con el Model separado.

- `CellModel.kt`: una casilla (id, posición y símbolo).
- `BoardModel.kt`: el tablero, el turno, el ganador y el empate. Tiene `makeMove()` y `checkWinner()`.
- `Gameviewmodel.kt`: guarda el tablero en un StateFlow. Tiene `onCellClick()` y `restartGame()`.
- `GameScreen.kt`: la pantalla (título, mensaje, tablero 3x3 y botón REINICIAR).
- `MainActivity.kt`: arranca la app.

Cómo funciona una jugada:

1. El usuario toca una casilla.
2. La pantalla llama a `vm.onCellClick(position)`.
3. El ViewModel llama a `makeMove()` del BoardModel, que devuelve un tablero nuevo.
4. El StateFlow avisa del cambio y la pantalla se redibuja sola.

![Captura de la app](docs/captura.gif)

## 3. Conceptos y métodos aprendidos

- **MutableStateFlow**: una caja que guarda un valor y avisa cuando cambia.
- **asStateFlow()**: da la caja en modo solo lectura, para que la pantalla no la pueda cambiar.
- **update { }**: cambia el valor del StateFlow de forma segura.
- **collectAsState()**: pasa el StateFlow a un estado de Compose para que la pantalla se redibuje cuando cambia.
- **viewModel()**: coge el ViewModel de la pantalla. No se pierde si giro el móvil.
- **data class**: clase para guardar datos.
- **.copy()**: hace una copia de un objeto cambiando solo lo que le digo.
- **LazyVerticalGrid**: una cuadrícula. Con `GridCells.Fixed(3)` salen 3 columnas.
- **items(..., key = ...)**: pinta un elemento por cada casilla de la lista.
- **Modifier.clickable(enabled = ...)**: hace que se pueda tocar. Lo pongo en falso si la casilla está ocupada o la partida ha acabado.
- **aspectRatio(1f)**: hace las casillas cuadradas.
- **Animatable y spring**: hacen que el símbolo crezca con un pequeño rebote.
- **LaunchedEffect**: ejecuta código cuando una clave cambia.
- **animateColorAsState**: cambia un color poco a poco.
- **enableEdgeToEdge()**: la app ocupa toda la pantalla.
