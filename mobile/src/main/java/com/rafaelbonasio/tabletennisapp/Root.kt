package com.rafaelbonasio.tabletennisapp

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
import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.rafaelbonasio.tabletennisapp.core.Match
import com.rafaelbonasio.tabletennisapp.core.MatchRules
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
data object RootNavKey : NavKey

@Composable
fun RootScreen(matches: List<Match>, sharedTransitionScope: SharedTransitionScope, onNavigateSettings: () -> Unit, onStartMatch: (Player, Player, MatchRules) -> Unit) {
    with (sharedTransitionScope) {
        val hazeState = rememberHazeState()

        val pagerState = rememberPagerState(pageCount = Tab.entries::size)

        FancyScaffold(
            hazeState = hazeState,
            topBar = {
                FancyTopAppBar(
                    title = {
                        Text(stringResource(Tab.entries[pagerState.currentPage].labelStringId))
                    },
                    hazeState = hazeState,
                    actions = {
                        IconButton(
                            onClick = onNavigateSettings,
                            modifier = Modifier
                                .sharedBounds(
                                    rememberSharedContentState(SettingsSharedTransitionKey),
                                    LocalNavAnimatedContentScope.current,
                                    boundsTransform = asymmetricBoundsTransform,
                                    clipInOverlayDuringTransition = OverlayClip(
                                        rememberDisplayRoundedCornerShape()
                                    )
                                )
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.settings_title))
                        }
                    }
                )
            },
            bottomBar = {
                FancyNavigationBar(
                    Tab.entries.map { tab -> FancyNavigationBarItem(stringResource(tab.labelStringId), tab.icon) },
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
                            matches,
                            sharedTransitionScope,
                            onStartMatch,
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

enum class Tab(val labelStringId: Int, val icon: ImageVector) {
    Scoreboard(R.string.scoreboard_title, Icons.Default.DateRange),
    Tab2(R.string.placeholder_page_title, Icons.Default.Lock),
    Tab3(R.string.placeholder_page_title, Icons.Default.Lock)
}

@Composable
fun PlaceholderTab() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(R.string.placeholder_page_text))
    }
}
