package com.rafaelbonasio.tabletennisapp

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import androidx.navigation3.ui.NavDisplay
import com.rafaelbonasio.tabletennisapp.ui.noneTransitionSpec
import com.rafaelbonasio.tabletennisapp.ui.theme.TableTennisAppTheme

@PreviewScreenSizes
@Composable
fun App() {
    val backStack = rememberNavBackStack(Root)

    val settingsViewModel: SettingsViewModel = viewModel()
    val settingsState by settingsViewModel.uiState.collectAsStateWithLifecycle()

    val historyViewModel: HistoryViewModel = viewModel()

    val gameViewModel: GameViewModel = viewModel()

    SharedTransitionLayout {
        TableTennisAppTheme(settingsState.theme, settingsState.isDynamicColorEnabled, settingsState.blurRadius) {
            NavDisplay(
                backStack = backStack,
                entryProvider = entryProvider {
                    entry<Root> {
                        RootScreen(this@SharedTransitionLayout,
                            LocalNavAnimatedContentScope.current, { backStack.add(Settings) }, { player1, player2, rules -> backStack.add(Game); gameViewModel.newGame(player1, player2, rules) })
                    }

                    entry<Settings> {
                        SettingsPage(settingsViewModel, this@SharedTransitionLayout, LocalNavAnimatedContentScope.current, { backStack.removeLastOrNull() })
                    }

                    entry<Game> {
                        GamePage(this@SharedTransitionLayout, LocalNavAnimatedContentScope.current, { backStack.removeLastOrNull() }, historyViewModel::addGame)
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
        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar")
    }
}
