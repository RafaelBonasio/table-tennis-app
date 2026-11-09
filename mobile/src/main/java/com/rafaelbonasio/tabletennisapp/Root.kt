package com.rafaelbonasio.tabletennisapp

import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
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
fun RootScreen(
    sharedTransitionScope: SharedTransitionScope,
    onNavigateSettings: () -> Unit,
    tabs: List<Tab>
) {
    with (sharedTransitionScope) {
        val hazeState = rememberHazeState()

        val pagerState = rememberPagerState(pageCount = tabs::size)

        FancyScaffold(
            hazeState = hazeState,
            topBar = {
                FancyTopAppBar(
                    title = {
                        Text(tabs[pagerState.currentPage].label)
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
                    tabs.map { tab -> FancyNavigationBarItem(tab.label, tab.icon) },
                    pagerState,
                    hazeState
                )
            }
        ) { paddingValues ->
            HorizontalPager(
                state = pagerState,
                beyondViewportPageCount = tabs.size // Prevents stuttering when switching tabs.
            ) { pageIndex ->
                tabs[pageIndex].content(paddingValues)
            }
        }
    }
}

data class Tab(val label: String, val icon: ImageVector, val content: @Composable (PaddingValues) -> Unit)

@Composable
fun getPlaceholderTab(): Tab {
    return Tab(stringResource(R.string.placeholder_page_title), Icons.Default.Lock, { PlaceholderTab() })
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
