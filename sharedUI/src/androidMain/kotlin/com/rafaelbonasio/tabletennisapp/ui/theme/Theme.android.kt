package com.rafaelbonasio.tabletennisapp.ui.theme

import android.graphics.Color
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView

@Composable
actual fun rememberColorScheme(isThemeLight: Boolean, dynamicColor: Boolean): ColorScheme {
    return when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (!isThemeLight) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        !isThemeLight -> DarkColorScheme
        else -> LightColorScheme
    }
}

@Composable
actual fun SystemBarStyleEffect(isThemeLight: Boolean) {
    val view = LocalView.current
    LaunchedEffect(isThemeLight) {
        val activity = view.context as ComponentActivity

        val style = if (isThemeLight) {
            SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        } else {
            SystemBarStyle.dark(Color.TRANSPARENT)
        }

        activity.enableEdgeToEdge(style, style)
    }
}