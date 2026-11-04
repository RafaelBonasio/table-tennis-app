package com.rafaelbonasio.tabletennisapp

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.rafaelbonasio.tabletennisapp.core.Match
import com.rafaelbonasio.tabletennisapp.core.MatchRules
import com.rafaelbonasio.tabletennisapp.core.Player
import com.rafaelbonasio.tabletennisapp.data.MatchRepository
import com.rafaelbonasio.tabletennisapp.ui.FancyScaffold
import com.rafaelbonasio.tabletennisapp.ui.asymmetricBoundsTransform
import com.rafaelbonasio.tabletennisapp.ui.hazeTint
import com.rafaelbonasio.tabletennisapp.ui.rememberDisplayRoundedCornerShape
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.blur.HazeBlurStyle
import dev.chrisbanes.haze.blur.hazeBlur
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlin.uuid.Uuid

@Composable
fun HistoryTab(matches: List<Match>, sharedTransitionScope: SharedTransitionScope, onStartMatch: (player1: Player, player2: Player, rules: MatchRules) -> Unit, paddingValues: PaddingValues) {
    var showDialog by remember { mutableStateOf(false) }

    val lazyListState = rememberLazyListState()

    val hazeState = rememberHazeState()

    with (sharedTransitionScope) {
        FancyScaffold(
            hazeState = hazeState,
            floatingActionButton = {
                val color = MaterialTheme.colorScheme.primaryContainer
                val hazeBlurStyle = remember(color) {
                    HazeBlurStyle {
                        colorEffects(hazeTint(color))
                    }
                }

                FloatingActionButton(
                    onClick = { showDialog = true },
                    modifier = Modifier
                        .sharedBounds(
                            rememberSharedContentState(MatchSharedTransitionKey),
                            LocalNavAnimatedContentScope.current,
                            boundsTransform = asymmetricBoundsTransform,
                            clipInOverlayDuringTransition = OverlayClip(rememberDisplayRoundedCornerShape())
                        )
                        .padding(paddingValues - WindowInsets.navigationBars.asPaddingValues())
                        .clip(rememberDisplayRoundedCornerShape()) // Can't rely on `shape` because of the blur.
                        .hazeBlur(HazeInput.Sources(hazeState), hazeBlurStyle),
                    containerColor = Color.Transparent,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.scoreboard_new_match))
                }
            }
        ) { innerPaddingValues ->
            LazyColumn(
                state = lazyListState,
                contentPadding = paddingValues
            ) {
                items(matches) { match ->
                    val scoreboard = match.calculateScoreboard()

                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                    ) {
                        Text("${match.player1.name} vs. ${match.player2.name}")
                        Text("${scoreboard.player1PointScore} - ${scoreboard.player2PointScore}")
                        Text("S: ${scoreboard.player1GameScore} - ${scoreboard.player2GameScore}")
                    }
                }
            }

            if (showDialog) {
                MatchSettingsDialog({ showDialog = false }, { pointCount, setCount ->
                    val player1 = Player(Uuid.random(), "Jogador Um")
                    val player2 = Player(Uuid.random(), "Jogador Dois")

                    val rules = MatchRules(pointCount, setCount, 2)

                    onStartMatch(player1, player2, rules)
                })
            }
        }
    }
}

class HistoryViewModel(private val matchRepository: MatchRepository) : ViewModel() {
    val matches = matchRepository.matches
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    suspend fun newMatch(player1: Player, player2: Player, rules: MatchRules): Uuid {
        val match = Match(Uuid.random(), player1, player2, rules)
        matchRepository.saveMatch(match)
        return match.id
    }
}
