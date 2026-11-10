package com.rafaelbonasio.tabletennisapp

import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.rafaelbonasio.tabletennisapp.ui.LocalMinimumPadding
import com.rafaelbonasio.tabletennisapp.ui.asymmetricBoundsTransform
import com.rafaelbonasio.tabletennisapp.ui.hazeTint
import com.rafaelbonasio.tabletennisapp.ui.rememberDisplayRoundedCornerShape
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.blur.HazeBlurStyle
import dev.chrisbanes.haze.blur.hazeBlur
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlin.random.Random
import kotlin.uuid.Uuid

@Composable
fun HistoryTab(matches: List<Match>, sharedTransitionScope: SharedTransitionScope, onStartMatch: (id: Uuid, player1: Player, player2: Player, rules: MatchRules) -> Unit, onNavigateToMatch: (Match) -> Unit, paddingValues: PaddingValues) {
    var showDialog by remember { mutableStateOf(false) }

    var pendingMatchId by remember { mutableStateOf(Uuid.NIL) }

    val lazyListState = rememberLazyListState()

    val hazeState = rememberHazeState()

    val sortedMatches = matches.sortedBy { match ->
        match.calculateScoreboard().isFinished
    }

    LaunchedEffect(pendingMatchId, sortedMatches) {
        if (pendingMatchId != Uuid.NIL) {
            val matchIndex = sortedMatches.indexOfFirst { it.id == pendingMatchId }
            if (matchIndex != -1) {
                lazyListState.animateScrollToItem(matchIndex)
            }
        }
    }

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
                    onClick = {
                        pendingMatchId = Uuid.random()
                        showDialog = true
                    },
                    modifier = Modifier
                        .sharedBounds(
                            rememberSharedContentState(MatchSharedTransitionKey(pendingMatchId)),
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
                contentPadding = paddingValues,
                verticalArrangement = Arrangement.spacedBy(LocalMinimumPadding.current)
            ) {
                items(sortedMatches) { match ->
                    val scoreboard = match.calculateScoreboard()

                    OutlinedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .sharedBounds( // TODO: Stop it from overlaying the Scaffold's slots, but keep it overlaying the other cards.
                                rememberSharedContentState(MatchSharedTransitionKey(match.id)),
                                LocalNavAnimatedContentScope.current,
                                boundsTransform = asymmetricBoundsTransform
                            )
                            .clickable(
                                enabled = !scoreboard.isFinished,
                                onClick = { onNavigateToMatch(match) }
                            ),
                        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                        border = if (scoreboard.isFinished) CardDefaults.outlinedCardBorder() else BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(LocalMinimumPadding.current),
                            horizontalArrangement = Arrangement.spacedBy(LocalMinimumPadding.current)
                        ) {
                            Column {
                                Row {
                                    Text(
                                        match.player1.name,
                                        modifier = Modifier.sharedBounds(
                                            rememberSharedContentState(MatchSharedElementKey(match.id, MatchSharedElementKeyElementType.Player1Name)),
                                            LocalNavAnimatedContentScope.current,
                                            boundsTransform = asymmetricBoundsTransform,
                                            renderInOverlayDuringTransition = false
                                        )
                                    )
                                    Text(" vs. ")
                                    Text(
                                        match.player2.name,
                                        modifier = Modifier.sharedBounds(
                                            rememberSharedContentState(MatchSharedElementKey(match.id, MatchSharedElementKeyElementType.Player2Name)),
                                            LocalNavAnimatedContentScope.current,
                                            boundsTransform = asymmetricBoundsTransform,
                                            renderInOverlayDuringTransition = false
                                        )
                                    )
                                }

                                Row {
                                    Text(
                                        scoreboard.player1PointScore.toString(),
                                        modifier = Modifier.sharedBounds(
                                            rememberSharedContentState(MatchSharedElementKey(match.id, MatchSharedElementKeyElementType.Player1PointScore)),
                                            LocalNavAnimatedContentScope.current,
                                            boundsTransform = asymmetricBoundsTransform,
                                            renderInOverlayDuringTransition = false
                                        )
                                    )
                                    Text(" - ")
                                    Text(
                                        scoreboard.player2PointScore.toString(),
                                        modifier = Modifier.sharedBounds(
                                            rememberSharedContentState(MatchSharedElementKey(match.id, MatchSharedElementKeyElementType.Player2PointScore)),
                                            LocalNavAnimatedContentScope.current,
                                            boundsTransform = asymmetricBoundsTransform,
                                            renderInOverlayDuringTransition = false
                                        )
                                    )
                                }

                                Row {
                                    Text("S: ")
                                    Text(
                                        scoreboard.player1GameScore.toString(),
                                        modifier = Modifier.sharedBounds(
                                            rememberSharedContentState(MatchSharedElementKey(match.id, MatchSharedElementKeyElementType.Player1GameScore)),
                                            LocalNavAnimatedContentScope.current,
                                            boundsTransform = asymmetricBoundsTransform,
                                            renderInOverlayDuringTransition = false
                                        )
                                    )
                                    Text(" - ")
                                    Text(
                                        scoreboard.player2GameScore.toString(),
                                        modifier = Modifier.sharedBounds(
                                            rememberSharedContentState(MatchSharedElementKey(match.id, MatchSharedElementKeyElementType.Player2GameScore)),
                                            LocalNavAnimatedContentScope.current,
                                            boundsTransform = asymmetricBoundsTransform,
                                            renderInOverlayDuringTransition = false
                                        )
                                    )
                                }
                            }

                            Spacer(Modifier.weight(1f))

                            if (!scoreboard.isFinished) {
                                Card(
                                    colors = CardDefaults.outlinedCardColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Text(
                                        text = stringResource(R.string.match_in_progress),
                                        modifier = Modifier.padding(LocalMinimumPadding.current),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (showDialog) {
                MatchSettingsDialog({ showDialog = false }, { pointCount, setCount ->
                    val player1 = Player(Uuid.random(), "Jogador Um")
                    val player2 = Player(Uuid.random(), "Jogador Dois")

                    val rules = MatchRules(pointCount, setCount, 2)

                    onStartMatch(pendingMatchId, player1, player2, rules)
                })
            }
        }
    }
}

class HistoryViewModel(private val matchRepository: MatchRepository) : ViewModel() {
    val matches = matchRepository.matches
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    suspend fun newMatch(id: Uuid, player1: Player, player2: Player, rules: MatchRules) {
        val match = Match(id, player1, player2, rules, Random.nextBoolean())
        matchRepository.saveMatch(match)
    }
}
