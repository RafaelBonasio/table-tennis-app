package com.rafaelbonasio.tabletennisapp.ui.theme

import android.graphics.Color
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.LocalHazeStyle

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun TableTennisAppTheme(
    theme: Theme,
    dynamicColor: Boolean,
    blurRadius: Dp,
    content: @Composable () -> Unit
) {
    val isThemeLight = theme.isLight()

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (!isThemeLight) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        !isThemeLight -> DarkColorScheme
        else -> LightColorScheme
    }

    // Update system bars:
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

    CompositionLocalProvider(
        LocalHazeStyle provides HazeStyle(tint = null, blurRadius = blurRadius, noiseFactor = 0f)
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

enum class Theme {
    DARK,
    LIGHT,
    SYSTEM;

    @Composable
    fun isLight(): Boolean {
        return when (this) {
            DARK -> false
            LIGHT -> true
            SYSTEM -> !isSystemInDarkTheme()
        }
    }
}
