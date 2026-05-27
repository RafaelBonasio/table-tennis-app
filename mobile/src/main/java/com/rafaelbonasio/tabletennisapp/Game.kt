package com.rafaelbonasio.tabletennisapp

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import com.rafaelbonasio.tabletennisapp.core.Game
import com.rafaelbonasio.tabletennisapp.core.GameEvent
import com.rafaelbonasio.tabletennisapp.core.GameRules
import com.rafaelbonasio.tabletennisapp.core.Player
import com.rafaelbonasio.tabletennisapp.core.Scoreboard
import com.rafaelbonasio.tabletennisapp.ui.FancyScaffold
import com.rafaelbonasio.tabletennisapp.ui.FancyTopAppBar
import com.rafaelbonasio.tabletennisapp.ui.asymmetricBoundsTransform
import com.rafaelbonasio.tabletennisapp.ui.rememberDisplayRoundedCornerShape
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data object Game : NavKey

data object GameSharedTransitionKey

@Composable
fun GamePage(sharedTransitionScope: SharedTransitionScope, animatedVisibilityScope: AnimatedVisibilityScope, onNavigateBack: () -> Unit, onAddGame: (Game) -> Unit, viewModel: GameViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val hazeState = rememberHazeState()

    with (sharedTransitionScope) {
        Box(modifier = Modifier.sharedBounds(
            rememberSharedContentState(GameSharedTransitionKey),
            animatedVisibilityScope,
            boundsTransform = asymmetricBoundsTransform,
            clipInOverlayDuringTransition = OverlayClip(rememberDisplayRoundedCornerShape())
        )) {

            FancyScaffold(
                hazeState = hazeState,

                topBar = {
                    FancyTopAppBar(
                        hazeState = hazeState,
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
                            IconButton({ viewModel.addEvent(GameEvent.Undo) }) {
                                Icon(Icons.Default.KeyboardArrowLeft, "Desfazer")
                            }
                            IconButton({ viewModel.addEvent(GameEvent.Redo) }) {
                                Icon(Icons.Default.Refresh, "Refazer")
                            }
                        },
                        floatingActionButton = {
                            FloatingActionButton(
                                { onAddGame(viewModel.game) },
                                shape = rememberDisplayRoundedCornerShape()
                            ) {
                                Icon(Icons.Default.ExitToApp, "Finalizar")
                            }
                        },
                        containerColor = Color.Transparent
                    )
                }
            ) {
                Row(Modifier.padding(it).clip(rememberDisplayRoundedCornerShape())) {
                    PlayerScoreCard(
                        viewModel.game.player1.name,
                        state.player1Score,
                        state.player1SetScore,
                        !state.isPlayer2Serving,
                        { viewModel.addEvent(GameEvent.Player1Scored) })

                    PlayerScoreCard(
                        viewModel.game.player2.name,
                        state.player2Score,
                        state.player2SetScore,
                        state.isPlayer2Serving,
                        { viewModel.addEvent(GameEvent.Player2Scored) }
                    )
                }
            }
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
            .fillMaxHeight(),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .background(if (isServing) Color.Red.copy(0.1f) else Color.Transparent),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(name, style = MaterialTheme.typography.titleLarge)

            Text(points.toString(), style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.ExtraBold)
            Text(setsScore.toString(), style = MaterialTheme.typography.displaySmall)
        }
    }
}

@Composable
fun GameSettingsDialog(onDismiss: () -> Unit, onConfirm: (pointCount: Int, setCount: Int) -> Unit) {
    val pointsFieldState = rememberTextFieldState("11")
    val setsFieldState = rememberTextFieldState("3")

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

class GameViewModel : ViewModel() {
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
