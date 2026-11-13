package com.rafaelbonasio.tabletennisapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import dev.chrisbanes.haze.blur.HazeBlurStyle
import dev.chrisbanes.haze.blur.LocalHazeBlurStyle

val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

val LightColorScheme = lightColorScheme(
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
expect fun rememberColorScheme(isThemeLight: Boolean, dynamicColor: Boolean): ColorScheme

/**
 * Updates the color scheme of the system bars.
 */
@Composable
expect fun SystemBarStyleEffect(isThemeLight: Boolean)

@Composable
fun TableTennisAppTheme(
    theme: Theme,
    dynamicColor: Boolean,
    blurRadius: Dp,
    content: @Composable () -> Unit
) {
    val isThemeLight = theme.isLight()

    val colorScheme = rememberColorScheme(isThemeLight, dynamicColor)

    SystemBarStyleEffect(isThemeLight)

    CompositionLocalProvider(
        LocalHazeBlurStyle provides remember(blurRadius) {
            HazeBlurStyle {
                blurRadius(blurRadius)
                noiseFactor(0f)
            }
        }
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
