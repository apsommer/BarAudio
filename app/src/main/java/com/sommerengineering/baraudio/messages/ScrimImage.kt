package com.sommerengineering.baraudio.messages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource

@Composable
fun BoxScope.ScrimImage(
    iconRes: Int,
    alpha: Float,
    modifier: Modifier = Modifier) {

    Image(
        painter = painterResource(iconRes),
        contentDescription = null,
        modifier = modifier)

    // scrim overlay
    Box(
        modifier = Modifier
            .matchParentSize()
            .background(MaterialTheme.colorScheme.background.copy(alpha)))
}