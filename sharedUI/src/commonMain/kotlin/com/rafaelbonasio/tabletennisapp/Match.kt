package com.rafaelbonasio.tabletennisapp

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.rafaelbonasio.tabletennisapp.core.MatchEvent
import com.rafaelbonasio.tabletennisapp.core.Scoreboard
import com.rafaelbonasio.tabletennisapp.data.MatchRepository
import com.rafaelbonasio.tabletennisapp.ui.FancyScaffold
import com.rafaelbonasio.tabletennisapp.ui.FancyTopAppBar
import com.rafaelbonasio.tabletennisapp.ui.asymmetricBoundsTransform
import com.rafaelbonasio.tabletennisapp.ui.rememberDisplayRoundedCornerShape
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import kotlin.uuid.Uuid

@Serializable
data class MatchNavKey(val matchId: Uuid) : NavKey

data class MatchSharedTransitionKey(val matchId: Uuid)

data class MatchUiState(
    val matchId: Uuid = Uuid.NIL,
    val scoreboard: Scoreboard = Scoreboard(),
    val player1Name: String = "",
    val player2Name: String = ""
)

@Composable
fun MatchScreen(
    state: MatchUiState,
    sharedTransitionScope: SharedTransitionScope,
    onNavigateBack: () -> Unit,
    onCancelMatch: () -> Unit,
    onAddEvent: (MatchEvent) -> Unit
) {
    val hazeState = rememberHazeState()

    with (sharedTransitionScope) {
        Box(modifier = Modifier.sharedBounds(
            rememberSharedContentState(MatchSharedTransitionKey(state.matchId)),
            LocalNavAnimatedContentScope.current,
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
                            Text(stringResource(Res.string.match_title))
                        }
                    )
                },
                bottomBar = {
                    BottomAppBar(
                        actions = {
                            IconButton({ onAddEvent(MatchEvent.Undo) }) {
                                Icon(Icons.Default.KeyboardArrowLeft, stringResource(Res.string.action_undo))
                            }
                            IconButton({ onAddEvent(MatchEvent.Redo) }) {
                                Icon(Icons.Default.Refresh, stringResource(Res.string.action_redo))
                            }
                        },
                        floatingActionButton = {
                            FloatingActionButton(
                                { onCancelMatch() },
                                shape = rememberDisplayRoundedCornerShape()
                            ) {
                                Icon(Icons.Default.ExitToApp, stringResource(Res.string.action_finish))
                            }
                        },
                        containerColor = Color.Transparent
                    )
                }
            ) {
                Row(Modifier.padding(it).clip(rememberDisplayRoundedCornerShape())) {
                    PlayerScoreCard(
                        state.player1Name,
                        state.scoreboard.player1PointScore,
                        state.scoreboard.player1GameScore,
                        !state.scoreboard.isPlayer2Serving,
                        { onAddEvent(MatchEvent.Player1Scored) },
                        sharedTransitionScope,
                        MatchSharedElementKey(state.matchId, MatchSharedElementKeyElementType.Player1Name),
                        MatchSharedElementKey(state.matchId, MatchSharedElementKeyElementType.Player1PointScore),
                        MatchSharedElementKey(state.matchId, MatchSharedElementKeyElementType.Player1GameScore)
                    )

                    PlayerScoreCard(
                        state.player2Name,
                        state.scoreboard.player2PointScore,
                        state.scoreboard.player2GameScore,
                        state.scoreboard.isPlayer2Serving,
                        { onAddEvent(MatchEvent.Player2Scored) },
                        sharedTransitionScope,
                        MatchSharedElementKey(state.matchId, MatchSharedElementKeyElementType.Player2Name),
                        MatchSharedElementKey(state.matchId, MatchSharedElementKeyElementType.Player2PointScore),
                        MatchSharedElementKey(state.matchId, MatchSharedElementKeyElementType.Player2GameScore)
                    )
                }
            }
        }

    }

    LaunchedEffect(state.scoreboard.isFinished) {
        if (state.scoreboard.isFinished) {
            onNavigateBack()
        }
    }
}

@Composable
private fun RowScope.PlayerScoreCard(
    name: String,
    points: Int,
    setsScore: Int,
    isServing: Boolean,
    onClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    textSharedKey: MatchSharedElementKey,
    pointScoreSharedKey: MatchSharedElementKey,
    gameScoreSharedKey: MatchSharedElementKey
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
            with (sharedTransitionScope) {
                Text(
                    name,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.sharedBounds(
                        rememberSharedContentState(textSharedKey),
                        LocalNavAnimatedContentScope.current,
                        boundsTransform = asymmetricBoundsTransform,
                        renderInOverlayDuringTransition = false
                    )
                )

                Text(
                    points.toString(),
                    modifier = Modifier.sharedBounds(
                        rememberSharedContentState(pointScoreSharedKey),
                        LocalNavAnimatedContentScope.current,
                        boundsTransform = asymmetricBoundsTransform,
                        renderInOverlayDuringTransition = false
                    ),
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    setsScore.toString(),
                    modifier = Modifier.sharedBounds(
                        rememberSharedContentState(gameScoreSharedKey),
                        LocalNavAnimatedContentScope.current,
                        boundsTransform = asymmetricBoundsTransform,
                        renderInOverlayDuringTransition = false
                    ),
                    style = MaterialTheme.typography.displaySmall
                )
            }
        }
    }
}

@Composable
fun MatchSettingsDialog(onDismiss: () -> Unit, onConfirm: (gameSize: Int, matchSize: Int) -> Unit) {
    val gameSizeFieldState = rememberTextFieldState("11")
    val matchSizeFieldState = rememberTextFieldState("3")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.match_settings_title)) },
        text = {
            Column {
                Text(stringResource(Res.string.match_settings_number_of_points))
                TextField(gameSizeFieldState)

                Text(stringResource(Res.string.match_settings_number_of_games))
                TextField(matchSizeFieldState)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.action_cancel))
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(gameSizeFieldState.text.toString().toInt(), matchSizeFieldState.text.toString().toInt())
                onDismiss()
            }) {
                Text(stringResource(Res.string.match_settings_start))
            }
        }
    )
}

class MatchViewModel(private val matchRepository: MatchRepository, matchId: Uuid) : ViewModel() {
    val matchFlow = matchRepository.getMatch(matchId)

    val uiState: StateFlow<MatchUiState> = matchFlow.map { match ->
        MatchUiState(
            match.id,
            match.calculateScoreboard(),
            match.player1.name,
            match.player2.name
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MatchUiState(matchId))

    fun addEvent(event: MatchEvent) {
        viewModelScope.launch {
            val match = matchFlow.first()

            match.events.add(event)

            matchRepository.saveMatch(match)
        }
    }
}

data class MatchSharedElementKey(val matchId: Uuid, val elementType: MatchSharedElementKeyElementType)

enum class MatchSharedElementKeyElementType {
    Player1Name,
    Player2Name,
    Player1PointScore,
    Player2PointScore,
    Player1GameScore,
    Player2GameScore
}
