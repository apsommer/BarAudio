package com.sommerengineering.baraudio.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun AppTheme(
    content: @Composable () -> Unit) {

    MaterialTheme(
        colorScheme = darkScheme,
        typography = AppTypography,
        content = content)
}