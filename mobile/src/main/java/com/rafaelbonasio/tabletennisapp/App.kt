package com.rafaelbonasio.tabletennisapp

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.rafaelbonasio.tabletennisapp.ui.theme.TableTennisAppTheme
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@PreviewScreenSizes
@Composable
fun App() {
    val backStack = rememberNavBackStack(Screen.Root)

    var theme by rememberSaveable { mutableStateOf(Theme.System) }

    val historyViewModel: HistoryViewModel = viewModel()
    val gameViewModel: GameViewModel = viewModel()

    TableTennisAppTheme(darkTheme = !theme.isLight(), true) {
        NavDisplay(
            backStack = backStack,

            entryProvider = { key ->
                when (key) {
                    is Screen.Root -> NavEntry(key) {
                        RootScreen({ backStack.add(Screen.Settings) }, { player1, player2, rules -> backStack.add(Screen.Game); gameViewModel.newGame(player1, player2, rules) })
                    }

                    is Screen.Settings -> NavEntry(key) {
                        SettingsPage(theme, { newTheme -> theme = newTheme }, { backStack.removeLastOrNull() })
                    }

                    is Screen.Game -> NavEntry(key) {
                        GamePage({ backStack.removeLastOrNull() }, historyViewModel::addGame)
                    }

                    else -> error("Unknown route: $key")
                }
            }
        )
    }
}

@Serializable
sealed class Screen(val title: String? = null, val iconId: Int? = null) : NavKey {
    @Serializable
    data object Root : Screen()

    @Serializable
    data object Settings : Screen("Configurações", R.drawable.ic_account_box)

    @Serializable
    data object History : Screen("Histórico", R.drawable.ic_favorite) {}

    @Serializable
    data object Game : Screen("Jogo")
}

@Composable
fun BackButton(onNavigateBack: () -> Unit) {
    IconButton(onClick = onNavigateBack) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar")
    }
}
