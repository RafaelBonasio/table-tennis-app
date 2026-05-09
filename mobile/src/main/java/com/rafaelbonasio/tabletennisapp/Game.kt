package com.rafaelbonasio.tabletennisapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rafaelbonasio.tabletennisapp.core.Game
import com.rafaelbonasio.tabletennisapp.core.GameEvent
import com.rafaelbonasio.tabletennisapp.core.GameRules
import com.rafaelbonasio.tabletennisapp.core.Player
import com.rafaelbonasio.tabletennisapp.core.Scoreboard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalMaterial3Api::class, ExperimentalUuidApi::class)
@Composable
fun GamePage(gameViewModel: GameViewModel, historyViewModel: HistoryViewModel, onNavigateBack: () -> Unit) {
    val state by gameViewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    BackButton(onNavigateBack)
                },
                title = {
                    Text("Jogo")
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                actions = {
                    IconButton({ gameViewModel.addEvent(GameEvent.Undo) }) {
                        Icon(Icons.Default.KeyboardArrowLeft, "Desfazer")
                    }
                    IconButton({ gameViewModel.addEvent(GameEvent.Redo) }) {
                        Icon(Icons.Default.Refresh, "Refazer")
                    }
                },
                floatingActionButton = {
                    FloatingActionButton({ historyViewModel.addGame(gameViewModel.game) }) {
                        Icon(Icons.Default.ExitToApp, "Finalizar")
                    }
                }
            )
        }
    ) {
        Row(Modifier.padding(it)) {
            PlayerScoreCard(
                gameViewModel.game.player1.name,
                state.player1Score,
                state.player1SetScore,
                !state.isPlayer2Serving,
                { gameViewModel.addEvent(GameEvent.Player1Scored) })

            PlayerScoreCard(
                gameViewModel.game.player2.name,
                state.player2Score,
                state.player2SetScore,
                state.isPlayer2Serving,
                { gameViewModel.addEvent(GameEvent.Player2Scored) }
            )
        }
    }
}

@Composable
private fun RowScope.PlayerScoreCard(
    name: String,
    points: Int,
    setsScore: Int,
    isServing: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .border(if (isServing) BorderStroke(2.dp, Color.Red) else BorderStroke(2.dp, Color.Gray)),
        color = MaterialTheme.colorScheme.primaryContainer,
        onClick = onClick
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(name, style = MaterialTheme.typography.titleLarge)

            Row {
                Text(setsScore.toString(), style = MaterialTheme.typography.labelSmall)
                Text(points.toString(), style = MaterialTheme.typography.displayLarge)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameSettingsDialog(onDismiss: () -> Unit, onConfirm: (pointCount: Int, setCount: Int) -> Unit) {
    var pointsFieldState = rememberTextFieldState("11")
    var setsFieldState = rememberTextFieldState("3")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Configurações do Jogo") },
        text = {
            Column {
                Text("Quantidade de Pontos")
                TextField(pointsFieldState)

                Text("Quantidade de Sets")
                TextField(setsFieldState)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(pointsFieldState.text.toString().toInt(), setsFieldState.text.toString().toInt())
                onDismiss()
            }) {
                Text("Iniciar")
            }
        }
    )
}



class GameViewModel() : ViewModel() {
    @OptIn(ExperimentalUuidApi::class)
    public var game: Game = Game(Player(Uuid.random(), ""), Player(Uuid.random(), ""), GameRules(11, 3, 2))

    private val _uiState = MutableStateFlow(Scoreboard())
    val uiState = _uiState.asStateFlow()

    fun addEvent(event: GameEvent) {
        game.events.add(event)

        _uiState.update { game.calculateScoreboard() }
    }

    fun newGame(player1: Player, player2: Player, rules: GameRules) {
        game = Game(player1, player2, rules)

        _uiState.update { Scoreboard() } //
    }
}
