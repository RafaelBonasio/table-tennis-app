package com.rafaelbonasio.tabletennisapp.ui

import android.os.Build
import android.view.RoundedCorner
import android.view.View
import androidx.annotation.Px
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

val LocalDisplayCornerRadiusFallback = compositionLocalOf { 16.dp } // TODO: Fine-tune default.

@Composable
fun rememberDisplayCornerRadius(): Dp {
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
