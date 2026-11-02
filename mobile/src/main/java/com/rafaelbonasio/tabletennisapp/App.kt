package com.rafaelbonasio.tabletennisapp

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.rafaelbonasio.tabletennisapp.ui.noneTransitionSpec
import com.rafaelbonasio.tabletennisapp.ui.theme.TableTennisAppTheme
import org.koin.androidx.compose.koinViewModel

@PreviewScreenSizes
@Composable
fun App() {
    val backStack = rememberNavBackStack(RootNavKey)

    val settingsViewModel: SettingsViewModel = koinViewModel()
    val settingsState by settingsViewModel.uiState.collectAsStateWithLifecycle()

    val historyViewModel: HistoryViewModel = koinViewModel()

    val gameViewModel: GameViewModel = koinViewModel()

    SharedTransitionLayout {
        TableTennisAppTheme(settingsState.theme, settingsState.isDynamicColorEnabled, settingsState.blurRadiusDp.dp) {
            NavDisplay(
                backStack = backStack,
                entryProvider = entryProvider {
                    entry<RootNavKey> {
                        val games by historyViewModel.games.collectAsStateWithLifecycle()

                        RootScreen(
                            games,
                            this@SharedTransitionLayout,
                            { backStack.add(SettingsNavKey) },
                            { player1, player2, rules -> backStack.add(GameNavKey); gameViewModel.newGame(player1, player2, rules) }
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

                    entry<GameNavKey> {
                        val uiState by gameViewModel.uiState.collectAsStateWithLifecycle()

                        GameScreen(
                            uiState,
                            gameViewModel.game,
                            this@SharedTransitionLayout,
                            { backStack.removeLastOrNull() },
                            historyViewModel::addGame,
                            gameViewModel::addEvent
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
