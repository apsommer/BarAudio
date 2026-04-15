package com.sommerengineering.baraudio.message

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.sommerengineering.baraudio.uitls.assetIconSize

@Composable
fun MessageItemIcon(iconRes: Int) {

    Box(
        Modifier
            .size(assetIconSize)
            .clip(CircleShape)
            .background(Color.Transparent)) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = null,
            modifier = Modifier.fillMaxSize())
    }
}

@Composable
fun AssetTextIcon(modifier: Modifier = Modifier) {
    
}