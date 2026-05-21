package com.rafaelbonasio.tabletennisapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.rafaelbonasio.tabletennisapp.core.GameRules
import com.rafaelbonasio.tabletennisapp.core.Player

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RootScreen(onNavigateSettings: () -> Unit, onStartGame: (Player, Player, GameRules) -> Unit) {
    var selectedTab by rememberSaveable { mutableStateOf(Tabs.SCOREBOARD) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            Tabs.entries.forEach {
                item(
                    icon = {
                        Icon(
                            painterResource(it.iconId),
                            contentDescription = it.label
                        )
                    },
                    label = { Text(it.label) },
                    selected = it == selectedTab,
                    onClick = { selectedTab = it }
                )
            }
        }
    ) {
        Scaffold(modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = {
                        Text(stringResource(R.string.app_name))
                    },
                    actions = {
                        IconButton(onClick = onNavigateSettings) {
                            Icon(Icons.Default.Settings, contentDescription = "Configurações")
                        }
                    }
                )
            }
        ) { paddingValues ->
            when (selectedTab) {
                Tabs.SCOREBOARD -> { HistoryTab(onStartGame, paddingValues) }
                Tabs.TAB_2, Tabs.TAB_3 -> { PlaceholderTab() }
            }
        }
    }
}

enum class Tabs(
    val label: String,
    val iconId: Int,
) {
    SCOREBOARD("Placar", R.drawable.ic_home),
    TAB_2("Guia 2", R.drawable.ic_favorite),
    TAB_3("Guia 3", R.drawable.ic_account_box)
}

@Composable
fun PlaceholderTab() {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        Text("Eventualmente...")
    }
}
