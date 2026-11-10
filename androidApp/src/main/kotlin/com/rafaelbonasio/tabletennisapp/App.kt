package com.rafaelbonasio.tabletennisapp

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.rafaelbonasio.tabletennisapp.core.MatchEvent
import com.rafaelbonasio.tabletennisapp.ui.noneTransitionSpec
import com.rafaelbonasio.tabletennisapp.ui.theme.TableTennisAppTheme
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun App() {
    val backStack = rememberNavBackStack(RootNavKey)

    val settingsViewModel: SettingsViewModel = koinViewModel()
    val settingsState by settingsViewModel.uiState.collectAsStateWithLifecycle()

    SharedTransitionLayout {
        TableTennisAppTheme(settingsState.theme, settingsState.isDynamicColorEnabled, settingsState.blurRadiusDp.dp) {
            NavDisplay(
                backStack = backStack,
                entryProvider = entryProvider {
                    entry<RootNavKey> {
                        val coroutineScope = rememberCoroutineScope()

                        RootScreen(
                            this@SharedTransitionLayout,
                            { backStack.add(SettingsNavKey) },
                            listOf(
                                Tab(
                                    stringResource(R.string.scoreboard_title),
                                    Icons.Default.DateRange,
                                    { paddingValues ->
                                        val historyViewModel: HistoryViewModel = koinViewModel()
                                        val matches by historyViewModel.matches.collectAsStateWithLifecycle()

                                        HistoryTab(
                                            matches,
                                            this@SharedTransitionLayout,
                                            { id, player1, player2, rules ->
                                                coroutineScope.launch {
                                                    historyViewModel.newMatch(id, player1, player2, rules)
                                                    backStack.add(MatchNavKey(id))
                                                }
                                            },
                                            { match ->
                                                backStack.add(MatchNavKey(match.id))
                                            },
                                            paddingValues
                                        )
                                    }
                                ),

                                getPlaceholderTab(),
                                getPlaceholderTab()
                            )
                        )
                    }

                    entry<SettingsNavKey> {
                        val uiState by settingsViewModel.uiState.collectAsStateWithLifecycle()

                        SettingsScreen(
                            uiState,
                            this@SharedTransitionLayout,
                            { backStack.removeLastOrNull() },
                            settingsViewModel::updateTheme,
                            settingsViewModel::updateDynamicColor,
                            settingsViewModel::updateBlurRadius
                        )
                    }

                    entry<MatchNavKey> { navKey ->
                        val matchViewModel: MatchViewModel = koinViewModel(key = navKey.matchId.toString()) {
                            parametersOf(navKey.matchId)
                        }
                        val uiState by matchViewModel.uiState.collectAsStateWithLifecycle()

                        MatchScreen(
                            uiState,
                            this@SharedTransitionLayout,
                            { backStack.removeLastOrNull() },
                            {
                                matchViewModel.addEvent(MatchEvent.Canceled)
                                backStack.removeLastOrNull()
                            },
                            matchViewModel::addEvent
                        )
                    }
                },

                // Rely entirely on shared bounds transition animations:
                transitionSpec = { noneTransitionSpec },
                popTransitionSpec = { noneTransitionSpec },
                predictivePopTransitionSpec = { noneTransitionSpec }
            )
        }
    }
}

@Composable
fun BackButton(onNavigateBack: () -> Unit) {
    IconButton(onClick = onNavigateBack) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.action_back))
    }
}
