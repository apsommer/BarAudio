package com.sommerengineering.baraudio.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

const val uiModeFadeTimeMillis = 2000

@Composable
fun AppTheme(
    isDarkMode: Boolean,
    content: @Composable () -> Unit) {

    // toggle dark mode
    val colorScheme =
        if (isDarkMode) darkScheme
        else lightScheme

    MaterialTheme(
        colorScheme = colorScheme.switch(),
        typography = AppTypography,
        content = content)
}

@Composable
fun animateColor(target: Color) =
    animateColorAsState(
        targetValue = target,
        animationSpec = tween(
            durationMillis = uiModeFadeTimeMillis))
        .value

@Composable
fun ColorScheme.switch() =
    copy(
        primary = animateColor(primary),
        onPrimary = animateColor(onPrimary),
        primaryContainer = animateColor(primaryContainer),
        onPrimaryContainer = animateColor(onPrimaryContainer),
        secondary = animateColor(secondary),
        onSecondary = animateColor(onSecondary),
        secondaryContainer = animateColor(secondaryContainer),
        onSecondaryContainer = animateColor(onSecondaryContainer),
        tertiary = animateColor(tertiary),
        onTertiary = animateColor(onTertiary),
        tertiaryContainer = animateColor(tertiaryContainer),
        onTertiaryContainer = animateColor(onTertiaryContainer),
        error = animateColor(error),
        onError = animateColor(onError),
        errorContainer = animateColor(errorContainer),
        onErrorContainer = animateColor(onErrorContainer),
        background = animateColor(background),
        onBackground = animateColor(onBackground),
        surface = animateColor(surface),
        onSurface = animateColor(onSurface),
        surfaceVariant = animateColor(surfaceVariant),
        onSurfaceVariant = animateColor(onSurfaceVariant),
        outline = animateColor(outline),
        outlineVariant = animateColor(outlineVariant),
        scrim = animateColor(scrim),
        inverseSurface = animateColor(inverseSurface),
        inverseOnSurface = animateColor(inverseOnSurface),
        inversePrimary = animateColor(inversePrimary),
        surfaceDim = animateColor(surfaceDim),
        surfaceBright = animateColor(surfaceBright),
        surfaceContainerLowest = animateColor(surfaceContainerLowest),
        surfaceContainerLow = animateColor(surfaceContainerLow),
        surfaceContainer = animateColor(surfaceContainer),
        surfaceContainerHigh = animateColor(surfaceContainerHigh),
        surfaceContainerHighest = animateColor(surfaceContainerHighest))

