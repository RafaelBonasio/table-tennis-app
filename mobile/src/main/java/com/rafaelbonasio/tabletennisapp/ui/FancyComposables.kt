package com.rafaelbonasio.tabletennisapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FancyTopAppBar(
    title: @Composable () -> Unit,
    hazeState: HazeState,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    val backgroundColor = MaterialTheme.colorScheme.surfaceContainer

    TopAppBar(
        title = title,
        modifier = Modifier
            .hazeEffect(hazeState) {
                tints = listOf(HazeDefaults.tint(backgroundColor))
                progressive = HazeProgressive.verticalGradient(startIntensity = 1f, endIntensity = 0f)
            },
        navigationIcon = navigationIcon,
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
    )
}

@Composable
fun FancyNavigationBar(
    items: List<FancyNavigationBarItem>,
    pagerState: PagerState,
    hazeState: HazeState
) {
    val backgroundColor = MaterialTheme.colorScheme.surfaceContainer

    NavigationBar(
        modifier = Modifier.padding(horizontal = LocalMinimumPadding.current * 2),
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.contentColorFor(backgroundColor)
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .clip(rememberDisplayRoundedCornerShape())
                .hazeEffect(hazeState) {
                    tints = listOf(HazeDefaults.tint(backgroundColor))
                }
        ) {
            val tabWidth = maxWidth / items.size

            Box(Modifier.height(IntrinsicSize.Max)) {
                // Indicator:
                Box(Modifier
                    .width(tabWidth)
                    .fillMaxHeight()
                    .graphicsLayer {
                        val pageOffset = pagerState.currentPage + pagerState.currentPageOffsetFraction

                        translationX = tabWidth.toPx() * pageOffset
                    }
                    .clip(rememberDisplayRoundedCornerShape())
                    .background(MaterialTheme.colorScheme.secondaryContainer.copy(HazeDefaults.tintAlpha))
                )

                Row {
                    val coroutineScope = rememberCoroutineScope()

                    items.forEachIndexed { index, item ->
                        Box(Modifier
                            .width(tabWidth)
                            .clickable(onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } })
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(item.icon, item.label)
                                Text(item.label)
                            }
                        }
                    }
                }
            }
        }
    }
}

data class FancyNavigationBarItem(
    val label: String,
    val icon: ImageVector
)

val LocalMinimumPadding = compositionLocalOf { 8.dp } // TODO: Fine-tune default.

@Composable
fun FancyScaffold(hazeState: HazeState, modifier: Modifier = Modifier, topBar: @Composable () -> Unit = {}, bottomBar: @Composable () -> Unit = {}, floatingActionButton: @Composable () -> Unit = {}, content: @Composable (PaddingValues) -> Unit) {
    Scaffold(
        modifier,
        topBar = topBar,
        bottomBar = bottomBar,
        floatingActionButton = floatingActionButton
    ) { paddingValues ->
        val contentPadding = paddingValues.fallBack(LocalMinimumPadding.current)

        Box(modifier = Modifier
            .fillMaxSize()
            .hazeSource(hazeState)
            .background(MaterialTheme.colorScheme.background) // Haze doesn't work properly otherwise.
        ) {
            // Second wrapper required for the top fading edge to work. TODO: Figure out why this doesn't happen with the bottom edge.
            Box(Modifier.verticalFadingEdges(contentPadding)) {
                content(contentPadding)
            }
        }
    }
}
