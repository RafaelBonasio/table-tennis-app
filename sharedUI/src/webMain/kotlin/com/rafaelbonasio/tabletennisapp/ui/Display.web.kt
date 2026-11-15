package com.rafaelbonasio.tabletennisapp.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp

@Composable
actual fun rememberDisplayCornerRadius(): Dp {
    return LocalDisplayCornerRadiusFallback.current
}