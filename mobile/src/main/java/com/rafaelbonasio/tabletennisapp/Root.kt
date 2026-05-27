package com.rafaelbonasio.tabletennisapp

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import com.rafaelbonasio.tabletennisapp.core.GameRules
import com.rafaelbonasio.tabletennisapp.core.Player
import com.rafaelbonasio.tabletennisapp.ui.FancyNavigationBar
import com.rafaelbonasio.tabletennisapp.ui.FancyNavigationBarItem
import com.rafaelbonasio.tabletennisapp.ui.FancyScaffold
import com.rafaelbonasio.tabletennisapp.ui.FancyTopAppBar
import com.rafaelbonasio.tabletennisapp.ui.asymmetricBoundsTransform
import com.rafaelbonasio.tabletennisapp.ui.rememberDisplayRoundedCornerShape
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.serialization.Serializable

@Serializable
data object Root : NavKey

@Composable
fun RootScreen(sharedTransitionScope: SharedTransitionScope, animatedVisibilityScope: AnimatedVisibilityScope, onNavigateSettings: () -> Unit, onStartGame: (Player, Player, GameRules) -> Unit) {
    with (sharedTransitionScope) {
        val hazeState = rememberHazeState()

        val pagerState = rememberPagerState(pageCount = Tab.entries::size)

        FancyScaffold(
            hazeState = hazeState,
            topBar = {
                FancyTopAppBar(
                    title = {
                        Text(Tab.entries[pagerState.currentPage].label)
                    },
                    hazeState = hazeState,
                    actions = {
                        IconButton(
                            onClick = onNavigateSettings,
                            modifier = Modifier
                                .sharedBounds(
                                    rememberSharedContentState(SettingsSharedTransitionKey),
                                    animatedVisibilityScope,
                                    boundsTransform = asymmetricBoundsTransform,
                                    clipInOverlayDuringTransition = OverlayClip(
                                        rememberDisplayRoundedCornerShape()
                                    )
                                )
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = "Configurações")
                        }
                    }
                )
            },
            bottomBar = {
                FancyNavigationBar(
                    Tab.entries.map { tab -> FancyNavigationBarItem(tab.label, tab.icon) },
                    pagerState,
                    hazeState
                )
            }
        ) { paddingValues ->
            HorizontalPager(
                state = pagerState
            ) { pageIndex ->
                when (Tab.entries[pageIndex]) {
                    Tab.Scoreboard -> {
                        HistoryTab(
                            sharedTransitionScope,
                            animatedVisibilityScope,
                            onStartGame,
                            paddingValues
                        )
                    }

                    Tab.Tab2, Tab.Tab3 -> {
                        PlaceholderTab()
                    }
                }
            }
        }
    }
}

enum class Tab(val label: String, val icon: ImageVector) {
    Scoreboard("Placar", Icons.Default.DateRange),
    Tab2("?", Icons.Default.Lock),
    Tab3("?", Icons.Default.Lock)
}

@Composable
fun PlaceholderTab() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Eventualmente...")
    }
}
