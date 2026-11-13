package com.rafaelbonasio.tabletennisapp.ui

import android.os.Build
import android.view.RoundedCorner
import android.view.View
import androidx.annotation.Px
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp

@Composable
actual fun rememberDisplayCornerRadius(): Dp {
    val fallback = LocalDisplayCornerRadiusFallback.current

    val density = LocalDensity.current
    val view = LocalView.current

    return remember(
        fallback,
        density,
        view,

        // TODO: Keying to `Configuration` doesn't work.
        //       Gemini: System race condition — `WindowInsets` is dispatched asynchronously and arrives after `Configuration`.
        LocalConfiguration.current
    ) {
        with (density) { view.getCornerRadius()?.toDp() } ?: fallback
    }
}

/**
 * @return Radius of this [View]'s rounded corners. `null` if:
 * - the [View] has no rounded corners; or
 * - the current Android version doesn't support querying rounded corners.
 */
@Px
fun View.getCornerRadius(): Int? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        this.rootWindowInsets?.getRoundedCorner(RoundedCorner.POSITION_TOP_LEFT)?.radius
    } else {
        null
    }
}