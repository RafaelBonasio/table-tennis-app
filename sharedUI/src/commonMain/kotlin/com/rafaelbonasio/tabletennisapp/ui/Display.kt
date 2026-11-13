package com.rafaelbonasio.tabletennisapp.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

val LocalDisplayCornerRadiusFallback = compositionLocalOf { 16.dp } // TODO: Fine-tune default.

@Composable
expect fun rememberDisplayCornerRadius(): Dp

@Composable
fun rememberDisplayRoundedCornerShape(): RoundedCornerShape {
    val cornerRadius = rememberDisplayCornerRadius()

    return remember(cornerRadius) {
        RoundedCornerShape(cornerRadius)
    }
}

fun PaddingValues.fallBack(fallback: Dp): PaddingValues {
    var left = calculateStartPadding(LayoutDirection.Ltr)
    var right = calculateEndPadding(LayoutDirection.Ltr)
    var top = calculateTopPadding()
    var bottom = calculateBottomPadding()

    left = if (left == 0.dp) fallback else left
    right = if (right == 0.dp) fallback else right
    top = if (top == 0.dp) fallback else top
    bottom = if (bottom == 0.dp) fallback else bottom

    return PaddingValues(left, top, right, bottom)
}
