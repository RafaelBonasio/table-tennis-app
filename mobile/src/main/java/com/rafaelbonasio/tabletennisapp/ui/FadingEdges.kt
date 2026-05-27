package com.rafaelbonasio.tabletennisapp.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer

fun Modifier.verticalFadingEdges(
    paddingValues: PaddingValues
): Modifier = this.graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen).drawWithContent {
    drawContent()

    // Top:
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color.Transparent, Color.Black),
            startY = 0f,
            endY = paddingValues.calculateTopPadding().toPx()
        ),
        blendMode = BlendMode.DstIn
    )

    // Bottom:
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color.Black, Color.Transparent),
            startY = size.height - paddingValues.calculateBottomPadding().toPx(),
            endY = size.height
        ),
        blendMode = BlendMode.DstIn
    )
}
