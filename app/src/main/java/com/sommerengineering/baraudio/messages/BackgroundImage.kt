package com.sommerengineering.baraudio.messages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.times
import com.sommerengineering.baraudio.uitls.darkModeAlpha
import com.sommerengineering.baraudio.uitls.fabPadding
import com.sommerengineering.baraudio.uitls.lightModeAlpha

@Composable
fun BoxScope.BackgroundImage(
    iconRes: Int,
    isDarkMode: Boolean) {

    Image(
        painter = painterResource(iconRes),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .align(Alignment.Center)
            .padding(2 * fabPadding))

    // scrim overlay
    Box(
        modifier = Modifier
            .matchParentSize()
            .background(MaterialTheme.colorScheme.background.copy(
                if (isDarkMode) darkModeAlpha else lightModeAlpha)))
}