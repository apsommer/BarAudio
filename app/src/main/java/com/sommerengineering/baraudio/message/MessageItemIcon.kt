package com.sommerengineering.baraudio.message

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sommerengineering.baraudio.theme.fontFamily
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
