package com.rafaelbonasio.tabletennisapp

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.minus
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rafaelbonasio.tabletennisapp.core.Game
import com.rafaelbonasio.tabletennisapp.core.GameRules
import com.rafaelbonasio.tabletennisapp.core.Player
import com.rafaelbonasio.tabletennisapp.ui.FancyScaffold
import com.rafaelbonasio.tabletennisapp.ui.asymmetricBoundsTransform
import com.rafaelbonasio.tabletennisapp.ui.rememberDisplayRoundedCornerShape
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Composable
fun HistoryTab(sharedTransitionScope: SharedTransitionScope, animatedVisibilityScope: AnimatedVisibilityScope, onStartGame: (player1: Player, player2: Player, rules: GameRules) -> Unit, paddingValues: PaddingValues, viewModel: HistoryViewModel = viewModel()) {
    var showDialog by remember { mutableStateOf(false) }

    val games by viewModel.games.collectAsState()

    val lazyListState = rememberLazyListState()

    val hazeState = rememberHazeState()

    with (sharedTransitionScope) {
        FancyScaffold(
            hazeState = hazeState,
            floatingActionButton = {
                val color = MaterialTheme.colorScheme.primaryContainer

                FloatingActionButton(
                    onClick = { showDialog = true },
                    modifier = Modifier
                        .sharedBounds(
                            rememberSharedContentState(GameSharedTransitionKey),
                            animatedVisibilityScope,
                            boundsTransform = asymmetricBoundsTransform,
                            clipInOverlayDuringTransition = OverlayClip(rememberDisplayRoundedCornerShape())
                        )
                        .padding(paddingValues - WindowInsets.navigationBars.asPaddingValues())
                        .clip(rememberDisplayRoundedCornerShape()) // Can't rely on `shape` because of the blur.
                        .hazeEffect(hazeState) {
                            tints = listOf(HazeDefaults.tint(color))
                        },
                    containerColor = Color.Transparent,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Novo Jogo")
                }
            }
        ) { innerPaddingValues ->
            LazyColumn(
                state = lazyListState,
                contentPadding = paddingValues
            ) {
                items(games) { game ->
                    val scoreboard = game.calculateScoreboard()

                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                    ) {
                        Text("${game.player1.name} vs. ${game.player2.name}")
                        Text("${scoreboard.player1Score} - ${scoreboard.player2Score}")
                        Text("S: ${scoreboard.player1SetScore} - ${scoreboard.player2SetScore}")
                    }
                }
            }

            if (showDialog) {
                GameSettingsDialog({ showDialog = false }, { pointCount, setCount ->
                    val player1 = Player(Uuid.random(), "Jogador Um")
                    val player2 = Player(Uuid.random(), "Jogador Dois")

                    val rules = GameRules(pointCount, setCount, 2)

                    onStartGame(player1, player2, rules)
                })
            }
        }
    }
}

class HistoryViewModel : ViewModel() {
    var _games = MutableStateFlow<List<Game>>(emptyList())
    val games = _games.asStateFlow()

    fun addGame(game: Game) {
        _games.update { list -> list + game }
    }
}
