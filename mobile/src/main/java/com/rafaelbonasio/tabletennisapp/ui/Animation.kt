package com.rafaelbonasio.tabletennisapp.ui

import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.togetherWith

val noneTransitionSpec = EnterTransition.None.togetherWith(ExitTransition.None)

val asymmetricBoundsTransform = BoundsTransform { initialBounds, targetBounds ->
    val isExpanding = targetBounds.width > initialBounds.width

    if (isExpanding) {
        spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        )
    } else {
        spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow)
    }
}
