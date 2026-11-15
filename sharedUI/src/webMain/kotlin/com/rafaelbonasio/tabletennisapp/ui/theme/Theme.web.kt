package com.rafaelbonasio.tabletennisapp.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

@Composable
actual fun rememberColorScheme(
    isThemeLight: Boolean,
    dynamicColor: Boolean // Not supported on Web.
): ColorScheme {
    return if (isThemeLight) LightColorScheme else DarkColorScheme
}

@Composable
actual fun SystemBarStyleEffect(isThemeLight: Boolean) {
    // No-op on Web.
}